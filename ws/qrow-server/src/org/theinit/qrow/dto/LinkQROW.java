package org.theinit.qrow.dto;

import org.theinit.qrow.server.QROWSender;


public class LinkQROW extends QROW {

	public LinkQROW(String senderId,String title, String link,byte[] thumbnail) {
		super(); 
		this.senderId=senderId;
		this.title=title;					
		this.data=link;
		this.thumbnail=thumbnail;
		this.type=QROW.TYPE_LINK;
		this.version=""+QROWSender.VERSION;
	}

	public LinkQROW(String senderId,String title, String link) {
		this(senderId,title,link,null); 
		
	}

	
}
