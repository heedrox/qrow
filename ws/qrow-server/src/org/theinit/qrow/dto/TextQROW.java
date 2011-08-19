package org.theinit.qrow.dto;


public class TextQROW extends QROW {

	public TextQROW(String senderId,String title, String text,byte[] thumbnail) {
		super();
		this.senderId=senderId;
		this.title=title;					
		this.data=text;
		this.thumbnail=thumbnail;
		this.type=QROW.TYPE_TEXT;
	}

	public TextQROW(String senderId,String title, String text) {
		this(senderId,title,text,null); 
	}

	
}
