package com.googlecode.meteorframework.storage.triplestore.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.googlecode.meteorframework.storage.StorageException;


public class TripleStoreUtils {

	public static SimpleDateFormat ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	public static SimpleDateFormat RFC822DATEFORMAT = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);

	public static String getDateAsRFC822String(Date date)
	{
		return RFC822DATEFORMAT.format(date);
	}

	public static String getDateAsISO8601String(Date date)
	{
		String result = ISO8601FORMAT.format(date);
		//convert YYYYMMDDTHH:mm:ss+HH00 into YYYYMMDDTHH:mm:ss+HH:00
		//- note the added colon for the Timezone
		result = result.substring(0, result.length()-2)
		+ ":" + result.substring(result.length()-2);
		return result;
	}	

	public static Date getDateFromISO8601String(String string)
	{
		try {
			if (string == null)
				return null;
			int i= string.lastIndexOf(':');
			if (0 <= i) {
				string= string.substring(0, i)+string.substring(i+1);
			}
			return ISO8601FORMAT.parse(string);
		} catch (ParseException e) {
			throw new StorageException(e);
		}
	}

}
