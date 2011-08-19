package org.theinit.qrow.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.theinit.qrow.dto.QROW;
import org.theinit.qrow.server.QROWSender;

public class QROWReceiver {

	private int port;

	private QROWReceiverCallback receiverCallback;

	private boolean shouldFinish=false;

	public QROWReceiver(int port, QROWReceiverCallback receiverCallback) {
		super();
		this.port = port;
		this.receiverCallback = receiverCallback;
	}

	public QROWReceiver(QROWReceiverCallback receiverCallback) {
		this(QROWSender.DEFAULT_QROW_PORT,receiverCallback);
	}

	public void startReceiving() {
		new ReceiverThread().start();
	}

	public void stopReceiving() {
		shouldFinish=true;
	}

	public String receive() {
		return "";
	}
	private class ReceiverThread extends Thread {

		public void run() {
			QROWReceiver.this.shouldFinish=false;
			MulticastSocket socket=null;
			InetAddress group=null;
			try {				
				socket = new MulticastSocket(port);
				group=InetAddress.getByName(QROWSender.DEFAULT_MCAST_GROUP);


				// join the multicast group
				socket.joinGroup(group);
				socket.setSoTimeout(2000);


				while (true) {

					System.out.println("En bucle");
					//dc.connect(new InetSocketAddress(QROW_PORT));				
					Charset utf8=Charset.forName("UTF-8");
					//TODO permitir leer mas de 1024 caracteres. OJO, quizas no sea mala idea limitar a 10K los mensajes, para que la gente no inunde las wifi.
					//se pueden lanzar datagramas de mas de X caracteres? parece que no, ¿por que? entiendo que porque no hay forma de saber
					byte[] buf=new byte[QROWSender.QROW_MAXLENGTH];
					DatagramPacket p=new DatagramPacket(buf,buf.length);
					//ByteBuffer b=ByteBuffer.allocate(1024);
					try {
						socket.receive(p);					

						byte[] data=p.getData();

						if (hasQROWBytes(data)) {

							byte[] newData=Arrays.copyOfRange(data, QROWSender.QROW_HELO.length,data.length );
							byte[] uzData=unzipBytes(newData);
							String qrtxt=utf8.decode(ByteBuffer.wrap(uzData)).toString().trim();
							receiverCallback.processQROW(QROW.fromJSON(qrtxt));	
						} else {
							//System.out.println("silently discarded a non qrow packet: "+data);
							//receiverCallback.notAQROW(QROW.fromJSON(qrtxt));
						}
					} catch (SocketTimeoutException ste) {
						//Check if shouldfinish is set. If not, then we keep on 
						if (shouldFinish) { 
							socket.leaveGroup(group);
							socket.close();
							return; 
						}
					}


				}


			} catch (Exception e) {
				// TODO Auto-generated catch block
				if (socket!=null) {
					try {
						socket.leaveGroup(group);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					socket.close();
				}

				e.printStackTrace();
			}


		}

		private boolean hasQROWBytes(byte[] b) {
			if (b.length<QROWSender.QROW_HELO.length) { return false; } 
			for (int a=0;a<QROWSender.QROW_HELO.length;a++) {
				if (b[a]!=QROWSender.QROW_HELO[a]) { return false; }
			}
			return true;
		}

		private byte[] unzipBytes(byte[] compressedData) {
			// Create the decompressor and give it the data to compress
			Inflater decompressor = new Inflater();
			decompressor.setInput(compressedData);

			// Create an expandable byte array to hold the decompressed data
			ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);

			// Decompress the data
			byte[] buf = new byte[1024];
			while (!decompressor.finished()) {
				try {
					int count = decompressor.inflate(buf);
					bos.write(buf, 0, count);
				} catch (DataFormatException e) {
				}
			}
			try {
				bos.close();
			} catch (IOException e) {
			}

			// Get the decompressed data
			byte[] decompressedData = bos.toByteArray();
			return decompressedData;

		}
	}


}
