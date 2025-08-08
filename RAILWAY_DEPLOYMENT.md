# Railway Deployment Guide

## Prerequisites
1. Railway account (https://railway.app)
2. Git repository with your code
3. Railway CLI (optional but recommended)

## Deployment Steps

### 1. Connect to Railway
- Go to https://railway.app
- Sign in with your GitHub account
- Click "New Project"
- Select "Deploy from GitHub repo"

### 2. Select Repository
- Choose your `my-bakery` repository
- Railway will automatically detect it's a Java/Maven project

### 3. Configure Environment Variables
Set the following environment variables in Railway dashboard:

```
PORT=8080
ADMIN_USERNAME=admin@mybakery.com
ADMIN_PASSWORD=Admin@123
DATABASE_URL=jdbc:postgresql://your-railway-db-url:5432/railway
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your-db-password
JWT_SECRET=your-secure-jwt-secret
CLOUDINARY_CLOUD_NAME=your-cloudinary-cloud-name
CLOUDINARY_API_KEY=your-cloudinary-api-key
CLOUDINARY_API_SECRET=your-cloudinary-api-secret
FILE_UPLOAD_DIR=/tmp/uploads
LOG_FILE=/tmp/application.log
```

### 4. Add PostgreSQL Database
- In Railway dashboard, click "New"
- Select "Database" → "PostgreSQL"
- Railway will automatically provide the connection details
- Update your `DATABASE_URL` environment variable with the provided URL

### 5. Deploy
- Railway will automatically build and deploy your application
- The build process uses the `nixpacks.toml` configuration
- Monitor the build logs for any issues

### 6. Verify Deployment
- Check the deployment logs in Railway dashboard
- Visit your application URL (provided by Railway)
- Test the health endpoint: `https://your-app.railway.app/actuator/health`

## Important Notes

### Database Migration
- The application uses `spring.jpa.hibernate.ddl-auto=update`
- Tables will be created automatically on first run
- Ensure your database is properly configured

### File Uploads
- File uploads are configured to use `/tmp/uploads` directory
- This is ephemeral storage - files will be lost on container restart
- Consider using Cloudinary for persistent file storage

### Health Checks
- Railway will use `/actuator/health` for health checks
- Ensure this endpoint is accessible and returns 200 OK

### Scaling
- Railway supports automatic scaling based on traffic
- Monitor your usage in the Railway dashboard

## Troubleshooting

### Build Issues
- Check the build logs in Railway dashboard
- Ensure all dependencies are properly declared in `pom.xml`
- Verify Java version compatibility (Java 17)

### Runtime Issues
- Check application logs in Railway dashboard
- Verify environment variables are correctly set
- Ensure database connectivity

### Performance
- Monitor resource usage in Railway dashboard
- Consider optimizing database queries
- Review connection pool settings

## Security Considerations

1. **Environment Variables**: Never commit sensitive data to Git
2. **JWT Secret**: Use a strong, unique JWT secret
3. **Database**: Use strong passwords for database access
4. **CORS**: Configure CORS properly for production
5. **HTTPS**: Railway provides HTTPS by default

## Monitoring

- Use Railway's built-in monitoring tools
- Set up alerts for application health
- Monitor database performance
- Track API usage and response times 
