package com.ashish.sabiniyahub.service;


import java.util.Map;

public interface CloudinaryImageService {

    public Map upload(byte[] file, String folder);

    public void delete(String public_id, String folder);
}
