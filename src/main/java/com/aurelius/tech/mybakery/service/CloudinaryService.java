package com.aurelius.tech.mybakery.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CloudinaryService {
    
    /**
     * Upload a single image to Cloudinary
     * 
     * @param file the image file to upload
     * @return Map containing upload result with URL and public ID
     * @throws IOException if upload fails
     */
    Map<String, Object> uploadImage(MultipartFile file) throws IOException;
    
    /**
     * Upload multiple images to Cloudinary
     * 
     * @param files list of image files to upload
     * @return List of Maps containing upload results
     * @throws IOException if upload fails
     */
    List<Map<String, Object>> uploadImages(List<MultipartFile> files) throws IOException;
    
    /**
     * Delete an image from Cloudinary
     * 
     * @param publicId the public ID of the image to delete
     * @return Map containing deletion result
     * @throws IOException if deletion fails
     */
    Map<String, Object> deleteImage(String publicId) throws IOException;
    
    /**
     * Get image URL with transformations
     * 
     * @param publicId the public ID of the image
     * @param transformations transformation parameters
     * @return transformed image URL
     */
    String getImageUrl(String publicId, Map<String, Object> transformations);
} 