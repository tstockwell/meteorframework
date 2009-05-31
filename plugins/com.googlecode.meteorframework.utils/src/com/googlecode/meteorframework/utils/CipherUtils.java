package com.googlecode.meteorframework.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtils
{
	private static String __Key= "x903,g8q";
    private static SecretKeySpec __sksSpec = new SecretKeySpec(__Key.getBytes(), "DES");

     /**
	   * encrypt
	   */
	  public static byte[] encrypt(byte[] data) {
	    try
		{
			//Get Key From Out
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, __sksSpec);
			byte[] encrypted = cipher.doFinal(data);
			return encrypted;
		} 
	    catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
	  }
	 
	  /**
	   * decrypt
	   */
	  public static byte[] decrypt(byte[] encrypted) {    
	    try
		{
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(javax.crypto.Cipher.DECRYPT_MODE, __sksSpec);
			byte[] decrypted = cipher.doFinal(encrypted);
			return decrypted;
		} 
	    catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
	  }

}
