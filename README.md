# Gradle80

A comprehensive Java 8 application built with Gradle, featuring Spring Boot and AWS integration.

## Project Overview

This project demonstrates a modern microservice architecture with the following capabilities:
- RESTful API endpoints using Spring Boot
- AWS messaging integration with SNS and SQS
- Data persistence with JPA
- Batch processing capabilities
- Comprehensive testing with JUnit and TestNG

## Technology Stack

- **Java Version**: 8
- **Build System**: Gradle
- **Frameworks**: 
  - Spring Boot
  - AWS SDK (SNS, SQS)
- **Testing**: 
  - JUnit
  - TestNG
- **Other Libraries**:
  - Lombok
  - Jackson
  - Swagger

## Project Structure

The application is organized into modular components:

### API Module

API definitions defining service contracts with models, request/response objects, and Swagger documentation.

- User, Product, Order, and Notification service interfaces
- DTO and request/response models
- API validation aspects

### Common Module

Common utilities, shared models, and core components used across all modules.

- Base entity and DTO classes
- Exception handling framework
- Utility classes (Date, String, JSON)
- Common validation framework

### Service Module

Core business logic implementing application services, business rules, and domain operations.

- Service implementations for User, Product, Order, and Notification
- Event handling framework
- Caching mechanisms
- JWT authentication

### Data Module

Data access layer with repository interfaces, entity definitions, and database configuration.

### AWS Integration Module

Integration with AWS messaging services providing SNS publishing and SQS message handling capabilities.

- SNS publishing
- SQS message listeners
- Queue and Topic management
- AWS credentials management

### Web Module

Web layer exposing REST endpoints, handling requests, and implementing controllers.

### Batch Module

Batch processing framework for scheduled and large-scale data operations.

## Setup and Installation

### Prerequisites

- JDK 8
- Gradle
- AWS Account (for SNS/SQS functionality)

### Building the Project

```bash
# Clone the repository
git clone [repository-url]

# Navigate to project directory
cd gradle80

# Build the project
./gradlew clean build
```

### Running the Application

```bash
./gradlew bootRun
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run integration tests
./gradlew integrationTest
```

## Configuration

The application uses a hierarchical configuration approach:

1. `application.yml` - Main application configuration
2. Environment-specific configs (dev, test, prod)
3. Module-specific configurations

Key configuration files:
- `application.yml` - Core application settings
- `application-aws.yml` - AWS integration settings
- `application-db.yml` - Database configuration

## API Documentation

API documentation is available via Swagger UI:
- When running locally: http://localhost:8080/swagger-ui.html

## Deployment

The application can be deployed as:
- Standalone JAR
- Docker container
- AWS service (ECS, EKS, etc.)

### Docker Deployment

```bash
# Build Docker image
docker build -t gradle80 .

# Run Docker container
docker run -p 8080:8080 gradle80
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement your changes
4. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## TODO

- Add Kubernetes deployment manifests
- Implement metrics collection with Prometheus
- Add distributed tracing
- Enhance test coverage

## FIXME

- Update deprecated AWS SDK methods
- Fix intermittent test failures in AWS integration tests