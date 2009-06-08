package net.sf.meteor.transaction;

import com.googlecode.meteorframework.core.annotation.Model;

@Model public interface TransactionService
{
	public UnitOfWork getUnitOfWork();
}
