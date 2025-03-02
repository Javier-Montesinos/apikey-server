package com.montesinos.securedbyheadertoken.server.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/saludo")
public class HelloRestController {

	@GetMapping("/hello")
	public String hello() {
		return "Hello world !";
	}
}
