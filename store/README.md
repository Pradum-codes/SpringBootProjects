# Simple Store — Teaching Guide

This document explains what the project does and why, from a developer/learning perspective. It is tailored for you so you can quickly recall what you built, why the pieces are there, and what concepts you practiced. The file is organized so each major file and concept has a clear, concise explanation.

## Personal summary — what I built and what I learned

This section is your quick reference — short bullets you can read and immediately remember the responsibilities and learning points.

- What I built:
  - A small Spring Boot web app with server-rendered pages (Thymeleaf) and basic CRUD for products.
  - A JPA `Product` entity persisted via Spring Data JPA to a relational database (configured for MySQL in `application.properties`).
  - A simple `ProductRepository` (extends `JpaRepository`) to get CRUD operations for free.
  - A `ProductService` to encapsulate business logic and interact with the repository.
  - Two controllers: `UserInterfaceController` (for `/`) and `ProductController` (for listing, adding, editing, deleting products). The `ProductController` mixes HTML form handling (GET/POST) and a small JSON `PUT` endpoint.
  - A `ProductUpdateDto` used to validate incoming JSON for the `PUT` endpoint using Jakarta Validation annotations.
  - Thymeleaf templates (`index`, `view`, `add`, `update`) and a static CSS file to style the UI.
  - A minimal `@SpringBootTest` to verify the application context starts.

- What I learned / practiced:
  - How `@SpringBootApplication` boots the app and how Spring auto-configures components.
  - Constructor-based dependency injection for services and controllers.
  - Basic JPA entity mapping and how `JpaRepository` simplifies data access.
  - Server-side rendering with Thymeleaf: passing model attributes, using `th:each`, `th:if`, and binding form values.
  - Differentiating between form-based handlers (`@RequestParam`) and JSON REST handlers (`@RequestBody` + `@Valid`).
  - Applying simple validation constraints and how a DTO helps decouple the API contract from the persistence model.
  - How `application.properties` controls DB connection, JPA behavior, and Thymeleaf caching.

Tip: Keep this section as your quick reference — you can expand any bullet into a small coding task (e.g., add a 'category' field) to practice.

---

## Table of contents

- [Motivation](#motivation)
- [Project structure and important files](#project-structure-and-important-files)
- [Deep dive: each class and file explained](#deep-dive-each-class-and-file-explained)
- [Key annotations and concepts (detailed)](#key-annotations-and-concepts-detailed)
- [Thymeleaf and templates explained](#thymeleaf-and-templates-explained)
- [Form handling patterns used here](#form-handling-patterns-used-here)
- [Validation and DTOs](#validation-and-dtos)
- [Persistence and database configuration](#persistence-and-database-configuration)
- [Testing strategy and examples](#testing-strategy-and-examples)
- [How to run the app locally (exact commands)](#how-to-run-the-app-locally-exact-commands)
- [Suggested exercises (with small steps)](#suggested-exercises-with-small-steps)
- [Troubleshooting & tips](#troubleshooting--tips)

---

## Motivation

This app is a learning project to practice building a simple, real-world web application with Spring Boot. It covers a compact but complete flow: data model → persistence → service layer → controllers → server-rendered UI. The goal is to be small enough to understand end-to-end, but to contain enough real concepts (JPA, validation, templates) to practice expanding and improving it.

---

## Project structure and important files

High-level map of the files you will look at most often and what they do.

- `pom.xml` — Maven configuration (dependencies, build plugins). Check it for which Spring Boot starters and libraries are included (Web, Thymeleaf, Data JPA, Validation, MySQL driver, test libs).
- `src/main/java/com/pradumcodes/store/StoreApplication.java` — application entry point. `@SpringBootApplication` and `main()` start the Spring context and embedded server.
- `src/main/java/com/pradumcodes/store/entity/Product.java` — JPA entity class representing products (id, name, price).
- `src/main/java/com/pradumcodes/store/repository/ProductRepository.java` — extends `JpaRepository` for CRUD operations.
- `src/main/java/com/pradumcodes/store/service/ProductService.java` — business logic layer. Delegates to the repository.
- `src/main/java/com/pradumcodes/store/controller/ProductController.java` — handles product-related routes and view names.
- `src/main/java/com/pradumcodes/store/controller/UserInterfaceController.java` — maps `/` to `index`.
- `src/main/java/com/pradumcodes/store/dto/ProductUpdateDto.java` — DTO for update endpoint with validation annotations.
- `src/main/resources/templates/*.html` — Thymeleaf templates that render HTML.
- `src/main/resources/static/css/styles.css` — static CSS resources.
- `src/main/resources/application.properties` — application configuration (DB, JPA, Thymeleaf, etc.).
- `src/test/java/.../StoreApplicationTests.java` — simple `@SpringBootTest` that checks context loads.

---

## Deep dive: each class and file explained

This section explains responsibilities, important methods/fields, annotations, and how each file participates in the app.

### `StoreApplication.java`
- Role: the main entry point. The `main` method calls `SpringApplication.run(...)` to create the Spring application context, configure beans, and start the embedded servlet container (Tomcat by default in Spring Boot).
- Annotation: `@SpringBootApplication` — a convenience annotation that includes `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.
  - `@EnableAutoConfiguration` tells Spring Boot to scan the classpath and auto-configure beans for common libraries (e.g., Data JPA, Thymeleaf, Web).
  - `@ComponentScan` tells Spring to discover components (`@Controller`, `@Service`, `@Repository`, `@Component`) in the package and subpackages. This is why your main class lives at the top-level package (`com.pradumcodes.store`): it ensures discovery of all your classes.
- Teaching note: moving the class to a subpackage without adjusting `@ComponentScan` can cause beans not to be discovered.

---

### `Product.java` (entity)
- Role: represents a product row persisted to the database.
- Important fields: `Long id`, `String name`, `double price`.
- Annotations:
  - `@Entity` — marks the class as a persistable JPA entity.
  - `@Id` — primary key field.
  - `@GeneratedValue(strategy = GenerationType.IDENTITY)` — instructs JPA to let the database generate the primary key value (MySQL auto-increment typical). `IDENTITY` means the DB assigns the id when the row is inserted.
- JPA requirements: a no-args constructor and getters/setters (unless using other mapping strategies). Thymeleaf uses getters to read values in templates.
- Notes: You can add `@Column` to customize column mapping, or `@Table` to rename the table. If the domain grows, consider DTOs or immutability patterns.

---

### `ProductRepository.java`
- Role: data access abstraction.
- Implementation: `interface ProductRepository extends JpaRepository<Product, Long>`.
- What it gives you: CRUD methods (save, findById, findAll, deleteById), paging & sorting, and the ability to declare repository methods with query derivation (e.g., `findByNameContaining(String q)`).
- Annotation: `@Repository` is present — it's optional when extending `JpaRepository` because Spring Data registers the bean for you, but `@Repository` is a useful semantic marker and can enable exception translation.

Teaching tip: Add custom queries with method names like `List<Product> findByPriceGreaterThan(double min)` or use `@Query("SELECT p FROM Product p WHERE ...")`.

---

### `ProductService.java`
- Role: service layer that contains business logic and interacts with `ProductRepository`.
- Annotation: `@Service` — a stereotype indicating business logic. Spring picks it up as a bean.
- Important methods: `findAll()`, `save(Product)`, `deleteById(Long)`, `findById(Long)`, `updateProduct(Long, Product)`, `countProducts()`.
- Dependency injection: constructor injection is used to require a `ProductRepository`. Constructor-based DI is recommended because it makes dependencies explicit and simplifies testing.
- Transactions: currently the service methods do not have `@Transactional`. For multi-step updates or methods that need transactional guarantees, annotate the method or class with `@Transactional` to ensure atomicity and proper session handling.

Teaching tip: Write unit tests for the service layer by mocking the repository (Mockito) to verify business logic without hitting the DB.

---

### `ProductController.java`
- Role: handle HTTP requests related to products and return Thymeleaf view names or REST-like JSON responses.
- Annotation: `@Controller` — used for MVC controllers that return view names (as opposed to `@RestController` which returns JSON by default).

Key handler methods and concepts:
- `@GetMapping("/products")` — fetches all products and adds them to the model for `view.html`.
- `@GetMapping("/add")` and `@PostMapping("/add")` — show add form and process form submission. The POST handler uses `@RequestParam` to read form fields.
- `@PostMapping("/products/delete")` — delete product via a form that includes a hidden `id` field.
- `@GetMapping("/products/{id}/edit")` and `@PostMapping("/products/{id}/edit")` — edit forms (GET to display, POST to save changes). The GET handler looks up the product (returns `update` template with `product` model attribute). The POST handler updates using `ProductService.updateProduct`.
- `@PutMapping("/products/{id}")` — REST-style endpoint that consumes JSON (`@RequestBody`) and uses `@Valid` with `ProductUpdateDto` for validation, returning a `ResponseEntity<Product>`.

Parameter binding and return types:
- `@RequestParam` binds form values (name/value pairs) to method parameters.
- `@PathVariable` binds URI path template values (e.g., `{id}`) to parameters.
- Returning `String` - the name of a Thymeleaf template (view resolver will pick `templates/<name>.html`).
- Returning `ResponseEntity<T>` - useful for JSON API endpoints with proper HTTP status codes.

Teaching tip: the controller currently swallows some exceptions and redirects silently; for better UX and maintainability add proper error handling and validation feedback.

---

### `UserInterfaceController.java`
- Role: simple controller that maps the root path (`/`) to the `index` template. It demonstrates minimal routing and separation of pages.

---

### `ProductUpdateDto.java` (DTO)
- Role: Data Transfer Object for the `PUT /products/{id}` endpoint. The DTO represents the request payload and carries validation annotations.
- Annotations used:
  - `@NotNull` — field must not be null.
  - `@Size(min = 1, max = 255)` — string length constraint for `name`.
  - `@DecimalMin("0.0")` — ensures `price` is not negative.
- Why DTOs: DTOs decouple the API contract from the persistence model (entities). They prevent over-binding (accidentally modifying entity fields not intended for the request) and centralize validation for the input shape.

Teaching tips:
- Use DTOs for all public API endpoints; map DTOs to entities in the service layer or use mapping libraries like MapStruct.
- To return shapes different from the entity (e.g., hide internal fields), create response DTOs.

---

### Templates and static resources (`resources/templates`, `resources/static`)
- Templates: `index.html`, `view.html`, `add.html`, `update.html`.
  - They use Thymeleaf expressions and attributes (`th:*`) to render server-side HTML.
  - Model attributes added in the controller (e.g., `model.addAttribute("products", ...)`) are accessible as `${products}`.
- Static resources: `static/css/styles.css` is served at `/css/styles.css` automatically by Spring Boot's static resource handling.
- Templates reference the CSS with `th:href="@{/css/styles.css}"` so the context path is handled correctly.

---

## Key annotations and concepts (detailed)

This section explains each annotation or concept you saw in the code, why it's used, and what it does at runtime.

- `@SpringBootApplication` — convenience meta-annotation for bootstrapping Spring Boot apps. It enables auto-configuration, component scanning, and allows registering extra configuration. When the app starts, Spring Boot inspects the classpath and configures beans automatically (for example, if `spring-boot-starter-data-jpa` is on the classpath, Spring Boot auto-configures a `DataSource` if properties are present).

- `@Entity` — marks a Java class as a JPA entity. Hibernate (or another JPA implementation) will map instances to DB rows.

- `@Id` and `@GeneratedValue` — identify the primary key and how values are generated. Strategies include `IDENTITY`, `AUTO`, `SEQUENCE`, and `TABLE`. `IDENTITY` is common for MySQL's auto-increment.

- `@Repository` — indicates a data access component. Spring may use it to translate persistence exceptions into Spring's DataAccessException hierarchy. When you extend `JpaRepository`, Spring Data auto-registers the interface as a bean — `@Repository` is helpful but not strictly required.

- `JpaRepository<T, ID>` — Spring Data interface providing CRUD, batch operations, paging, sorting, and CRUD-derived query method support.

- `@Service` — stereotype for service-layer classes. Functionally equivalent to `@Component`, but indicates intent (business logic).

- Dependency Injection (DI) / Constructor Injection — Spring injects beans into constructors (recommended pattern). This improves testability and makes dependencies explicit.

- `@Controller` vs `@RestController` — `@Controller` is used when methods return view names and render HTML. `@RestController` combines `@Controller` and `@ResponseBody` and is used for JSON REST APIs.

- `@GetMapping`, `@PostMapping`, `@PutMapping` — shortcut annotations for mapping HTTP methods (GET, POST, PUT) to controller methods.

- `@RequestParam` — binds HTTP form or query parameters to method arguments.

- `@PathVariable` — binds part of the URI path (e.g., `/products/{id}`) to a method argument.

- `@RequestBody` — binds the request body (e.g., JSON) to an object and uses HttpMessageConverters to convert the payload.

- `@Valid` — triggers validation for the annotated object using Jakarta Bean Validation (Hibernate Validator typically). If validation fails in a controller method parameter, Spring throws `MethodArgumentNotValidException` for `@RequestBody` or binds errors into `BindingResult` for form-backed objects.

- `ResponseEntity<T>` — flexible way to return HTTP response body and status code (and headers) from a controller.

- `@Transactional` (not present but relevant) — ensures a method runs inside a database transaction. Use for multi-step writes or when you need rollback semantics.

- Thymeleaf attributes (`th:each`, `th:if`, `th:text`, `th:href`, `th:action`, `th:value`) — used to iterate lists, show/hide blocks, inject text, and bind form actions and values. Thymeleaf expressions are evaluated on the server and produce plain HTML sent to the browser.

- `spring.jpa.hibernate.ddl-auto` — controls schema generation behavior. Common values:
  - `none` — do nothing.
  - `validate` — validate that the schema matches entities.
  - `update` — update the schema to match entities (convenient for dev; risky for production).
  - `create` — create schema on startup (drops existing schema in some cases).
  - `create-drop` — create on startup and drop on shutdown (useful for tests).

- `spring.thymeleaf.cache=false` — turn off template caching during development so changes appear immediately without restarting.

---

## Thymeleaf and templates explained (practical notes)

- `th:each` — loop over collections. Example: `th:each="p : ${products}"` iterates over `products` and binds each item to `p`.
- `th:if` — conditionally include a block in the rendered HTML. Useful for empty-state UIs.
- `th:text` vs direct expression `\${...}` — `th:text` replaces the element's body with the expression's result and safely escapes HTML by default.
- `th:href` and `th:action` — build URLs relative to the app context using `@{...}` syntax. Example: `th:href="@{/products}"` resolves to `/products`.
- Utilities: `#lists`, `#numbers` in Thymeleaf provide helper methods (check Thymeleaf docs for many built-ins).

Security note: If you later add Spring Security, templates will need to include CSRF tokens in forms. For now, the project uses default Spring Boot settings (no security configured).

---

## Form handling patterns used here

This project demonstrates two patterns:

1. HTML form submission handled by controller methods using `@RequestParam` (or binding objects). This is the classic server-rendered form flow:
   - Browser GETs a form page, server renders a HTML form, user submits form with method POST, controller reads form fields (via `@RequestParam`), service persists the entity, then you typically redirect to another page (POST-Redirect-GET pattern) to avoid duplicate submissions.

2. JSON REST endpoint using `@RequestBody` and `@Valid` (for `PUT /products/{id}`). This demonstrates how to accept API clients (e.g., JS apps or HTTP clients) sending JSON payloads. The DTO validates content, and the controller returns `ResponseEntity`.

Why both? Learning both patterns helps you understand differences between server-rendered apps and API-driven apps.

---

## Validation and DTOs

- DTOs keep external input separate from the entity model. This is important for security (avoid accidental field updates), validation specificity, and evolving API contracts.
- Bean Validation annotations (`@NotNull`, `@Size`, `@DecimalMin`) declare constraints on fields. When used with `@Valid`, Spring will validate request payloads and either throw an exception (JSON) or populate `BindingResult` (form bindings).
- Handling validation failures:
  - For form submissions, accept a `BindingResult` parameter after the validated object and re-render the form with error messages when errors exist.
  - For REST endpoints, handle `MethodArgumentNotValidException` in a `@ControllerAdvice` to return structured error responses (HTTP 400 with validation details).

Example strategy (improvement): add a `@ControllerAdvice` that maps validation exceptions into a JSON structure with `field -> message` pairs.

---

## Persistence and database configuration (detailed)

The project uses Spring Data JPA and expects a relational database. Key points from `application.properties`:

- `spring.datasource.url` — JDBC URL. Example in this project: `jdbc:mysql://localhost:3306/storedb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`.
  - `useSSL=false` avoids SSL config for local dev.
  - `allowPublicKeyRetrieval=true` is sometimes used with newer MySQL versions and non-SSL connections.
  - `serverTimezone=UTC` ensures consistent timezone handling.
- `spring.datasource.username` and `spring.datasource.password` — DB credentials.
- `spring.jpa.hibernate.ddl-auto=update` — automatically update the DB schema to match entities at startup (good for quick iteration, not recommended for production).
- `spring.jpa.show-sql=true` — prints executed SQL to the log (helpful for learning and debugging).

Local development options:
- Use the configured MySQL instance (create the database and user as in the properties), or
- Switch to an in-memory H2 database for fast iteration and tests (requires adding H2 to `pom.xml` and adjusting `spring.datasource.url=jdbc:h2:mem:storedb`).

Production considerations:
- Use Flyway or Liquibase for controlled, versioned schema migrations instead of `ddl-auto=update`.
- Store secrets (DB password) outside `application.properties` in environment variables or a secrets manager.

---

## Testing strategy and examples

The project currently has one smoke test (`@SpringBootTest` context loads). For better coverage and learning, add:

1. Unit tests for `ProductService` using Mockito to mock `ProductRepository`.
   - Assert that `save`, `updateProduct`, and `deleteById` call the repository correctly and handle edge cases.
2. Controller tests using `@WebMvcTest(ProductController.class)` and `MockMvc` to simulate HTTP requests and assert view names, model attributes, redirects, and status codes.
3. Integration tests using H2 or Testcontainers (MySQL in a Docker container) to run repository and service code against a real DB.

Run tests with:

```sh
./mvnw test
```

Suggested minimal example (conceptual):
- Unit test: `ProductServiceTest` that stubs `repo.findById(...)` to return an `Optional.of(product)` and asserts `updateProduct` returns updated values.
- MVC test: `ProductControllerTest` using `MockMvc` to perform GET `/products` and expect status 200 and view `view`.

---

## How to run the app locally (exact commands)

Prerequisites
- Java (the version defined in `pom.xml`; probably Java 17+). Check `maven.compiler.source` in `pom.xml`.
- Maven (or use the included Maven wrapper `./mvnw`).
- MySQL server (unless you switch to H2 for local dev).

If you use MySQL, create the database and user (example commands for MySQL client):

```sql
CREATE DATABASE storedb;
CREATE USER 'storeuser'@'localhost' IDENTIFIED BY 'StrongPassword123';
GRANT ALL PRIVILEGES ON storedb.* TO 'storeuser'@'localhost';
FLUSH PRIVILEGES;
```

Start the application with the Maven wrapper:

```sh
./mvnw spring-boot:run
```

Open the app in your browser:
- Home: http://localhost:8080/
- Products: http://localhost:8080/products

To run tests:

```sh
./mvnw test
```

To run the JAR (build then run):

```sh
./mvnw package
java -jar target/*.jar
```

If you want to use H2 for quick local runs, add the H2 dependency to `pom.xml` and change `application.properties` to:

```
spring.datasource.url=jdbc:h2:mem:storedb;DB_CLOSE_DELAY=-1
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

This starts an in-memory DB and recreates schema on each run.

---

## Suggested exercises (with small steps)

1. Add a `description` field to `Product`:
   - Add the field in `Product.java` with getter/setter.
   - Update `add.html` and `update.html` to include a description input.
   - Update controllers to accept and persist the new field.
   - Run app and verify new products show descriptions.

2. Add form validation for the `add` form:
   - Create a `ProductCreateDto` with validation annotations.
   - In `POST /add`, bind to the DTO and accept `BindingResult`.
   - If errors, re-render the `add` template and show error messages.

3. Make the `PUT /products/{id}` endpoint return proper 404 when product not found and 400 when validation fails:
   - Add a `@ControllerAdvice` to handle `MethodArgumentNotValidException` and `EntityNotFoundException`.
   - Return structured JSON errors for API clients.

4. Add search and pagination:
   - Add repository method `Page<Product> findByNameContaining(String q, Pageable pageable)`.
   - Modify controller to accept `page` and `size` parameters and pass a `Pageable` to the repository.
   - Update `view.html` to show next/previous links.

5. Write tests for service & controller:
   - Unit test `ProductService` with mocks.
   - `@WebMvcTest` for `ProductController` to assert model attributes and correct view names.

6. Dockerize the app and database:
   - Create `Dockerfile` for the Spring Boot app.
   - Add `docker-compose.yml` for MySQL and the app (make sure DB credentials match `application.properties` or use environment variables).

---

## Troubleshooting & tips

- If templates don't update after editing, ensure `spring.thymeleaf.cache=false` (dev) and rebuild if necessary.
- If JPA complains about missing tables or columns when using `ddl-auto=update`, check the database connection and ensure the DB user has privileges.
- If you prefer not to install MySQL locally, use H2 or Testcontainers for tests and local runs.
- When debugging controllers, use `logger.debug(...)` or run with `logging.level.org.springframework=DEBUG` in `application.properties` to get more information.

---

## Extra deep-dive sections (pom.xml, lifecycle, entity states, transactions, HTTP verbs, and errors)

### `pom.xml` and key dependencies

Open `pom.xml` to see exactly which Spring Boot starters and libraries are included (this project uses the Spring Boot starters that provide cohesive sets of dependencies and sensible defaults). Key dependency types you'll commonly see and why they're used:

- `spring-boot-starter-web` — includes Spring MVC, Jackson (for JSON) and an embedded servlet container (Tomcat by default). Needed for controllers and REST endpoints.
- `spring-boot-starter-thymeleaf` — Thymeleaf view resolver and template engine integration for server-side HTML rendering.
- `spring-boot-starter-data-jpa` — Spring Data JPA + Hibernate for ORM and repository support.
- `jakarta.validation-api` / `hibernate-validator` (transitively via `spring-boot-starter-validation`) — Bean Validation API used for `@Valid` and constraint annotations.
- JDBC driver (e.g., `mysql-connector-java`) — the JDBC driver required to talk to the configured database.
- `spring-boot-starter-test` — testing utilities (JUnit, Mockito, Spring Test) for writing unit and integration tests.

Why starters matter: Spring Boot starters group a sensible set of dependencies so you don't need to declare many transitive libraries manually. The auto-configuration system uses the presence of these libraries to enable features automatically.


### Application lifecycle & bean creation (what happens when you run the app)

When you run `./mvnw spring-boot:run` or `SpringApplication.run(...)`, Spring Boot performs these high-level steps:
1. Create a `SpringApplication` instance and determine sources (your `@SpringBootApplication` class).
2. Prepare an `ApplicationContext` (usually `AnnotationConfigServletWebServerApplicationContext` for web apps).
3. Perform classpath scanning (component scan) and discover candidate components (`@Component`, `@Controller`, `@Service`, `@Repository`).
4. Apply auto-configuration classes (based on what's on the classpath and properties) to register additional beans (e.g., `DataSource`, `EntityManagerFactory`, `PlatformTransactionManager`, Thymeleaf view resolver).
5. Instantiate and wire beans (dependency injection), resolving constructor arguments and field/setter injection as needed.
6. Run any `CommandLineRunner` or `ApplicationRunner` beans (not present in this project but useful to seed data).
7. Start the embedded servlet container (Tomcat) and begin accepting HTTP requests.

Bean scopes are usually singleton for these components. If you need different lifecycles (prototype, request, session), you can annotate beans accordingly.


### JPA entity lifecycle states (brief, important to understand)

An entity instance transitions through several states which are important for understanding persistence behaviour:
- Transient: the entity is created with `new` and not associated with a persistence context. No DB identity yet.
- Managed (persistent): after `entityManager.persist(entity)` or repository `save`, the entity is managed by the persistence context. Changes to fields within a transaction are tracked and flushed to the DB.
- Detached: after the persistence context is closed (e.g., transaction ended) the entity becomes detached — changes are not automatically persisted unless merged or saved again.
- Removed: entity scheduled for deletion within a transaction.

Why it matters: the `updateProduct` method in `ProductService` finds an entity (managed inside the transactional scope), modifies fields, and saves — if the method isn't transactional, you might accidentally run into detached entities or multiple repository calls. Use `@Transactional` to control persistence context boundaries when doing updates.


### Transactions & propagation (when to use `@Transactional`)

- `@Transactional` starts a transaction on public methods by default when applied at the class or method level (requires a `PlatformTransactionManager` bean — auto-configured by Spring when JPA is present).
- Use `@Transactional` for methods that perform writes (`save`, `update`, `delete`) or read sequences that require repeatable reads.
- Propagation basics:
  - `REQUIRED` (default): join an existing transaction or start a new one if none exists.
  - `REQUIRES_NEW`: always start a new transaction, suspending the existing one.
  - `SUPPORTS`, `NOT_SUPPORTED`, `MANDATORY`, `NEVER`, `NESTED` — advanced use-cases.

Example: annotate `updateProduct` with `@Transactional` so the find + modify + save happen in one atomic unit.


### HTTP methods mapping to CRUD (RESTful mapping)

A conventional mapping from HTTP verbs to CRUD operations:
- GET — read operations (list or single item). In this app: `GET /products`, `GET /products/{id}/edit` (returns HTML form to edit).
- POST — create operations (form submission that creates a resource). In this app: `POST /add`.
- PUT/PATCH — update operations (idempotent). In this app: `PUT /products/{id}` accepts JSON and updates a product.
- DELETE — delete operations (idempotent). This app uses a `POST` to `/products/delete` due to HTML form limitations (forms support GET and POST). A more RESTful API would use `DELETE /products/{id}` from a client that supports all verbs (AJAX or REST client).

Idempotency note:
- PUT should be idempotent — sending the same PUT multiple times yields the same state. POST is not idempotent by default.


### Error handling patterns and best practices

The project currently does basic redirects on errors. For maintainability, prefer these patterns:

1. For server-rendered pages (Thymeleaf forms):
   - Use form binding (`@ModelAttribute` or a command object) with `@Valid` and `BindingResult`.
   - If `BindingResult.hasErrors()` re-render the form with error messages bound to fields.
   - Keep error messages in `messages.properties` for localization.

2. For REST endpoints (JSON):
   - Use a `@ControllerAdvice` with `@ExceptionHandler` methods to map exceptions to appropriate HTTP status codes and JSON error shapes (e.g., 400 for validation errors, 404 for not-found, 500 for server errors).
   - Example mapping: `MethodArgumentNotValidException` -> HTTP 400 with a `errors` array of `{ field, message }`.

3. For data access errors:
   - Let `DataAccessException` bubble to a `@ControllerAdvice` or map it to a 500 with a helpful message.

4. Logging:
   - Log exceptions with `logger.error(...)` including contextual IDs (product id, request id) and avoid logging sensitive info.


### Validation failure handling and HTTP status codes

- When `@Valid` fails on `@RequestBody` (JSON), Spring throws `MethodArgumentNotValidException`. The `@ControllerAdvice` can catch it and return HTTP 400 with a JSON body describing field errors.
- When `@Valid` fails with form binding and `BindingResult` is present, you should re-render the form with errors shown inline and return HTTP 200 (the edit page) so the user can correct inputs.
- When a resource isn't found, return HTTP 404 for API endpoints and redirect to a friendly page for server-rendered apps.

Example mapping (conceptual):
- Validation error -> 400 Bad Request with:
  ```json
  { "errors": [ { "field": "name", "message": "must not be blank" }, { "field": "price", "message": "must be >= 0" } ] }
  ```
- Not found -> 404 Not Found with:
  ```json
  { "error": "Product not found", "id": 123 }
  ```
- Server error -> 500 Internal Server Error with a safe message for clients and detailed info in server logs.


## Best practices & common pitfalls

- Keep controller methods thin: controllers should orchestrate request/response and delegate business logic to services.
- Use constructor injection over field injection: it makes dependencies explicit and improves testability.
- Use DTOs for API boundaries (request and response) to avoid over-binding to JPA entities and to control serialization shapes.
- Avoid `spring.jpa.hibernate.ddl-auto=update` in production. Use Flyway or Liquibase for deterministic migrations.
- Watch for N+1 queries: when loading associations, prefer appropriate fetch strategies or explicit joins when necessary.
- Externalize secrets and environment-specific settings: use environment variables or a secrets manager rather than committing credentials.
- Write meaningful tests: unit tests for business logic, slice tests (`@WebMvcTest`) for controllers, and integration tests for repository layers.
- Log at appropriate levels (DEBUG for dev, INFO for normal ops, WARN/ERROR for problems) and avoid logging sensitive data (passwords, secrets).

Common pitfalls specific to this project:
- Not creating the MySQL database before running the app (causes connection errors). Use H2 for quick local testing if you don't want to provision MySQL.
- Forgetting to add `th:name` attributes or correct `name` attributes in forms so controller `@RequestParam` bindings work.
- Misusing `@Controller` vs `@RestController` which can lead to returning view names as literal JSON or vice-versa.
- Assuming entities remain managed outside transactional boundaries — be explicit with `@Transactional` on update methods.


## References & further reading

- Spring Boot reference: https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Thymeleaf documentation: https://www.thymeleaf.org/
- Bean Validation (Jakarta Validation): https://beanvalidation.org/
- Flyway (DB migrations): https://flywaydb.org/
- Testing Spring Boot: https://spring.io/guides/gs/testing-spring-boot/

