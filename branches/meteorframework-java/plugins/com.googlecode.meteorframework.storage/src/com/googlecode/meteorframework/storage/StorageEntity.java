package com.googlecode.meteorframework.storage;

import com.googlecode.meteorframework.core.Resource;
import com.googlecode.meteorframework.core.annotation.DefaultValue;
import com.googlecode.meteorframework.core.annotation.ExtensionOf;
import com.googlecode.meteorframework.core.annotation.IsWriteOnce;
import com.googlecode.meteorframework.core.annotation.ModelElement;

/**
 * An object that has been, or will be, persisted.
 * @author Ted Stockwell
 */
@ModelElement
@ExtensionOf(Resource.class)
public interface StorageEntity {
	
	long getStartRevision();
	@IsWriteOnce void setStartRevision(long revisionOrderNumber);

	@DefaultValue("-1")
	long setEndRevision();
	@IsWriteOnce void setEndRevision(long revisionOrderNumber);
}
