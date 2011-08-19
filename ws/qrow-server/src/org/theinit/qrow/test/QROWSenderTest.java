package org.theinit.qrow.test;

import org.theinit.qrow.dto.LinkQROW;
import org.theinit.qrow.server.QROWSender;

public class QROWSenderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			QROWSender qs=new QROWSender();
			qs.addQROW(new LinkQROW("IDENTIFICADOR","Un código QR en la WIFI", "http://www.google.es"));
			QROWSender qs2=new QROWSender();
			qs2.addQROW(new LinkQROW("IDENTIFICADOR2","Un código 2 QR en la WIFI", "http://www.google.es"));
			QROWSender qs3=new QROWSender();
			qs3.addQROW(new LinkQROW("IDENTIFICADOR3","Un código 3 QR en la WIFI", "http://www.google.es"));
			qs.startSending();
			qs2.startSending();
			qs3.startSending();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}





}
