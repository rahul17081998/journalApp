# Exception Handling in Journal App

This document explains the standardized exception handling approach used in this application.

## Overview

The application uses a global exception handling mechanism to provide consistent error responses across all APIs. This is implemented using Spring's `@RestControllerAdvice` and a standard response format.

## Standard Response Format

All API responses follow the `ApiResponse<T>` format:

```json
{
  "success": true/false,
  "message": "Human-readable message",
  "data": { /* Response data when success is true */ },
  "timestamp": "2023-07-15T14:30:45.123Z",
  "statusCode": 200,
  "statusName": "OK",
  "error": {
    "code": "ERROR_CODE",
    "details": "Detailed error information"
  }
}
```

## Custom Exceptions

The application defines several custom exceptions:

1. `ResourceNotFoundException`: Thrown when a requested resource cannot be found.
2. `BadRequestException`: Thrown when the client sends an invalid request.
3. `FileProcessingException`: Thrown when there's an error processing files.

## How to Use

### In Controllers

Controllers should not include try-catch blocks. Instead, they should:

1. Focus on parameter validation and business logic
2. Throw appropriate exceptions when errors occur
3. Return `ApiResponse` objects for successful responses

Example with cleaner static imports:

```java
import static com.rahul.journal_app.model.ApiResponse.success;

@PostMapping("/resource")
public ResponseEntity<ApiResponse<ResourceDto>> createResource(@RequestBody ResourceRequest request) {
    // Validate request
    if (!isValid(request)) {
        throw new BadRequestException("Invalid resource request");
    }
    
    // Process request
    ResourceDto resource = service.createResource(request);
    
    // Return success response with default OK status
    return ResponseEntity.ok(success(resource, "Resource created successfully"));
    
    // Or with a custom status
    // return ResponseEntity.status(HttpStatus.CREATED)
    //     .body(success(resource, "Resource created successfully", HttpStatus.CREATED));
}
```

### In Services

Services should:

1. Validate their input
2. Throw specific exceptions when errors occur
3. Return domain objects or DTOs, not `ResponseEntity` objects

Example:

```java
public User updateUser(String username, UserUpdateRequest request) {
    User user = findByUsername(username);
    if (user == null) {
        throw new ResourceNotFoundException("User", "username", username);
    }
    
    // Update user
    return userRepository.save(user);
}
```

### Simplified Error Handling

The enhanced `ApiResponse` class provides utility methods for simpler error creation:

```java
// With details
ApiResponse.error("User not found", "NOT_FOUND", "User with ID 123 was not found", HttpStatus.NOT_FOUND);

// Without details (simpler)
ApiResponse.error("User not found", "NOT_FOUND", HttpStatus.NOT_FOUND);

// With default INTERNAL_SERVER_ERROR status
ApiResponse.error("Operation failed", "PROCESS_ERROR");
```

## Exception Hierarchy

```
RuntimeException
├── ResourceNotFoundException
├── BadRequestException
└── FileProcessingException
```

## HTTP Status Codes

| Exception Type | HTTP Status |
|----------------|-------------|
| ResourceNotFoundException | 404 NOT_FOUND |
| BadRequestException | 400 BAD_REQUEST |
| FileProcessingException | 500 INTERNAL_SERVER_ERROR |
| AccessDeniedException | 403 FORBIDDEN |
| BadCredentialsException | 401 UNAUTHORIZED |
| MaxUploadSizeExceededException | 413 PAYLOAD_TOO_LARGE |
| MethodArgumentNotValidException | 400 BAD_REQUEST |
| RuntimeException | 500 INTERNAL_SERVER_ERROR |
| Exception | 500 INTERNAL_SERVER_ERROR |

## Benefits

1. **Consistency**: All APIs return responses in the same format
2. **Clean Code**: Controllers and services don't need to handle exceptions directly
3. **Separation of Concerns**: Business logic is separate from error handling
4. **Easier Debugging**: Centralized logging of exceptions
5. **Better Client Experience**: Clients receive meaningful error messages and codes
6. **HTTP Status Integration**: Status codes included in the response body for easier client parsing 
