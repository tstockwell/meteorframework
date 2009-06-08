package com.googlecode.meteorframework.storage.triplestore.impl;

import java.math.BigDecimal;
import java.util.Date;

import com.googlecode.meteorframework.core.annotation.Decorator;
import com.googlecode.meteorframework.core.utils.ConversionService;
import com.truemesh.squiggle.Literal;
import com.truemesh.squiggle.literal.BigDecimalLiteral;
import com.truemesh.squiggle.literal.BooleanLiteral;
import com.truemesh.squiggle.literal.DateTimeLiteral;
import com.truemesh.squiggle.literal.FloatLiteral;
import com.truemesh.squiggle.literal.IntegerLiteral;
import com.truemesh.squiggle.literal.StringLiteral;

@Decorator abstract public class ConversionServiceImpl 
implements ConversionService
{
	public Literal convert(String value, Class<Literal> type)
	{
		return new StringLiteral(value);
	}
	public Literal convert(Integer value, Class<Literal> type)
	{
		return new IntegerLiteral(value);
	}
	public Literal convert(Float value, Class<Literal> type)
	{
		return new FloatLiteral(value);
	}
	public Literal convert(Boolean value, Class<Literal> type)
	{
		return new BooleanLiteral(value);
	}
	public Literal convert(BigDecimal value, Class<Literal> type)
	{
		return new BigDecimalLiteral(value);
	}
	public Literal convert(Date value, Class<Literal> type)
	{
		return new DateTimeLiteral(value);
	}
}
