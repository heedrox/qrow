package org.theinit.qrow.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;

import org.theinit.qrow.dto.QROW;
import org.theinit.qrow.exception.InvalidQROWConfiguration;

public class QROWSender {

	public static final int VERSION=1;

	public static final String DEFAULT_MCAST_GROUP="239.255.14.92"; //1492 = QROW
	public static final int DEFAULT_QROW_PORT=51492; //1492 = QROW
	public static final long TIME_ANNOUNCEMENTS_MILLISECS=10000;	
	public static final int DEFAULT_TTL=1;

	public static final int QROW_MAXLENGTH=32768;

	public static final byte[] QROW_HELO = new byte[] { 0x51, 0x52, 0x4F, 0x57 };

	private int port;	
	private QROW qrow;




	private boolean shouldFinish=false;

	public QROWSender(int port) {
		super();
		this.port = port;
	}

	public QROWSender() {
		super();
		this.port=QROWSender.DEFAULT_QROW_PORT;
	}


	/**
	 * QROWSender sends QROWs; elemental Wifi QR information. After creating a QROWSender, 
	 * you must add one QROW to it. You can use addQROW to add it. 
	 * Once you add the QROW you want to send, you execute the QROWSender.run() method. 
	 * @param qrow
	 */
	public void addQROW(QROW qrow) {
		this.qrow=qrow;
	}

	/**
	 * Synonym for QROWSender.addQROW(QROW qrow)
	 * @param qrow
	 */
	public void setQROW(QROW qrow) {
		addQROW(qrow);
	}

	/**
	 * Main method to startSending. Before calling this method, you must set a senderId, and addQROWs to the sender (minimun: one).
	 * Once done, you start sending. 
	 * If you want to stop, you call to stopSending().
	 * @throws InvalidQROWConfiguration
	 */
	public void startSending() throws InvalidQROWConfiguration {
		if (!checkValid()) { 
			System.err.println("Error in QROWSender configuration. First use setSenderId, and then addQROWs to it. ");
			throw new InvalidQROWConfiguration();
		}
		new senderThread().start();
	}

	/**
	 * Stops sending QROW announcements. Might take a little while until stops.  
	 */
	public void stopSending() {
		this.shouldFinish=true;
	}


	/**
	 * Checks validity of the QROW to send.
	 * Required fields are senderId, title, type and data.
	 * @return true if ok, false if not .
	 */
	private boolean checkValid() {
		if (qrow==null) { return false; }
		if (qrow.getSenderId()==null) { return false; }
		if (qrow.getTitle()==null) { return false; }
		if (qrow.getType()==null) { return false; }
		if (qrow.getData()==null) { return false; }
		if ("".equals(qrow.getData().trim())) { return false; }
		if ("".equals(qrow.getType().trim())) { return false; }
		if ("".equals(qrow.getTitle().trim())) { return false; }
		if ("".equals(qrow.getSenderId().trim())) { return false; }
		return true;
	}


	/**
	 * This Thread is responsible of sending UDP broadcast datagrams with the
	 * QROW information. 
	 * 
	 * @author INIT
	 *
	 */
	private class senderThread extends Thread {


		@Override
		public void run() {
			QROWSender.this.shouldFinish=false;
			while (true) {
				send();
				try {
					if (QROWSender.this.shouldFinish) { return; }
					Thread.sleep(QROWSender.TIME_ANNOUNCEMENTS_MILLISECS);
					if (QROWSender.this.shouldFinish) { return; }
				} catch (InterruptedException e) {
					return;
				}
			}

		}

		/**
		 * Sends the announcements of the QRs 
		 * It serializes with a json, and sends it through UDP broadcast Datagrams.
		 * Default port is 14920. It is advisable not to change it.
		 */
		private void send() {
			/* BROADCAST MODE - BETTER NOT 
			try {

				while (true) {
					DatagramChannel dc=DatagramChannel.open();
					dc.socket().setBroadcast(true);

					String dts=QROWSender.this.qrow.toJSON();
					ByteBuffer data=QROW.TEXT_CHARSET.encode(dts);
					//TODO Pillar la excepcion, y avisar de que no hay red
					//FIXME lo mejor seria encontrar un broadcast address, de hecho, si lo conecto a mi hotspot, da error
					//TODO probar con varias interfaces, ¿se broadcastea en todas?
					//TODO probar que pasa cuando se te corta la red, y vuelve. O cuando estas broadcasting en una (wifi), y enchufas una nueva (eth0), ¿se empieza a broadcastear en esa?
					int resultado=dc.send(data, new InetSocketAddress("255.255.255.255",QROWSender.this.port));
					System.out.println("Enviado... "+dts+" - length: "+dts.length()+" vs. "+resultado);
					dc.close();
					Thread.sleep(QROWSender.TIME_ANNOUNCEMENTS_MILLISECS);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

			try {

				//TODO Pillar la excepcion, y avisar de que no hay red
				//TODO probar con varias interfaces, ¿se broadcastea en todas?
				//TODO probar que pasa cuando se te corta la red, y vuelve. O cuando estas broadcasting en una (wifi), y enchufas una nueva (eth0), ¿se empieza a broadcastear en esa?

				while (true) {
					MulticastSocket socket = new MulticastSocket();
					String dts=QROWSender.this.qrow.toJSON();
					ByteBuffer data=QROW.TEXT_CHARSET.encode(dts);
					byte[] arprezData=data.array();
					byte[] zData=zipBytes(arprezData);
					byte[] totalData=concatArray(QROWSender.QROW_HELO,zData);

					System.out.println("Enviadno: "+new String(totalData));
					DatagramPacket packet = new DatagramPacket(totalData, totalData.length, InetAddress.getByName(QROWSender.DEFAULT_MCAST_GROUP), QROWSender.DEFAULT_QROW_PORT);
					socket.setTimeToLive(QROWSender.DEFAULT_TTL);
					socket.send(packet);
					socket.close() ;
					Thread.sleep(QROWSender.TIME_ANNOUNCEMENTS_MILLISECS);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private byte[] concatArray(byte[] a, byte[] b) {
			byte[] c = new byte[a.length + b.length];
			System.arraycopy(a, 0, c, 0, a.length);
			System.arraycopy(b, 0, c, a.length, b.length);
			return c;
		}


		private byte[] zipBytes(byte[] input) throws IOException {
			// From exampleDepot.com; THX
			// Create the compressor with highest level of compression
			Deflater compressor = new Deflater();
			compressor.setLevel(Deflater.BEST_COMPRESSION);

			// Give the compressor the data to compress
			compressor.setInput(input);
			compressor.finish();

			// Create an expandable byte array to hold the compressed data.
			// You cannot use an array that's the same size as the orginal because
			// there is no guarantee that the compressed data will be smaller than
			// the uncompressed data.
			ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

			// Compress the data
			byte[] buf = new byte[1024];
			while (!compressor.finished()) {
				int count = compressor.deflate(buf);
				bos.write(buf, 0, count);
			}
			try {
				bos.close();
			} catch (IOException e) {
			}

			// Get the compressed data
			byte[] compressedData = bos.toByteArray();
			return compressedData;
		}

	}

}
