package com.servicenow.service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import com.servicenow.repository.ImageUploadRepository;

@CrossOrigin
@Component
public class ImageUploadService {
    
    @Autowired
	private ImageUploadRepository imageRepo;
    
    private static final String imageBaseURL = "http://localhost:8081/images/";

	public void saveUploadedFiles(List<MultipartFile> files) throws IOException {
		
		for (MultipartFile file: files)
        {
            Thread thread = new Thread(new ThumbnailGenerator(file));
            thread.start();
        }
		imageRepo.uploadImages(files);
	}

	public void deleteImages() {
		
		imageRepo.deleteThumbnails();
		imageRepo.deleteImages();
		System.out.println("Files Deleted Successfully");
	}	

	public List<Map<String, String>> getImageList() {
		
		List<String> filenames = imageRepo.getImageList();
		List<Map<String, String>> imageList = new ArrayList<Map<String,String>>();
		for (String name : filenames) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("filename", name);
			map.put("thumburl", imageBaseURL + name.substring(0, name.lastIndexOf(".")));
			map.put("imageurl", imageBaseURL + name.substring(0, name.lastIndexOf(".")-6));
			imageList.add(map);
		}
		return imageList;
	}
	
	public InputStream getImage(String name) {
		
		return name.contains("_thumb")? imageRepo.getThumbnail(name) : imageRepo.getImage(name);
	}

}

class ThumbnailGenerator extends Thread
{
    private MultipartFile file;
        
	public ThumbnailGenerator(MultipartFile file) {
		this.file = file;
	}
	
	public void run()
    {
        try {
            saveThumbnails(file); 
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private void saveThumbnails(MultipartFile files) throws IOException {
		
		String name = file.getOriginalFilename();
		BufferedImage bi = createThumbnails(ImageIO.read(file.getInputStream()), 128, 128);
        String ext = name.substring(name.lastIndexOf(".")+1);
        String out = name.replaceFirst(".([a-z]+)$", "_thumb." + ext);
        System.out.println(name + " --> " + out);
        
//        File thumbnail = new File("thumbs\\",  out);
//        thumbnail.createNewFile(); 
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, ext, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
//        ImageIO.write(bi, ext, thumbnail);
        
        ImageUploadRepository imageRepo = new ImageUploadRepository();
		imageRepo.saveThumbnails(is, out);
				
	}
	
	private BufferedImage createThumbnails(BufferedImage in, int w, int h) {
        // scale w, h to keep aspect constant
        double outputAspect = 1.0*w/h;
        double inputAspect = 1.0*in.getWidth()/in.getHeight();
        if (outputAspect < inputAspect) {
            // width is limiting factor; adjust height to keep aspect
            h = (int)(w/inputAspect);
        } else {
            // height is limiting factor; adjust width to keep aspect
            w = (int)(h*inputAspect);
        }
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(in, 0, 0, w, h, null);
        g2.dispose();
        return bi;
    }

}
