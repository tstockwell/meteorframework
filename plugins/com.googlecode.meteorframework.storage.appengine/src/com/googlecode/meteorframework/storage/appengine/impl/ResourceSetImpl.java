package com.googlecode.meteorframework.storage.appengine.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.Map;

import com.googlecode.meteorframework.core.MeteorNotFoundException;
import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.storage.ResourceSet;
import com.googlecode.meteorframework.storage.StorageSession;

import simpleorm.utils.SException;
import simpleorm.utils.SLog;
/*
 * Copyright (c) 2002 Southern Cross Software Queensland (SCSQ).  All rights
 * reserved.  See COPYRIGHT.txt included in this distribution.
 */


/**
 * A DataSet contains records of various types together with their meta data.  
 * The records are indexed by their type and primary keys, and so can be efficiently retrieved.
 * A list of dirty records that need to be updated is also maintained.<p>
 *
 * A DataSet is normally associated with a SSession/Jdbc, and is accessed indirectly via the SSessionJdbc methods.
 * But a DataSet can also be accessed directly when it is detached from the SSession.<p> 
 * @author aberglas
 */
public class ResourceSetImpl implements ResourceSet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, Resource> _resources = new LinkedHashMap<String, Resource>();
    	
	/** 
	 * Ordered list of resources to flush and purge. 
     * NEEDED to maintain the Order of the flushing for foreign key updates.
     */
	private ArrayList<Object> _dirtyResources = new ArrayList<Object>();
   
    private transient StorageSession _session;
    
	@Override 
	public <T> T findByURI(Class<T> type, String uri)
	{
		Resource resource= _resources.get(uri);
		if (resource == null)
			return null;
		return resource.castTo(type);
	}
	
	@Override 
	public void attach(Object resource)
	{
		String uri= ((Resource)resource).getURI();
		if (_resources.get(uri) == null)
		{
			_resources.put(uri, ((Resource)resource));
		}
	}
	
	@Override 
	public void detach(Object resource)
	{
		String uri= ((Resource)resource).getURI();
		if (_resources.get(uri) == null)
		{
			_resources.put(uri, ((Resource)resource));
		}
	}

        
    /** Used if no key is known, and one is to be provided later. */
    public <RI extends SRecordInstance> RI createWithNullKey(SRecordMeta<RI> rmeta) {
		RI instance = rmeta.newRecordInstance();
        instance.nullPrimaryKeys();
        instance.setDataSet(this); // needs to be done now for logging.
        instance.setNewRow(true);
        instance.setDirty(true);
		records.put(instance, instance);
        return instance;
	}

    /** Remove record from cache, does not flag it for deletion. */
    public void removeRecord(SRecordInstance rinst) {
        //System.err.println("RemoveRecord " + rinst + rinst.updateListIndex);
		records.remove(rinst);
        if (rinst.updateListIndex >= 0)
            dirtyRecords.remove(rinst.updateListIndex);
        rinst.destroy();        
	}

    public Collection<SRecordInstance> queryAllRecords() {
		return records.values();
	}
    
    /** Return all records in dataset where rec.ref == ref.  
     * Ie. the inverse of findRecrod.
     */
    public <I extends SRecordInstance, R extends SRecordInstance> List<R> queryReferencing(
        I refed, SFieldReference<I> ref) {
        if (ref.getReferencedRecordMeta() != refed.getMeta())
            throw new SException.Error("Reference " + ref + " does not reference a " + refed);
        ArrayList<R> res = new ArrayList();
          for (SRecordInstance rec: queryAllRecords()) {
              if (rec.getMeta() == ref.getRecordMeta()) {
                  if (rec.findReference(ref) == refed)
                      res.add((R)rec);
              }                  
          }
        return res;
    }

	public boolean isAttached() {
		return session != null;
	}
	
    
	// Should be done on each record, one by one
	public void clearDirtyList() {
		getDirtyRecords().clear();
	}
    
   /** Ordered list of SRecordInstances to flush and purge. */
   public List<SRecordInstance> getDirtyRecords() {
        return dirtyRecords;
    }

   /** Remove all records from cache and update list. */
    public void purge() {
        for (SRecordInstance ri : records.values()) {
            ri.destroy();
        }
        records.clear();
        clearDirtyList();
    }
    
   	public void destroy() {
        purge();
        records = null;
        dirtyRecords = null;
	}
    
    /** Clone the dataset.
     * Useful befor attempting to attach and commit to recover from errors.
     * (See LongTransactionTest).<p>
     * 
     * Curent implementation uses slow serialization alg, should really use clone()/able.
     */
    public SDataSet clone() {
        Object original = this;
        SDataSet clone = null;
        try {
            //Increased buffer size to speed up writing  
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(original);
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(bos.toByteArray()));
            clone =(SDataSet)in.readObject();
            in.close();
            bos.close();

            return clone;
        } catch (Exception es) {
            throw new SException.Error("While cloning " + original, es);
        }
    }


   ////////////////////// These should ideally all be package local, not accessed even from simpleorm.database ////////////////////

    /**
     * Create a new instance of this record, used by SSession during the fiddly doFindOrCreate etc.<p>
     */
    @Deprecated public <RI extends SRecordInstance> RI newInstanceNotInDataSet(SRecordMeta<RI> rmeta) {
        return rmeta.newRecordInstance();
    }
        
	@Deprecated public <RI extends SRecordInstance> RI findUsingPrototype(RI rinst) {
//        System.err.println(">ds findfromcache " + rinst + rinst.hashCode());
//        dumpDataSet();
		RI result = (RI) records.get(rinst);
		if (result != null)
			result.setWasInCache(true);
//        System.err.println("<ds findfromcache " + result);
		return (RI)result;
	}
    	
	@Deprecated public void pokeIntoDataSet(SRecordInstance rinst) {
		SRecordInstance prev = records.put(rinst, rinst);
		rinst.setDataSet(this);
	}


	void putInDirtyList(SRecordInstance rinst) {
		if (rinst.getDataSet() != this) {
			throw new SException.Error("Inconsistant DataSet");
		}
        if (rinst.updateListIndex != -1) 
            throw new SException.InternalError("Already in dirty list " + rinst);
		this.getDirtyRecords().add(rinst);
		rinst.updateListIndex =  getDirtyRecords().size() -1;
	}
	
	void removeFromDirtyList(int index, SRecordInstance rinst) {
		if (rinst.getDataSet() != this) {
			throw new SException.Error("Inconsistant DataSet");
		}
		if (getDirtyRecords().get(index) != rinst) {
			throw new SException.Error("Inconsistant updateList index");
		}
		this.getDirtyRecords().set(index, null);
	}
	
    public SLog getLogger() {
         if (logger == null) {
             logger = SLog.newSLog(); // eg. after unserialize.
             logger.setSession(getSession());
         }
        return logger;
    }
    
    /**
     * Create a new instance of this record, unattached to a dataset.
     * Used for backward compatibility, records normally ONLY live within
     * a DataSet.  
     * Detached records are marked as new.
     * @See SDataSet#attach
     */

    @Deprecated static public <RI extends SRecordInstance> RI createDetachedInstance(SRecordMeta<RI> rmeta) {
        RI inst = rmeta.newRecordInstance();
        inst.setNewRow(true);
        return inst;
    }
    
    /** Attach rinst into the dataset, checking that it is not already there. 
     * References should be null.
     * Only for compatibility with 2.*, normally records only normal created within data sets.
     */
   	@Deprecated public void attach(SRecordInstance rinst) {
		SRecordInstance prev = records.put(rinst, rinst);
        if (prev != null) 
            throw new SException.Error("Record already in dataset " + prev);
		rinst.setDataSet(this);
        if (rinst.isDirty()) // normal
            putInDirtyList(rinst);
	}

    
    
   	/**
	 * Dumps out the entire cache of records for this connection. For debugging
	 * wierd bugs only.
	 */
	public void dumpDataSet() {
		getLogger().message("DumpDataSet " + queryAllRecords().size() + " records.");
		for (SRecordInstance ri : queryAllRecords()) {
			getLogger().message("    " + ri + ri.hashCode());
            if (ri.getDataSet() != this)
                throw new SException.InternalError("Wrong DataSet "+ ri + ri.getDataSet());
        }
        //getLogger().message("    Dirty " + dirtyRecords);
	}

    public String toString() {
        return "[SDataSet " + getSession() + "]";
    }
    
   //////////////////////////// Empty get/set /////////////////////////////////
   
    public SSessionI getSession() {
        return session;
    }

    public void setSession(SSessionI session) {
        this.session = session;
    }    

    
}
