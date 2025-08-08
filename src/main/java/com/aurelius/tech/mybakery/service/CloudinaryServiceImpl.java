package com.aurelius.tech.mybakery.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryServiceImpl.class);
    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
        logger.info("CloudinaryServiceImpl initialized with cloudinary instance: {}", cloudinary != null ? "present" : "null");
    }

    @Override
    public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
        logger.info("Starting upload for file: {}", file.getOriginalFilename());
        
        if (file == null || file.isEmpty()) {
            logger.error("File is null or empty");
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        logger.info("File content type: {}", contentType);
        if (contentType == null || !contentType.startsWith("image/")) {
            logger.error("Invalid file type: {}", contentType);
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Validate file size (max 20MB)
        long fileSize = file.getSize();
        logger.info("File size: {} bytes", fileSize);
        if (fileSize > 20 * 1024 * 1024) {
            logger.error("File too large: {} bytes", fileSize);
            throw new IllegalArgumentException("File size must be less than 20MB");
        }

        try {
            // Simplified upload parameters without transformation
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", "my-bakery/products",
                "resource_type", "image",
                "allowed_formats", new String[]{"jpg", "jpeg", "png", "gif", "webp"},
                "overwrite", true,
                "invalidate", true
            );
            
            logger.info("Upload parameters: {}", uploadParams);
            logger.info("Attempting to upload to Cloudinary...");
            
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            
            logger.info("Upload successful. Result keys: {}", result.keySet());
            return result;
            
        } catch (Exception e) {
            logger.error("Error during Cloudinary upload: {}", e.getMessage(), e);
            // Provide more specific error messages
            if (e.getMessage().contains("Invalid transformation")) {
                throw new RuntimeException("Invalid image transformation parameters: " + e.getMessage());
            } else if (e.getMessage().contains("Invalid API credentials")) {
                throw new RuntimeException("Invalid Cloudinary API credentials: " + e.getMessage());
            } else if (e.getMessage().contains("File format not supported")) {
                throw new RuntimeException("File format not supported by Cloudinary: " + e.getMessage());
            } else {
                throw new RuntimeException("Failed to upload image to Cloudinary: " + e.getMessage());
            }
        }
    }

    @Override
    public List<Map<String, Object>> uploadImages(List<MultipartFile> files) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                results.add(uploadImage(file));
            }
        }
        
        return results;
    }

    @Override
    public Map<String, Object> deleteImage(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    @Override
    public String getImageUrl(String publicId, Map<String, Object> transformations) {
        try {
            return cloudinary.url()
                    .transformation(new com.cloudinary.Transformation().params(transformations))
                    .generate(publicId);
        } catch (Exception e) {
            logger.error("Error generating image URL for publicId: {} with transformations: {}", publicId, transformations, e);
            // Return the original URL if transformation fails
            return cloudinary.url().generate(publicId);
        }
    }
    
    /**
     * Get a thumbnail URL for a product image
     * 
     * @param publicId the public ID of the image
     * @return thumbnail URL
     */
    public String getThumbnailUrl(String publicId) {
        try {
            return cloudinary.url()
                    .transformation(new com.cloudinary.Transformation()
                            .width(150)
                            .height(150)
                            .crop("fill")
                            .quality("auto"))
                    .generate(publicId);
        } catch (Exception e) {
            logger.error("Error generating thumbnail URL for publicId: {}", publicId, e);
            return cloudinary.url().generate(publicId);
        }
    }
    
    /**
     * Get a medium-sized URL for a product image
     * 
     * @param publicId the public ID of the image
     * @return medium-sized URL
     */
    public String getMediumUrl(String publicId) {
        try {
            return cloudinary.url()
                    .transformation(new com.cloudinary.Transformation()
                            .width(400)
                            .height(300)
                            .crop("fill")
                            .quality("auto"))
                    .generate(publicId);
        } catch (Exception e) {
            logger.error("Error generating medium URL for publicId: {}", publicId, e);
            return cloudinary.url().generate(publicId);
        }
    }
    
    /**
     * Get a large-sized URL for a product image
     * 
     * @param publicId the public ID of the image
     * @return large-sized URL
     */
    public String getLargeUrl(String publicId) {
        try {
            return cloudinary.url()
                    .transformation(new com.cloudinary.Transformation()
                            .width(800)
                            .height(600)
                            .crop("limit")
                            .quality("auto"))
                    .generate(publicId);
        } catch (Exception e) {
            logger.error("Error generating large URL for publicId: {}", publicId, e);
            return cloudinary.url().generate(publicId);
        }
    }
} 