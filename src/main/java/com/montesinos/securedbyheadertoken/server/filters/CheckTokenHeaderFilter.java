package com.montesinos.securedbyheadertoken.server.filters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.montesinos.securedbyheadertoken.server.domain.ApiKey;

@Component
@Order(1)
public class CheckTokenHeaderFilter implements Filter {

	private final static Logger LOG = LoggerFactory.getLogger(CheckTokenHeaderFilter.class);
	
	@Value("${apikey.user.name}")
	private final String apiKeyUserName = null;
	
	@Value("${apikey.name}")
	private final String apiKeyName = null;
	
	@Value("${apikey.scope}")
	private final String apiScope = null;
	
	@Value("${apikey.manager.url}")
	private final String apiKeyManagerUrl = null;
	
	/**
	 * Almacena en memoria las keys para dar agilidad a las peticiones
	 */
	private Map<String, ApiKey> keys;
		
	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		LOG.info("Initializing filter :{}", this);
		
		this.loadKeys();				  
	}	

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
				
		String apiKey = req.getHeader(this.apiKeyName);
		String userName = req.getHeader(this.apiKeyUserName);
		
		LOG.info("req : {}, user name: {}, api key value: {}", req.getRequestURI(), userName, apiKey);

		if(!this.authenticateKey(apiScope, userName, apiKey)) {				
			res.sendError(HttpStatus.NOT_FOUND.value(), "Recurso no disponible");
		}
		
		chain.doFilter(request, response);
	}
	
	@Override
	public void destroy() {
		LOG.warn("Destructing filter :{}", this);
	}
	
	private void loadKeys() {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ApiKey[]> response = restTemplate.getForEntity(this.apiKeyManagerUrl, ApiKey[].class);
		ApiKey[] apiKeys = response.getBody();
		
		keys = new HashMap<String, ApiKey>();
		for(int i = 0; i < apiKeys.length;i++) {
			keys.put(apiKeys[i].getUsername(), apiKeys[i]);
		}
	}
	
	private boolean authenticateKey(String apiScope, String userName, String keyUuid) {
		boolean retValue = false;
		if(this.keys.containsKey(userName)) {
			ApiKey keyBD = this.keys.get(userName);

			MessageDigest md = null;
			byte[] hashedPassword = null;
			try {
				md = MessageDigest.getInstance("SHA-256");				
				md.update(Base64.getDecoder().decode(keyBD.getSalt().getBytes("UTF-8")));
				
				hashedPassword = md.digest(keyUuid.getBytes(StandardCharsets.UTF_8));
				
				if(keyBD.getApiScope().equals(apiScope) 
						&& keyBD.getHashedUuid().equals(byteArrayToHexadecimal(hashedPassword))) {
					retValue = true;
				} 
				
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return retValue;
	}
	
	private String byteArrayToHexadecimal(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i< bytes.length ;i++){
	        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
		return sb.toString();
	}

}
