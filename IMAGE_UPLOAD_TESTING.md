# Image Upload Testing Guide

This guide will help you test the image upload functionality in your My Bakery application.

## Prerequisites

1. **Application Running**: Make sure your Spring Boot application is running on `http://localhost:8080`
2. **Cloudinary Configuration**: Verify your Cloudinary credentials are correctly set in `application.properties`
3. **Browser**: Use a modern browser (Chrome, Firefox, Safari, Edge)

## Testing Steps

### 1. Test the Upload API Directly

First, let's test if the backend API is working correctly:

#### Using the Test Page
1. Open your browser and go to: `http://localhost:8080/test-upload.html`
2. This page provides a standalone test for the image upload functionality
3. Try uploading an image by:
   - Clicking the upload area to browse files
   - Dragging and dropping an image file
4. Check the browser console for any error messages
5. Verify that the image appears in the preview area

#### Using Postman or curl
You can also test the API directly:

```bash
curl -X POST \
  http://localhost:8080/api/upload/product-image \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@/path/to/your/image.jpg'
```

### 2. Test in the Main Application

1. **Access the Application**: Go to `http://localhost:8080`
2. **Login**: Use the admin credentials:
   - Username: `admin@mybakery.com`
   - Password: `Admin@123`
3. **Navigate to Products**: Click on "Products" in the sidebar
4. **Add New Product**: Click the "Add Product" button
5. **Test Image Upload**:
   - In the product form, scroll down to the "Product Images" section
   - Try uploading images using drag & drop or click to browse
   - Verify that images appear in the preview
   - Test removing images by clicking the "X" button
6. **Save Product**: Fill in other required fields and save the product
7. **Verify**: Check that the product was saved with images

### 3. Test Editing Products

1. **Edit Existing Product**: Click the edit button on any product
2. **Verify Images Load**: Check that existing images are displayed
3. **Add More Images**: Try adding additional images
4. **Remove Images**: Test removing some images
5. **Save Changes**: Update the product and verify changes are saved

## Expected Behavior

### ✅ What Should Work

1. **File Selection**: Clicking the upload area should open file browser
2. **Drag & Drop**: Dragging images over the area should highlight it
3. **File Validation**: Only image files should be accepted
4. **Size Validation**: Files over 5MB should be rejected
5. **Multiple Uploads**: Up to 5 images should be allowed
6. **Progress Indicator**: Upload progress should be shown
7. **Image Preview**: Uploaded images should appear as thumbnails
8. **Image Removal**: Clicking "X" should remove images
9. **Product Saving**: Images should be saved with the product

### ❌ Common Issues to Check

1. **Upload Fails**: Check browser console for errors
2. **Images Not Displaying**: Verify Cloudinary URLs are accessible
3. **CORS Errors**: Check if the API endpoints are accessible
4. **File Size Issues**: Ensure files are under 5MB
5. **Authentication Issues**: Make sure you're logged in

## Debugging

### Browser Console
Open browser developer tools (F12) and check the Console tab for any JavaScript errors.

### Network Tab
Check the Network tab to see if API calls are being made and what responses are received.

### Application Logs
Check your Spring Boot application logs for any backend errors.

## Troubleshooting

### Upload Fails with 500 Error
- Check Cloudinary credentials in `application.properties`
- Verify Cloudinary account is active
- Check application logs for detailed error messages

### Images Not Uploading
- Ensure file is an image (jpg, png, gif, webp)
- Check file size is under 5MB
- Verify internet connection

### Images Not Displaying After Upload
- Check if Cloudinary URLs are accessible
- Verify the image URLs are being saved correctly
- Check browser console for any JavaScript errors

### Drag & Drop Not Working
- Ensure you're using a modern browser
- Check if JavaScript is enabled
- Verify the CSS classes are being applied correctly

## Success Criteria

The image upload feature is working correctly if:

1. ✅ You can upload images via drag & drop
2. ✅ You can upload images via file browser
3. ✅ Images appear in the preview area
4. ✅ You can remove individual images
5. ✅ Products are saved with image URLs
6. ✅ Existing product images load when editing
7. ✅ No errors appear in browser console
8. ✅ No errors appear in application logs

## Next Steps

Once testing is complete:

1. **Remove Test File**: Delete `test-upload.html` if no longer needed
2. **Production Deployment**: Update Cloudinary credentials for production
3. **User Training**: Train users on how to use the image upload feature
4. **Monitoring**: Set up monitoring for upload success/failure rates

## Support

If you encounter issues:

1. Check the [Cloudinary Setup Guide](CLOUDINARY_SETUP.md)
2. Review application logs
3. Check browser console for errors
4. Verify Cloudinary account status
5. Test with different image files and sizes 