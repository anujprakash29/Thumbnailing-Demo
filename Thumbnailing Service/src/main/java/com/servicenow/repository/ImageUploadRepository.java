package com.servicenow.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

@Component
public class ImageUploadRepository {
	
	public void uploadImages(List<MultipartFile> files) {
		try {

			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("imagedb");
			DBCollection collection = db.getCollection("dummyColl");
			
			GridFS gfsPhoto = new GridFS(db, "images");
			for (MultipartFile file : files) {
				GridFSDBFile imageForOutput = gfsPhoto.findOne(file.getOriginalFilename());
				if (imageForOutput != null ) {
					System.out.println("File already exists: " + file.getOriginalFilename());
					continue;
				}
				
				GridFSInputFile gfsFile = gfsPhoto.createFile(file.getBytes());
				gfsFile.setFilename(file.getOriginalFilename());
				gfsFile.save();
			}

		} catch (MongoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteImages() {
		
		try {

			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("imagedb");
			DBCollection collection = db.getCollection("dummyColl");
			
			GridFS gfsPhoto = new GridFS(db, "images");
			
			DBCursor cursor = gfsPhoto.getFileList();
			while (cursor.hasNext()) {
				gfsPhoto.remove(cursor.next());
			}

		} catch (MongoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveThumbnails(InputStream is, String out) {
		try {

			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("imagedb");
			DBCollection collection = db.getCollection("dummyColl");
			
			GridFS gfsPhoto = new GridFS(db, "thumbs");
			GridFSDBFile imageForOutput = gfsPhoto.findOne(out);
			if (imageForOutput != null ) {
				System.out.println("File already exists: " + out);
				return;
			}
			
			GridFSInputFile gfsFile = gfsPhoto.createFile(is);
			gfsFile.setFilename(out);
			DBCursor cursor = gfsPhoto.getFileList();
			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}
			gfsFile.save();
			

		} catch (MongoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteThumbnails() {
		
		try {

			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("imagedb");
			DBCollection collection = db.getCollection("dummyColl");
			
			GridFS gfsPhoto = new GridFS(db, "thumbs");
			
			DBCursor cursor = gfsPhoto.getFileList();
			while (cursor.hasNext()) {
				gfsPhoto.remove(cursor.next());
			}

		} catch (MongoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> getImageList() {
		
		try {

			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("imagedb");
			DBCollection collection = db.getCollection("dummyColl");
			
			GridFS gfsPhoto = new GridFS(db, "thumbs");
			
			List<String> filenames = new ArrayList<String>();
			DBCursor cursor = gfsPhoto.getFileList();
			while (cursor.hasNext()) {
				filenames.add(cursor.next().get("filename").toString());
			}
			
			return filenames;
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public InputStream getImage(String name) {
		
		try {

			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("imagedb");
			DBCollection collection = db.getCollection("dummyColl");			
			GridFS gfsPhoto = new GridFS(db, "images");
			
			GridFSDBFile imageForOutput = gfsPhoto.findOne(name+".jpg");
			
			if(imageForOutput == null) return null;
			return imageForOutput.getInputStream();

		} catch (MongoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public InputStream getThumbnail(String name) {
		try {

			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("imagedb");
			DBCollection collection = db.getCollection("dummyColl");			
			GridFS gfsPhoto = new GridFS(db, "thumbs");
			
			GridFSDBFile imageForOutput = gfsPhoto.findOne(name+".jpg");
			
			if(imageForOutput == null) return null;
			return imageForOutput.getInputStream();

		} catch (MongoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
