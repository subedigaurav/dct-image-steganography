package com.gasu.disteg.algos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import javax.xml.bind.DatatypeConverter;

public class BlowfishAlgorithm 
{
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	// Converts byte array to hex string
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	static String encrypt(String stegoKey) throws Exception
	{
		// Configuration
		byte[] key	= "secret".getBytes();
		String IV  	= "12345678";

		System.out.println("-- Settings -----------");
		System.out.println("KEY:\t " + bytesToHex(key));
		System.out.println("IV:\t " + bytesToHex(IV.getBytes()));
		
		// Create new Blowfish cipher
		SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish"); 
		Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding"); 
		
		String secret = stegoKey;
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, new javax.crypto.spec.IvParameterSpec(IV.getBytes())); 
		byte[] encoding = cipher.doFinal(secret.getBytes());
		
		System.out.println("-- Encrypted -----------");
		System.out.println("Base64:\t " + DatatypeConverter.printBase64Binary(encoding));
		System.out.println("HEX:\t " + bytesToHex(encoding));
		
		String encrypted=DatatypeConverter.printBase64Binary(encoding);
		return encrypted;
	}
	
	static String decrypt(String encryptedKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		// Configuration
		byte[] key	= "secret".getBytes();
		String IV  	= "12345678";
		
		// Decode Base64
		byte[] ciphertext = DatatypeConverter.parseBase64Binary(encryptedKey);

		//Decrypt 
		SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");
		Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, keySpec, new javax.crypto.spec.IvParameterSpec(IV.getBytes()));
		byte[] message = cipher.doFinal(ciphertext);

		System.out.println("-- Decrypted -----------");
		System.out.println("HEX:\t " + bytesToHex(message));
		System.out.println("PLAIN:\t " + new String(message));
		
		return new String(message);
	}
	
	
	public static void main(String[] args) throws Exception {
		System.out.println("Enter the message");
		InputStreamReader istream = new InputStreamReader(System.in) ;
        BufferedReader bufRead = new BufferedReader(istream);
        String msg=bufRead.readLine();
		String mmm=encrypt(msg);
		System.out.println(mmm);
		decrypt(mmm);
	}
	}