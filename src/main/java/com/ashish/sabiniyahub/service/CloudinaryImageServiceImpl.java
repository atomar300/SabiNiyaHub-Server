package com.ashish.sabiniyahub.service;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryImageServiceImpl implements CloudinaryImageService{

    @Autowired
    private Cloudinary cloudinary;


    public Map upload(byte [] file, String folder) {
        try {
            Map data = this.cloudinary.uploader().upload(file, Map.of("folder", folder));
            return data;
        } catch (IOException e) {
            throw new RuntimeException("Image uploading failed");
        }
    }


    public void delete(String public_id, String folder){
        try {
            this.cloudinary.uploader().destroy(public_id, Map.of("folder", folder));
        } catch (IOException e) {
            throw new RuntimeException("Image deletion failed!!!");
        }
    }
}
