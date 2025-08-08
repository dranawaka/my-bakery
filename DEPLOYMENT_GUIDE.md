# My Bakery Management System - Deployment Guide

This guide covers deploying the separated React frontend and Spring Boot backend applications.

## Architecture Overview

The application is now split into two separate components:

1. **React Frontend** (`my-bakery-frontend/`) - Modern UI built with React
2. **Spring Boot Backend** (root directory) - REST API and business logic

## Prerequisites

- Node.js (v16 or higher)
- Java 17 or higher
- Maven 3.6+
- Database (MySQL/PostgreSQL)

## Development Setup

### 1. Backend Setup

```bash
# Navigate to backend directory
cd my-bakery

# Install dependencies and start
mvn clean install
mvn spring-boot:run
```

The backend will run on `http://localhost:8080`

### 2. Frontend Setup

```bash
# Navigate to frontend directory
cd my-bakery-frontend

# Install dependencies
npm install

# Start development server
npm start
```

The frontend will run on `http://localhost:3000`

## Production Deployment

### Option 1: Separate Deployment (Recommended)

#### Backend Deployment

1. **Build the JAR file:**
   ```bash
   cd my-bakery
   mvn clean package -DskipTests
   ```

2. **Deploy the JAR:**
   ```bash
   java -jar target/my-bakery-0.0.1-SNAPSHOT.jar
   ```

3. **Environment Configuration:**
   ```bash
   export SPRING_PROFILES_ACTIVE=prod
   export SPRING_DATASOURCE_URL=jdbc:mysql://your-db-host:3306/my_bakery
   export SPRING_DATASOURCE_USERNAME=your_username
   export SPRING_DATASOURCE_PASSWORD=your_password
   ```

#### Frontend Deployment

1. **Build the React app:**
   ```bash
   cd my-bakery-frontend
   npm run build
   ```

2. **Deploy to a static hosting service:**
   - **Netlify**: Drag and drop the `build` folder
   - **Vercel**: Connect your GitHub repository
   - **AWS S3**: Upload build files to S3 bucket
   - **Nginx**: Serve build files from nginx

3. **Environment Configuration:**
   Create `.env.production`:
   ```env
   REACT_APP_API_URL=https://your-backend-domain.com/api
   ```

### Option 2: Combined Deployment

#### Build React App and Serve from Spring Boot

1. **Build React app:**
   ```bash
   cd my-bakery-frontend
   npm run build
   ```

2. **Copy build files to Spring Boot:**
   ```bash
   # Copy all files from build directory to Spring Boot static folder
   cp -r build/* ../src/main/resources/static/
   ```

3. **Update Spring Boot configuration:**
   Add to `application.properties`:
   ```properties
   # Serve React app for all non-API routes
   spring.web.resources.add-mappings=true
   spring.web.resources.static-locations=classpath:/static/
   ```

4. **Add React routing support:**
   Create `src/main/java/com/aurelius/tech/mybakery/config/ReactRoutingConfig.java`:
   ```java
   @Controller
   public class ReactRoutingConfig {
       @RequestMapping(value = "/{path:[^\\.]*}")
       public String redirect() {
           return "forward:/index.html";
       }
   }
   ```

5. **Deploy the combined application:**
   ```bash
   mvn clean package
   java -jar target/my-bakery-0.0.1-SNAPSHOT.jar
   ```

## Docker Deployment

### Backend Dockerfile

Create `Dockerfile` in the backend root:

```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/my-bakery-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Frontend Dockerfile

Create `Dockerfile` in `my-bakery-frontend/`:

```dockerfile
FROM node:16-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Docker Compose

Create `docker-compose.yml` in the root:

```yaml
version: '3.8'
services:
  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/my_bakery
    depends_on:
      - db

  frontend:
    build: ./my-bakery-frontend
    ports:
      - "80:80"
    depends_on:
      - backend

  db:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: my_bakery
      MYSQL_ROOT_PASSWORD: rootpassword
    volumes:
      - db_data:/var/lib/mysql

volumes:
  db_data:
```

## Environment Configuration

### Backend Environment Variables

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/my_bakery
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# Cloudinary (if using)
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Server
SERVER_PORT=8080
```

### Frontend Environment Variables

```bash
# API URL
REACT_APP_API_URL=https://your-backend-domain.com/api

# Other configurations
REACT_APP_APP_NAME=My Bakery
REACT_APP_VERSION=1.0.0
```

## Security Considerations

### Backend Security

1. **CORS Configuration**: Only allow specific origins
2. **JWT Security**: Use strong secret keys
3. **Database Security**: Use connection pooling and prepared statements
4. **Input Validation**: Validate all inputs
5. **HTTPS**: Use SSL/TLS in production

### Frontend Security

1. **Environment Variables**: Never expose sensitive data
2. **API Security**: Use HTTPS for API calls
3. **Token Storage**: Store tokens securely
4. **Input Sanitization**: Sanitize user inputs

## Monitoring and Logging

### Backend Monitoring

1. **Application Metrics**: Use Spring Boot Actuator
2. **Logging**: Configure proper logging levels
3. **Health Checks**: Implement health endpoints
4. **Performance Monitoring**: Use Micrometer for metrics

### Frontend Monitoring

1. **Error Tracking**: Implement error boundaries
2. **Performance Monitoring**: Use React Profiler
3. **Analytics**: Track user interactions
4. **Logging**: Log important user actions

## Troubleshooting

### Common Issues

1. **CORS Errors**: Check CORS configuration
2. **API Connection**: Verify backend URL
3. **Build Failures**: Check Node.js version
4. **Database Connection**: Verify database credentials

### Debug Commands

```bash
# Check backend health
curl http://localhost:8080/actuator/health

# Check frontend build
cd my-bakery-frontend && npm run build

# Check Docker containers
docker-compose ps

# View logs
docker-compose logs -f
```

## Performance Optimization

### Backend Optimization

1. **Database Indexing**: Optimize database queries
2. **Caching**: Implement Redis caching
3. **Connection Pooling**: Configure HikariCP
4. **Compression**: Enable GZIP compression

### Frontend Optimization

1. **Code Splitting**: Implement lazy loading
2. **Bundle Optimization**: Minimize bundle size
3. **Image Optimization**: Compress images
4. **Caching**: Implement service workers

## Backup and Recovery

1. **Database Backups**: Regular database backups
2. **Application Backups**: Backup configuration files
3. **Disaster Recovery**: Document recovery procedures
4. **Version Control**: Maintain code versioning

## Support and Maintenance

1. **Regular Updates**: Keep dependencies updated
2. **Security Patches**: Apply security updates
3. **Performance Monitoring**: Monitor application performance
4. **User Support**: Provide user documentation 