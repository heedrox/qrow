package org.theinit.qrow.dto;

import org.theinit.util.Base64Util;

public class ImageQROW extends QROW {

	public ImageQROW(String senderId,String title, byte[] image,byte[] thumbnail) {
		super(); 
		this.senderId=senderId;
		this.title=title;					
		this.data=Base64Util.encode(image);
		this.thumbnail=thumbnail;
		this.type=QROW.TYPE_IMAGE;
	}

	public ImageQROW(String senderId,String title, byte[] image) {
		this(senderId,title,image,null); 
	}

	
}
