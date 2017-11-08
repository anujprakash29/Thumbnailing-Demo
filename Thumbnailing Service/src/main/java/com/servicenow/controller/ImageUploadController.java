package com.servicenow.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.servicenow.service.ImageUploadService;

@Controller
public class ImageUploadController {
	    
    @Autowired
	private ImageUploadService imageService;
    
    private static final String homepage = "http://localhost:8080";
    private static final String imagesURL = "http://localhost:8080/images";
    
    @PostMapping("/uploadimage")
    public String uploadImages( @RequestParam("files") MultipartFile[] uploadfiles,
    		RedirectAttributes redirectAttributes) {
    	
    	String uploadedFileName = Arrays.stream(uploadfiles).map(x -> x.getOriginalFilename())
                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));
    	
        if (StringUtils.isEmpty(uploadedFileName)) {
        	redirectAttributes.addFlashAttribute("message",
                    "No Files Selected" );
        	return "redirect:" + homepage;
        }

        try {

        	imageService.saveUploadedFiles(Arrays.asList(uploadfiles));

        } catch (IOException e) {
        	redirectAttributes.addFlashAttribute("message",
                    "An error occured!" );
        	return "redirect:" + homepage;
        }
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + uploadedFileName + "!");
        return "redirect:" + homepage;

    }
    
    @PostMapping("/deleteimages")
    public String deleteImages(RedirectAttributes redirectAttributes) {
    	
    	try {

        	imageService.deleteImages();

        } catch (Exception e) {
        	redirectAttributes.addFlashAttribute("message",
                    "An error occured!" );
        	return "redirect:" + homepage;
        }
        redirectAttributes.addFlashAttribute("message",
                "All Files Removed Successfully ");
        return "redirect:" + homepage;
    }
    
    @GetMapping(value = "/imagelist", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getImageList() {
    	
    	JSONObject result = new JSONObject();
    	List<Map<String, String>> list = imageService.getImageList();
    	result.put("images", list);
    	return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
    	
    }    
    
    @GetMapping(value = "/images/{imagename}", produces="image/jpg")
    public ResponseEntity<byte[]> getImageAsByteArray(@PathVariable("imagename") String name) throws IOException {
    	
    	InputStream in = imageService.getImage(name);
    	return new ResponseEntity<byte[]>(in==null? null : IOUtils.toByteArray(in), HttpStatus.OK);
    	
    }

}
