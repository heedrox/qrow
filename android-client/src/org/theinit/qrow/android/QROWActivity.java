package org.theinit.qrow.android;

import org.theinit.qrow.client.QROWReceiver;
import org.theinit.qrow.client.QROWReceiverCallback;
import org.theinit.qrow.dto.QROW;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class QROWActivity extends Activity {
   
	QROWReceiver qrow=null;
	
	Handler androidHandler=new Handler() {

		@Override
		public void handleMessage(Message msg) {
			TextView tv=(TextView)QROWActivity.this.findViewById(R.id.txtLabel);
			tv.setText(msg.getData().getString("msg"));
		}
		
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        qrow=new QROWReceiver(new QROWReceiverCallback() {
			
			public void processQROW(QROW qrow) {
				Message msg=new Message();
				Bundle data=new Bundle();
				data.putString("msg",qrow.getType()+" - "+qrow.getTitle()+" - "+qrow.getData());
				msg.setData(data);				
				androidHandler.sendMessage(msg);				
			}
		});
        qrow.startReceiving();
        
        
    }

	@Override
	protected void onPause() {
		super.onPause();
		if (qrow!=null) {
			qrow.stopReceiving();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (qrow!=null) {
			qrow.startReceiving();
		}
	}
    
    
    
}