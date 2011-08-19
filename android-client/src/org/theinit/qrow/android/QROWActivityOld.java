package org.theinit.qrow.android;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class QROWActivityOld extends Activity {
    
	public static int QROW_PORT=14920;
	
	String resultado;
	public long contador=0;
	
	Handler h=new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			TextView tv=(TextView)QROWActivityOld.this.findViewById(R.id.txtLabel);
			//Log.d("Recogido","Recogido: "+msg.getData().getString("data"));
			//tv.setText(msg.getData().getString("data"));
			Log.d("data","Recogido: "+QROWActivityOld.this.resultado);
			tv.setText((contador++)+" - "+QROWActivityOld.this.resultado);
			try {
				tv.setText("OK JSON - "+(contador)+" - "+QROWActivityOld.this.resultado);
				JSONObject o=new JSONObject(QROWActivityOld.this.resultado);
				System.out.println(o.get("bundle").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				tv.setText("ERROR JSON - "+(contador)+" - "+QROWActivityOld.this.resultado);
			}
		}
		
	};
	
	Thread lector=new Thread() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				DatagramChannel dc=DatagramChannel.open();
				dc.socket().setBroadcast(true);
				dc.socket().bind(new InetSocketAddress(QROW_PORT));
				//dc.connect(new InetSocketAddress(QROW_PORT));
				
				Charset utf8=Charset.forName("UTF-8");
				while (true) {
					//TODO permitir leer mas de 1024 caracteres. OJO, quizas no sea mala idea limitar a 10K los mensajes, para que la gente no inunde las wifi.
					//se pueden lanzar datagramas de mas de X caracteres? parece que no, ¿por que? entiendo que porque no hay forma de saber
					Log.d("dp","Leyendo...");
					byte[] buf=new byte[1024];
					DatagramPacket p=new DatagramPacket(buf,1024);
					//ByteBuffer b=ByteBuffer.allocate(1024);
					dc.socket().receive(p);
					
					String totalData=utf8.decode(ByteBuffer.wrap(p.getData())).toString().trim();
					
					
					//ByteBuffer b=ByteBuffer.wrap(buf);
					//System.out.println(utf8.decode(b).toString().trim());
					Message msg=new Message();
					//Bundle data=new Bundle();
					//data.putString("data",utf8.decode(b).toString().trim());
					//msg.setData(data);
					QROWActivityOld.this.resultado=totalData;
					h.sendMessage(msg);
					
				}
			} catch (Exception e) {
				e.printStackTrace();				
			}
		}
		
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        lector.start();
        
        
    }
    
}