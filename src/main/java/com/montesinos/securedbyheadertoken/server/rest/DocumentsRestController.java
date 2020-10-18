package com.montesinos.securedbyheadertoken.server.rest;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.montesinos.securedbyheadertoken.server.domain.Document;

@RestController
@RequestMapping("/documents")
public class DocumentsRestController {

	@PostMapping("/download")
	public List<Document> downloadDocuments(@RequestBody List<Document> documents) {
		for(int i = 0; i < documents.size(); i++) {
			documents.get(i).setContentBase64("contenido del documento con id: " + documents.get(i).getId() + 
					" de tipo: " + documents.get(i).getType());
		}
		return documents;
	}
}
