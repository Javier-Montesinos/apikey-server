package com.montesinos.securedbyheadertoken.server.domain;

public class ApiKey {
	
	private String username;	
	private String uuid;
	private String salt;
	private String hashedUuid;
	private String apiScope;
	private boolean active;
	
	/**
	 * 
	 */
	public ApiKey() {
		
	}

	/**
	 * @param uuid
	 * @param salt
	 * @param hashedUuid
	 * @param active
	 */
	public ApiKey(String uuid, String salt, String hashedUuid, boolean active) {
		super();
		this.uuid = uuid;
		this.salt = salt;
		this.hashedUuid = hashedUuid;
		this.active = active;
	}

	/**
	 * @param username
	 * @param uuid
	 * @param salt
	 * @param hashedUuid
	 * @param active
	 */
	public ApiKey(String username, String uuid, String salt, String hashedUuid, boolean active) {
		super();
		this.username = username;
		this.uuid = uuid;
		this.salt = salt;
		this.hashedUuid = hashedUuid;
		this.active = active;
	}
	
	

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getHashedUuid() {
		return hashedUuid;
	}

	public void setHashedUuid(String hashedUuid) {
		this.hashedUuid = hashedUuid;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}	

	public String getApiScope() {
		return apiScope;
	}

	public void setApiScope(String apiScope) {
		this.apiScope = apiScope;
	}

	@Override
	public String toString() {
		return "ApiKey [username=" + username + ", uuid=" + uuid + ", salt=" + salt.toString() + ", hashedUuid="
				+ hashedUuid + ", active=" + active + "]";
	}
	
}
