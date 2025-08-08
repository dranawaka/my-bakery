package com.aurelius.tech.mybakery.controller;

import com.aurelius.tech.mybakery.dto.ApiResponse;
import com.aurelius.tech.mybakery.service.CloudinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ImageUploadController {

    private static final Logger logger = LoggerFactory.getLogger(ImageUploadController.class);
    private final CloudinaryService cloudinaryService;

    @Autowired
    public ImageUploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Upload a single image for a product
     */
    @PostMapping("/product-image")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadProductImage(
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        logger.info("Upload request received. File: {}", file != null ? file.getOriginalFilename() : "null");
        
        try {
            // Validate file parameter
            if (file == null) {
                logger.warn("No file provided in upload request");
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No file provided"));
            }
            
            // Validate file
            if (file.isEmpty()) {
                logger.warn("Empty file provided in upload request");
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Please select a file to upload"));
            }

            // Check file type
            String contentType = file.getContentType();
            logger.info("File content type: {}", contentType);
            if (contentType == null || !contentType.startsWith("image/")) {
                logger.warn("Invalid file type: {}", contentType);
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Only image files are allowed"));
            }

            // Check file size (max 20MB)
            long fileSize = file.getSize();
            logger.info("File size: {} bytes", fileSize);
            if (fileSize > 20 * 1024 * 1024) {
                logger.warn("File too large: {} bytes", fileSize);
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("File size must be less than 20MB"));
            }

            logger.info("Attempting to upload file to Cloudinary: {}", file.getOriginalFilename());
            
            // Upload to Cloudinary
            Map<String, Object> result = cloudinaryService.uploadImage(file);
            
            logger.info("File uploaded successfully to Cloudinary. Result: {}", result);
            
            // Extract relevant information
            Map<String, Object> response = new HashMap<>();
            String url = (String) result.get("url");
            String secureUrl = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");
            Integer width = (Integer) result.get("width");
            Integer height = (Integer) result.get("height");
            
            logger.info("Extracted values - url: {}, secureUrl: {}, publicId: {}, width: {}, height: {}", 
                       url, secureUrl, publicId, width, height);
            
            response.put("url", url);
            response.put("publicId", publicId);
            response.put("secureUrl", secureUrl);
            response.put("width", width);
            response.put("height", height);
            
            return ResponseEntity.ok(ApiResponse.success(response, "Image uploaded successfully"));
            
        } catch (IOException e) {
            logger.error("IOException during upload: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to upload image: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during upload: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to upload image: " + e.getMessage()));
        }
    }

    /**
     * Upload multiple images for a product
     */
    @PostMapping("/product-images")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> uploadProductImages(
            @RequestParam("files") List<MultipartFile> files) {
        
        try {
            // Validate files
            if (files.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Please select files to upload"));
            }

            // Check each file
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error("One or more files are empty"));
                }

                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Only image files are allowed"));
                }

                if (file.getSize() > 20 * 1024 * 1024) {
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error("File size must be less than 20MB"));
                }
            }

            // Upload to Cloudinary
            List<Map<String, Object>> results = cloudinaryService.uploadImages(files);
            
            // Extract relevant information for each result
            List<Map<String, Object>> response = results.stream()
                .map(result -> {
                    String url = (String) result.get("url");
                    String secureUrl = (String) result.get("secure_url");
                    String publicId = (String) result.get("public_id");
                    Integer width = (Integer) result.get("width");
                    Integer height = (Integer) result.get("height");
                    
                    logger.info("Multiple upload - url: {}, secureUrl: {}, publicId: {}", url, secureUrl, publicId);
                    
                    Map<String, Object> item = new HashMap<>();
                    item.put("url", url);
                    item.put("publicId", publicId);
                    item.put("secureUrl", secureUrl);
                    item.put("width", width);
                    item.put("height", height);
                    return item;
                })
                .toList();
            
            return ResponseEntity.ok(ApiResponse.success(response, "Images uploaded successfully"));
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to upload images: " + e.getMessage()));
        }
    }

    /**
     * Delete an image from Cloudinary
     */
    @DeleteMapping("/product-image/{publicId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteProductImage(
            @PathVariable String publicId) {
        
        try {
            Map<String, Object> result = cloudinaryService.deleteImage(publicId);
            return ResponseEntity.ok(ApiResponse.success(result, "Image deleted successfully"));
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to delete image: " + e.getMessage()));
        }
    }
} 