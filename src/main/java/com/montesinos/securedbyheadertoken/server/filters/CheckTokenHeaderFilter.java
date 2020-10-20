package com.montesinos.securedbyheadertoken.server.filters;

import java.io.IOException;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Order(1)
public class CheckTokenHeaderFilter implements Filter {

	private final static Logger LOG = LoggerFactory.getLogger(CheckTokenHeaderFilter.class);
	
	@Value("${apikey.user.name}")
	private final String apiKeyUserName = null;
	
	@Value("${apikey.user.value}")
	private String apiKeyUserValue;
		
	@Value("${apikey.name}")
	private final String apiKeyName = null;
	
	@Value("${apikey.value}")
	private String apiKeyValue;
		
	@Value("${apikey.scope}")
	private final String apiScope = null;	
	
	@Value("${apikey.manager.url}")
	private final String apiKeyManagerUrl = null;
	
	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		LOG.info("Initializing filter :{}", this);						 
	}	

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
				
		String apiKey = req.getHeader(this.apiKeyName);
		String userName = req.getHeader(this.apiKeyUserName);
		
		LOG.info("req : {}, user name: {}, api key value: {}", req.getRequestURI(), userName, apiKey);
					
		if(authApiKey(userName, apiKey) == true) {
			chain.doFilter(request, response);
			
		} else {		
			res.sendError(HttpStatus.NOT_FOUND.value(), "Recurso no disponible");
			
		}	
		
	}
	
	@Override
	public void destroy() {
		LOG.warn("Destructing filter :{}", this);
	}
	
	/**
	 * Valida contra el apikey-manager si el usuario y apikey son correctos
	 * @param userName
	 * @param apiKey
	 * @return
	 */
	private boolean authApiKey(String userName, String apiKey) {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();        
        headers.add(this.apiKeyUserName, this.apiKeyUserValue);
        headers.add(this.apiKeyName, this.apiKeyValue);
        
		String urlAuthenticateKey= this.apiKeyManagerUrl + "/auth/" + this.apiScope + "/" + userName + "/" + apiKey;
		ResponseEntity<String> keyAuthResponse = restTemplate.exchange(urlAuthenticateKey, 
				HttpMethod.GET, new HttpEntity<Object>(headers), String.class);		

		return Boolean.valueOf(keyAuthResponse.getBody()).booleanValue();
	}
}
