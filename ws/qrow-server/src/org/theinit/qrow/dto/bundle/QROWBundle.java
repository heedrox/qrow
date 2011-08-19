package org.theinit.qrow.dto.bundle;

import java.util.Collection;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.theinit.qrow.dto.QROW;
import org.theinit.qrow.exception.InvalidSenderIdException;

public class QROWBundle extends HashMap<String,QROW>{



	String senderId;
	String version;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1159467281685159507L;

	/**
	 * Returns the sender id for this bundle
	 * @return
	 */
	public String getSenderId() {
		return senderId;
	}

	
	
	public String toJSON() {
		JSONObject qj=new JSONObject();
		JSONArray arr=new JSONArray();
		Collection<QROW> qs=this.values();
		for (QROW q : qs) {
			arr.add(q.toJSONObject());
		}		
		qj.put("bundle", arr);
		return qj.toString();		
	}



}
