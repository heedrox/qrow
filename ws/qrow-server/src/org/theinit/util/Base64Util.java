package org.theinit.util;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64Util {

	/**
	 * Returns a Base64 Encoded String. It is a wrapper in case Base64Encoder from sun doesnt exist in the VM of the client
	 * @param data
	 * @return
	 */
	public static String encode(byte[] data) {
		//BASE64Encoder.
		if (data==null) { return null; }
		BASE64Encoder b64=new BASE64Encoder();
		return b64.encode(data);
	}
	
	public static byte[] decode(String data) {
		//BASE64Encoder.
		if (data==null) { return null; }
		BASE64Decoder b64=new BASE64Decoder();
		try {
			return b64.decodeBuffer(data);
		} catch (IOException e) {		
			e.printStackTrace();
		}
		return null;
	}
}
