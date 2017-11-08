package com.servicenow.controller;

import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class ImageUploadController {
    
    @GetMapping("/")
    public String home() {
        return "home";
    }
    
    @GetMapping("/images")
    public String images() {
        return "images";
    }
    
    @GetMapping(value = "/imagelist", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getImageList() {
    	
    	RestTemplate template = new RestTemplate();
    	return template.getForEntity("http://localhost:8081/imagelist", JSONObject.class);
    	
    }
    

}
