# Cloudinary Setup Guide for My Bakery

This guide will help you set up Cloudinary for image uploads in your bakery application.

## What is Cloudinary?

Cloudinary is a cloud-based service that provides solutions for image and video management. It offers:
- Image upload and storage
- Image transformations (resize, crop, filters, etc.)
- CDN delivery for fast loading
- Automatic optimization

## Setup Steps

### 1. Create a Cloudinary Account

1. Go to [https://cloudinary.com/](https://cloudinary.com/)
2. Click "Sign Up" and create a free account
3. Verify your email address

### 2. Get Your Cloudinary Credentials

1. Log in to your Cloudinary dashboard
2. Go to the "Dashboard" section
3. Copy your credentials:
   - **Cloud Name**
   - **API Key**
   - **API Secret**

### 3. Configure Your Application

1. Open `src/main/resources/application.properties`
2. Replace the placeholder values with your actual Cloudinary credentials:

```properties
# Cloudinary Configuration
cloudinary.cloud-name=your-actual-cloud-name
cloudinary.api-key=your-actual-api-key
cloudinary.api-secret=your-actual-api-secret
```

### 4. Environment Variables (Recommended for Production)

For production environments, use environment variables instead of hardcoding credentials:

```properties
# Cloudinary Configuration
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME}
cloudinary.api-key=${CLOUDINARY_API_KEY}
cloudinary.api-secret=${CLOUDINARY_API_SECRET}
```

Then set these environment variables:
```bash
export CLOUDINARY_CLOUD_NAME=your-cloud-name
export CLOUDINARY_API_KEY=your-api-key
export CLOUDINARY_API_SECRET=your-api-secret
```

## Features Included

### Image Upload
- Drag and drop interface
- Multiple image upload (up to 5 images per product)
- File type validation (images only)
- File size validation (max 5MB)
- Progress indicators

### Image Management
- Automatic image optimization
- Responsive image transformations
- Secure URLs
- Easy deletion

### Image Transformations
- Automatic resizing to 800x600 for main images
- Thumbnail generation (150x150)
- Medium size generation (400x300)
- Quality optimization

## API Endpoints

The following endpoints are available for image management:

### Upload Single Image
```
POST /api/upload/product-image
Content-Type: multipart/form-data
Body: file (image file)
```

### Upload Multiple Images
```
POST /api/upload/product-images
Content-Type: multipart/form-data
Body: files (multiple image files)
```

### Delete Image
```
DELETE /api/upload/product-image/{publicId}
```

## Usage in Frontend

### Adding Images to Products
1. Go to the Products page
2. Click "Add Product" or edit an existing product
3. Use the drag-and-drop area or click to browse files
4. Upload up to 5 images
5. The first image will be used as the main product icon

### Image Preview
- Images are displayed as thumbnails in the upload area
- Click the "X" button to remove individual images
- Images are automatically optimized and stored in Cloudinary

## Security Considerations

1. **API Key Security**: Never commit your API secret to version control
2. **File Validation**: Only image files are accepted
3. **File Size Limits**: Maximum 5MB per file
4. **Upload Limits**: Maximum 5 images per product

## Troubleshooting

### Common Issues

1. **Upload Fails**: Check your Cloudinary credentials
2. **Images Not Displaying**: Verify the URLs are accessible
3. **Large File Uploads**: Ensure files are under 5MB

### Debug Mode

To enable debug logging, add this to your `application.properties`:
```properties
logging.level.com.aurelius.tech.mybakery.service.CloudinaryService=DEBUG
```

## Cost Considerations

Cloudinary offers a generous free tier:
- 25 GB storage
- 25 GB bandwidth per month
- 25,000 transformations per month

For most small to medium bakeries, this should be sufficient.

## Support

If you encounter issues:
1. Check the Cloudinary documentation: [https://cloudinary.com/documentation](https://cloudinary.com/documentation)
2. Review the application logs
3. Verify your credentials are correct
4. Ensure your Cloudinary account is active 