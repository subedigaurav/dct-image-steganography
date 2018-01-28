package com.gasu.disteg.misc;

public class Test {
	 //Message to be sent
    
    private static byte[] convertMessageToByte(String message) {
        byte[] messageByteArray = message.getBytes();
        return messageByteArray;
    }
    
    public static void main(String[] args) {
    	String message="This is Stego Message!!!";
        byte[] messageByteArray=convertMessageToByte(message);
        for (int i = 0; i < messageByteArray.length; i++) {
			System.out.println(messageByteArray[i]);
		}
	}
}
