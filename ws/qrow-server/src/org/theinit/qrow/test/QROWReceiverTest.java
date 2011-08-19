package org.theinit.qrow.test;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;

import org.theinit.qrow.client.QROWReceiver;
import org.theinit.qrow.client.QROWReceiverCallback;
import org.theinit.qrow.dto.QROW;
import org.theinit.qrow.server.QROWSender;

public class QROWReceiverTest {

	public static void main(String[] args) {
		QROWReceiver receiver=new QROWReceiver(new QROWReceiverCallback() {

			@Override
			public void processQROW(QROW qrow) {
				// TODO Auto-generated method stub
				System.out.println(qrow.toString());
			}
		});
		
		receiver.startReceiving();


	}


}
