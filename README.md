# Journal Application

A comprehensive journal management system built with Spring Boot, MongoDB, and modern cloud-native features.

## 📋 Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Key Features](#key-features)
- [Technical Stack](#technical-stack)
- [Setup and Running](#setup-and-running)
- [API Endpoints](#api-endpoints)
- [Security Implementation](#security-implementation)
- [Database Schema](#database-schema)
- [Advanced Features](#advanced-features)
- [Performance Optimizations](#performance-optimizations)
- [Monitoring](#monitoring)

## Overview

The Journal Application is a full-featured personal journal management system that allows users to create, update, and manage journal entries with rich features like sentiment analysis, PDF exports, profile management, and administrative controls.

## Architecture

The application follows a clean architecture pattern with:

- **Controllers**: Handle HTTP requests and responses
- **Services**: Implement business logic
- **Repositories**: Manage data access
- **Entities**: Define data models
- **DTOs/Models**: Transfer data between layers
- **Configuration**: System-wide settings
- **Filters**: Process requests and responses
- **Exception Handling**: Centralized error management
- **Security**: Authentication and authorization
- **Monitoring**: Performance and health metrics

### System Architecture Diagram

```
┌───────────────────────────────────────────────────────────────────────────┐
│                         Journal Application Architecture                  │
└───────────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                                 Client                                    │
└───────────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                              API Gateway Layer                            │
│                                                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────┐    │
│  │ JWT Filter      │  │ Logging Filter  │  │ Security Filter Chain   │    │
│  └─────────────────┘  └─────────────────┘  └─────────────────────────┘    │
└───────────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                              Controller Layer                             │
│                                                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │ UserController  │  │ JournalController│  │ AdminController │           │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
│                                                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │ PublicController│  │ PDFExportController│ AttachmentController         │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
└───────────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                               Service Layer                               │
│                                                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │ UserService     │  │ JournalService  │  │ EmailService    │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
│                                                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │ PDFGenerator    │  │ AttachmentService│  │ WeatherService │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
│                                                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │ SmsService      │  │ TwitterService  │  │ RedisService    │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
└───────────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                            Repository Layer                               │
│                                                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │ UserRepository  │  │ JournalRepository│  │ AttachmentRepo │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
│                                                                           │
│  ┌─────────────────┐  ┌─────────────────┐                                 │
│  │ UserOtpRepository│  │ ConfigRepository│                                │
│  └─────────────────┘  └─────────────────┘                                 │
└───────────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                             Data Store Layer                              │
│                                                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │ MongoDB         │  │ Redis           │  │ Kafka           │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
└───────────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                           Monitoring & Metrics                            │
│                                                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │ JMX MBeans      │  │ Prometheus      │  │ Actuator        │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
└───────────────────────────────────────────────────────────────────────────┘
```

### Component Interaction Diagram

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│             │      │             │      │             │      │             │
│   Client    │──┬──▶│  Controller │──┬──▶│   Service   │──┬──▶│ Repository  │
│             │  │   │             │  │   │             │  │   │             │
└─────────────┘  │   └─────────────┘  │   └─────────────┘  │   └─────────────┘
                 │                    │                    │          │
                 │                    │                    │          │
                 │                    │                    │          ▼
                 │                    │                    │   ┌─────────────┐
                 │                    │                    │   │             │
                 │                    │                    │   │  MongoDB    │
                 │                    │                    │   │             │
                 │                    │                    │   └─────────────┘
                 │                    │                    │
                 │                    │                    │   ┌─────────────┐
                 │                    │                    │   │             │
                 │                    │                    └──▶│    Redis    │
                 │                    │                        │  (Cache)    │
                 │                    │                        └─────────────┘
                 │                    │
                 │                    │                    ┌─────────────┐
                 │                    │                    │             │
                 │                    └───────────────────▶│  External   │
                 │                                         │   APIs      │
                 │                                         └─────────────┘
                 │
                 │                    ┌─────────────┐
                 │                    │             │
                 └───────────────────▶│  Security   │
                                      │  Filters    │
                                      └─────────────┘
```

### Data Flow Diagram for Journal Entry Creation and PDF Export

```
┌───────────────┐     ┌────────────────┐     ┌───────────────┐
│               │     │                │     │               │
│    Client     │────▶│ JournalController ──▶│ JournalService│
│               │     │                │     │               │
└───────┬───────┘     └────────────────┘     └───────┬───────┘
        │                                            │
        │                                            │
        │                                            ▼
        │                                    ┌───────────────┐
        │                                    │               │
        │                                    │ JournalRepository
        │                                    │               │
        │                                    └───────┬───────┘
        │                                            │
        │                                            │
        │                                            ▼
        │                                    ┌───────────────┐
        │                                    │               │
        │                                    │   MongoDB     │
        │                                    │               │
        │                                    └───────────────┘
        │
        │             ┌────────────────┐     ┌───────────────┐
        │             │                │     │               │
        └────────────▶│ PDFController  │────▶│PDFGeneratorService
                      │                │     │               │
                      └────────────────┘     └───────────────┘
```

## Key Features

1. **User Management**
   - Registration with email verification
   - Profile management (personal details, photo, preferences)
   - Role-based access control (User, Admin)

2. **Journal Entries**
   - Create, read, update, delete operations
   - Rich text content
   - Sentiment analysis integration
   - Date-based filtering and organization

3. **PDF Generation**
   - Export journals to professionally formatted PDFs
   - Customizable templates
   - Table of contents with page numbers
   - Support for profile photos

4. **File Attachments**
   - Upload and manage attachments
   - Profile photo management

5. **Weather Integration**
   - Location-based weather information

6. **Security**
   - JWT-based authentication
   - Role-based authorization
   - Password encryption
   - CSRF protection

7. **Notifications**
   - Email notifications
   - SMS integration

## Technical Stack

- **Backend**: Spring Boot, Java
- **Database**: MongoDB
- **Caching**: Redis
- **Security**: Spring Security, JWT
- **Monitoring**: JMX, Prometheus, Micrometer
- **PDF Generation**: iText/OpenPDF
- **Messaging**: Kafka for asynchronous processing
- **Email**: SMTP integration

## Setup and Running

### Prerequisites
- Java 11 or higher
- MongoDB
- Redis
- Kafka (optional, for async processing)
- Maven

### Clone the Repository
```bash
git clone https://github.com/yourusername/journal-app.git
cd journal-app
```

### Configuration
The application uses environment variables for configuration. The main configuration file is `application.yml`, but you should use an environment file for running the application.

Create an environment variable file or use the provided `Db-script/environment_variable.env` which contains settings like:
```
SERVER_PORT=8081
MONGODB_URI=mongodb://localhost:27017/journaldb
REDIS_HOST=localhost
REDIS_PORT=6379
# Other environment variables...
```

### Building the Application
```bash
mvn clean package
```

### Running the Application
Run the application with environment variables:
```bash
java -jar target/journal-app.jar --spring.config.location=file:./application.yml --spring.profiles.active=dev
```

Using environment file:
```bash
java -jar target/journal-app.jar --spring.config.location=file:./application.yml --spring.profiles.active=dev --env-file=Db-script/environment_variable.env
```

### Setting Up Monitoring

#### Prometheus Setup
1. Navigate to the Prometheus directory:
   ```bash
   cd /Users/rahulkumar
   ```

2. Run Prometheus with the configuration file:
   ```bash
   ./prometheus-2.54.1.darwin-amd64 --config.file=<path-to-prometheus-config>/prometheus.yml
   ```

#### JMX Exporter Setup
1. To enable JMX metrics export to Prometheus, add this JVM argument when running the application:
   ```bash
   java -javaagent:/Users/rahulkumar/jmx_prometheus_javaagent-1.2.0.jar=1234:/Users/rahulkumar/Documents/mongoDb-springboot/journalApp/config/jmx_exporter_config.yml -jar target/journal-app.jar
   ```

2. This will expose JMX metrics on port 1234 which Prometheus can scrape.

### Accessing the Application
Once running, the application will be available at:
- API Endpoints: `http://localhost:8081/api`
- Actuator Endpoints: `http://localhost:8081/actuator`
- Prometheus Metrics: `http://localhost:8081/actuator/prometheus`

## API Endpoints

### Public APIs
- **POST /auth/register**: Register a new user
- **POST /auth/login**: User login
- **POST /auth/verify-otp**: Verify OTP for email confirmation
- **POST /auth/resend-otp**: Resend verification OTP
- **GET /auth/forget-password**: Request password reset
- **POST /auth/reset-password**: Reset user password

### User APIs
- **GET /user**: Get user profile with weather information
- **PUT /user**: Update user profile
- **DELETE /user**: Delete user account
- **POST /user/upload-profile-photo**: Upload profile photo
- **GET /user/user-details**: Get detailed user profile information

### Journal APIs
- **GET /journal**: Get all journal entries
- **POST /journal**: Create a new journal entry
- **GET /journal/{id}**: Get a specific journal entry
- **PUT /journal/{id}**: Update a journal entry
- **DELETE /journal/{id}**: Delete a journal entry

### PDF Export APIs
- **GET /pdf/export**: Export user details and all journals of a particular user to PDF

### Attachment APIs
- **POST /attachment/upload**: Upload a file attachment
- **GET /attachment/download/{fileId}**: Download a specific attachment

### Admin APIs
- **GET /admin/users**: Get all users (Admin only)
- **PUT /admin/users/{username}/grant-admin**: Grant admin access (Admin only)
- **PUT /admin/users/{username}/remove-admin**: Remove admin access (Admin only)

## Security Implementation

The application implements a robust security system:

1. **JWT Authentication**: Token-based stateless authentication
2. **Custom Security Filter**: `JwtFilter` for validating tokens
3. **Role-Based Access Control**: Different permissions for User and Admin roles
4. **Password Encryption**: BCrypt password encoder
5. **Endpoint Security**: Secured API endpoints
6. **CSRF Protection**: Configurable CSRF protection

## Database Schema

The application uses MongoDB with the following main collections:

1. **User**
   - Core user information (username, password, roles)
   - Profile details (name, email, phone, address)
   - Configuration and preferences
   - Embedded collections (education, social profiles, preferences)

2. **JournalEntries**
   - Journal content and metadata
   - Sentiment analysis results
   - Creation and modification timestamps

3. **Attachment**
   - File metadata for attachments
   - Binary storage for uploaded files

4. **UserOtp**
   - OTP storage for verification
   - Expiration management

## Advanced Features

### Exception Handling
- Centralized exception handling through `GlobalExceptionHandler`
- Standardized API responses with `ApiResponse` class
- Custom exceptions for specific error scenarios

### Caching
- Redis-based caching for improved performance
- Custom `AppCache` implementation

### JMX Monitoring
- MBean implementations for system monitoring
- Metrics for user activity, system health

### Prometheus Integration
- Custom metrics collection
- JMX metrics exported to Prometheus
- Performance and health monitoring

### Scheduled Tasks
- User-related scheduled operations
- Automated cleanup and maintenance

### Async Processing
- Kafka integration for asynchronous event processing
- Sentiment analysis performed asynchronously

## Performance Optimizations

1. **Redis Caching**: Frequently accessed data cached in Redis
2. **Efficient Queries**: Optimized MongoDB queries
3. **Connection Pooling**: Database connection management
4. **Async Processing**: Non-blocking operations where possible
5. **Metrics Monitoring**: Performance tracking for optimization

## Monitoring

The application provides comprehensive monitoring capabilities:

1. **JMX MBeans**: Expose runtime metrics via JMX
2. **Prometheus Integration**: Custom metrics exported for Prometheus
3. **Actuator Endpoints**: Spring Boot Actuator for health and metrics
4. **Logging**: Detailed logging with SLF4J and Logback
5. **Request Tracking**: Global request logging filter

---

## Development Guidelines

### Exception Handling

The application implements a standardized approach to handling exceptions:

1. **Custom Exceptions**:
   - `ResourceNotFoundException`: When requested resources cannot be found
   - `BadRequestException`: For invalid client requests
   - `FileProcessingException`: For file handling errors

2. **Global Exception Handler**:
   - Centralized error handling via `@RestControllerAdvice`
   - Consistent error responses in JSON format
   - Appropriate HTTP status codes

3. **Standard Response Format**:
   - All API responses use the `ApiResponse<T>` format
   - Success/error flag, message, timestamp, and data
   - HTTP status codes embedded in the response

See the [Exception Handling Documentation](./src/main/java/com/rahul/journal_app/README_EXCEPTION_HANDLING.md) for detailed implementation guidelines.
