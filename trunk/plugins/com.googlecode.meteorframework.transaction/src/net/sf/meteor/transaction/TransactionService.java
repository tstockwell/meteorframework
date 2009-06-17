package net.sf.meteor.transaction;

import com.googlecode.meteorframework.core.annotation.ModelElement;

@ModelElement public interface TransactionService
{
	public UnitOfWork getUnitOfWork();
}
