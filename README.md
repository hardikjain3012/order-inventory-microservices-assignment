# order-inventory-microservices-assignment

## Project Overview
This project involves the design and implementation of two Spring Boot microservices — Order Service and Inventory Service. These services communicate with each other via REST APIs. The system is designed to be modular and extensible, utilizing the Factory Design Pattern to allow for future expansion of services and logic.

## Features
- **Order Service**: Manages customer orders, interacts with Inventory Service to check product availability.
- **Inventory Service**: Manages product inventory, provides stock information to Order Service.

## Tech Stack
- **Backend**: Spring Boot, Java
- **Database**: H2 Database (for development and testing)
- **API Documentation**: Swagger
- **Testing**: JUnit, Mockito, JaCoCo for code coverage

## Prerequisites
- Java 17
- Maven
- H2 Database (embedded)
- IDE (e.g., IntelliJ IDEA)

## Quick Start

### Build & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/hardikjain3012/order-inventory-microservices-assignment.git
   cd order-inventory-microservices-assignment
   ```
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```
3. Run the services:
   ```bash
   java -jar order-service/target/order-service.jar
   java -jar inventory-service/target/inventory-service.jar
   ```

## API Documentation (OpenAPI / Swagger)

Both services include OpenAPI (Swagger) support via **springdoc**. After you start the services you can open the interactive Swagger UI in your browser.

- Inventory Service (default):
    - Swagger UI: http://localhost:8081/swagger-ui/index.html
    - OpenAPI JSON: http://localhost:8081/v3/api-docs

- Order Service (default):
    - Swagger UI: http://localhost:8080/swagger-ui/index.html
    - OpenAPI JSON: http://localhost:8080/v3/api-docs

If you've changed the server port in `application.properties`, update the URL accordingly.

springdoc dependencies were added to each module's `pom.xml` (inventory-service uses webmvc starter, order-service uses webflux starter) — this provides the UI and the `/v3/api-docs` endpoint automatically.

Example usage (via UI or curl):

### inventory-service:

- get batches by product id
  ```bash
  curl -X GET "http://localhost:8081/inventory/products/1?strategy=FEFOInventoryService" -H "Content-Type: application/json"
  ```

- update batch quantity
  ```bash
  curl -X POST "http://localhost:8081/inventory/update" -H "Content-Type: application/json" -d '{"productId": 3, "quantity": 20, "action": "DECREMENT", "strategy": "FEFOInventoryService"}'
  ```

### order-service:

- create order
  ```bash
  curl -X POST "http://localhost:8080/orders" -H "Content-Type: application/json" -d '{"customerName":"John","items":[{"productId":1,"quantity":2}]}'
  ```

## Testing
- Unit tests are provided for both services using JUnit and Mockito.
- To run the tests, use the following command:
  ```bash
  mvn test
  ```
- JaCoCo is used for test coverage reporting. To generate the coverage report, run:
  ```bash
  mvn jacoco:report
  ```

## Troubleshooting
- Ensure that all prerequisites are met before running the services.
- Check the application logs for any error messages.
- Common issues include port conflicts, database connection issues, and missing environment variables.

## Next Steps
- Explore adding more microservices to the system.
- Implement API Gateway for better management of microservices.
- Consider using Docker for containerization and easier deployment.
