package net.sf.meteor.transaction;

import com.googlecode.meteorframework.core.annotation.Model;

@Model public interface UnitOfWork
{
	public void begin() throws TransactionException;

	public void commit() throws TransactionException;
	
	public void rollback() throws TransactionException;

}
