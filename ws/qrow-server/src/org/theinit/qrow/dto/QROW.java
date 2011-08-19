package org.theinit.qrow.dto;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.theinit.qrow.exception.InvalidSenderIdException;
import org.theinit.qrow.exception.QROWTypeNotSupportedException;
import org.theinit.util.Base64Util;

public abstract class QROW {

	public static Charset TEXT_CHARSET=Charset.forName("UTF-8");

	private static int MAXLENGTH_SENDERID=16;

	public static String TYPE_LINK="url";
	public static String TYPE_IMAGE="image";
	public static String TYPE_TEXT="text";
	public static String TYPE_CUSTOM="custom";	
	//FUTURE
	//public static String TYPE_INTENT=""; //webintent, android intent, iphone intent... They could open a concrete app with a concrete ID or selected item (sending parameters). Maybe they could be used with url

	protected String type;
	protected String title;
	protected byte[] thumbnail;
	protected Calendar lastModified;
	protected String data;
	protected String metadata;
	protected String senderId;
	protected String version;


	public QROW() {
		super();
		this.lastModified=Calendar.getInstance();
	}


	/**
	 * Sets the sender id of the bundle. It must be max of 16 characters, 
	 * and must match [0-9a-zA-Z ]{1,16} regexp
	 * @param senderId
	 * @throws InvalidSenderIdException
	 */
	public void setSenderId(String senderId) throws InvalidSenderIdException{
		if (senderId==null) { return; } 
		if (!senderId.matches("[0-9a-zA-Z ]{1,16}")) { 
			throw new InvalidSenderIdException();
		}
		if (senderId.length()>QROW.MAXLENGTH_SENDERID) {
			throw new InvalidSenderIdException();
		}
		this.senderId = senderId;
	}


	public String getSenderId() { 
		return this.senderId; 
	}

	public void setThumbnail(byte[] image) {
		this.thumbnail=image;		
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public Calendar getLastModified() {
		return lastModified;
	}


	public void setLastModified(Calendar lastModified) {
		this.lastModified = lastModified;
	}




	public String getData() {
		return data;
	}


	public void setData(String data) {
		this.data = data;
	}


	public byte[] getThumbnail() {
		return thumbnail;
	}


	public String getMetadata() {
		return metadata;
	}


	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}




	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	/**
	 * Retrieves the JSONObject
	 * @return
	 */
	public JSONObject toJSONObject() {
		JSONObject o=new JSONObject();
		o.put("type", this.type);
		o.put("title",this.title);
		o.put("thumbnail", Base64Util.encode(this.thumbnail));
		o.put("lastModified", this.lastModified==null?null:this.lastModified.getTimeInMillis());
		o.put("data",this.data);
		o.put("senderId", this.getSenderId());
		o.put("version", this.version);
		o.put("metadata",this.metadata);
		return o;
	}

	/**
	 * Retrieves the String that represents the JSONObject
	 * @return
	 */
	public String toJSON() {
		return this.toJSONObject().toString();
	}


	@Override
	public String toString() {
		return "QROW [type=" + type + ", title=" + title + ", thumbnail="
		+ Arrays.toString(thumbnail) + ", lastModified=" + lastModified
		+ ", data=" + data + ", metadata=" + metadata + ", senderId="
		+ senderId + ", version=" + version + "]";
	}



	public static QROW fromJSON(String data) throws Exception {
		QROW q;

		JSONObject o=(JSONObject)(new JSONParser().parse(new StringReader(data)));
		String type=(String)o.get("type");

		if (QROW.TYPE_CUSTOM.equals(type)) {
			q=new CustomQROW();
			if (o.get("title")!=null) { q.setVersion((String)o.get("title")); }
			if (o.get("senderId")!=null) { q.setSenderId((String)o.get("senderId")); }
			if (o.get("data")!=null) { q.setData((String)o.get("data")); }

		} else if (QROW.TYPE_IMAGE.equals(type)) {			
			q=new ImageQROW((String)o.get("senderId"), (String)o.get("title"), Base64Util.decode((String)o.get("data")));		
		} else if (QROW.TYPE_LINK.equals(type)) {			
			q=new LinkQROW((String)o.get("senderId"), (String)o.get("title"), (String)o.get("data"));			
		} else if (QROW.TYPE_TEXT.equals(type)) {
			q=new TextQROW((String)o.get("senderId"), (String)o.get("title"), (String)o.get("data"));
		} else {
			throw new QROWTypeNotSupportedException();
		}

		if (o.get("thumbnail")!=null) { q.setThumbnail(Base64Util.decode((String)o.get("thumbnail"))); }
		if (o.get("version")!=null) { q.setVersion((String)o.get("version")); }
		if (o.get("metadata")!=null) { q.setMetadata((String)o.get("metadata")); }
		if (o.get("lastModified")!=null) { 
			Calendar c=Calendar.getInstance();
			c.setTimeInMillis((Long)o.get("lastModified"));
			q.setLastModified(c);			
		}

		return q;

	}


}

