package org.cycleourcity.cyclelourcity_web_server.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.cycleourcity.cyclelourcity_web_server.middleware.datalayer.exceptions.UnableToPerformOperation;
import org.joda.time.DateTime;

public class SecurityUtils {
	
	private static final Random r = new SecureRandom();
	
	public static byte[] generateSalt(int byteSize){
		byte[] salt = new byte[byteSize];
		r.nextBytes(salt);
		return salt;
	}
	
	public static byte[] getSecureHash(int iterations, String password, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(salt);
		byte[] hash = digest.digest(password.getBytes("UTF-8"));
		for(int i=0; i < iterations; i++){ //Hardens brute-force attacks
			digest.reset();
			hash = digest.digest(hash);
		}
		
		return hash;
	}
	
	public static Date getExpirationDate(int days){
		Date dt = new Date();
		DateTime dtOrg = new DateTime(dt);
		DateTime dtPlusOne = dtOrg.plusDays(days);
		return dtPlusOne.toDate();
	}
	
	public static String generateSecureActivationToken(int size) throws UnsupportedEncodingException{
		byte[] token = new byte[size];
		r.nextBytes(token);
		return new String(token, "UTF-8");
	}
	
	public static byte[] hashSHA1(String s) throws NoSuchAlgorithmException{
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		return digest.digest(s.getBytes());
	}
}
