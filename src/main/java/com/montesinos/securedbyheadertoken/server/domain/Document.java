package com.montesinos.securedbyheadertoken.server.domain;

public class Document {

	private int type;
	private String id;
	private String contentBase64;
	
	/**
	 * 
	 */
	public Document() {
		
	}

	/**
	 * @param type
	 * @param id
	 */
	public Document(int type, String id) {
		super();
		this.type = type;
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContentBase64() {
		return contentBase64;
	}

	public void setContentBase64(String contentBase64) {
		this.contentBase64 = contentBase64;
	}		
	
}
