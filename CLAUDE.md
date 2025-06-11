# Book Club Application - Claude Context

## Project Overview
A Spring Boot web application for managing reading lists with user authentication, role-based access control, and comprehensive book management features.

## Technology Stack & Versions
- **Java**: 17
- **Spring Boot**: 3.4.3
- **Spring Security**: 6
- **Spring Data JPA**: Latest with Spring Boot 3.4.3
- **Database**: MySQL 8.0+
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven
- **Architecture**: Layered (Controller → Service → Repository → Entity)

## Development Commands

### Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Run tests
mvn test

# Run specific test
mvn test -Dtest=ClassName
```

## Code Style & Conventions

### Package Structure
Follow this established package structure:
```
com.bookclub
├── controller/     # REST controllers and web controllers
├── service/        # Business logic layer
├── repository/     # Data access layer
├── entity/         # JPA entities
├── dto/           # Data transfer objects
├── config/        # Configuration classes
├── security/      # Security-related classes
└── exception/     # Custom exceptions and handlers
```

### Naming Conventions
- **Controllers**: `BookController`, `ReadingListController`, `UserController`
- **Services**: `BookService`, `ReadingListService`, `UserService`
- **Repositories**: `BookRepository`, `ReadingListRepository`, `UserRepository`
- **Entities**: `Book`, `ReadingList`, `User`, `UserRole`
- **DTOs**: `BookDto`, `ReadingListDto`, `UserDto`

### Annotation Patterns
- Use `@RestController` for API endpoints (`/api/**`)
- Use `@Controller` for web pages (Thymeleaf templates)
- Service classes: `@Service` with constructor injection
- Repository interfaces: Extend `JpaRepository<Entity, Long>`
- Validation: Use `@Valid` with appropriate validation annotations

## Security Implementation

### Role-Based Access Control
- **ROLE_USER**: Manage own reading lists, view public books
- **ROLE_ADMIN**: Full access to users, books, and all reading lists

### Endpoint Security Patterns
```java
// Public endpoints
@GetMapping("/api/books/public/**")

// User-only endpoints  
@PreAuthorize("hasRole('USER')")
@PostMapping("/api/readinglists")

// Admin-only endpoints
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/api/books")
```

## Database Design

### Credentials
- MySQL database required: bookclub (created automatically)
- Default credentials: springstudent/springstudent
- Database configuration in src/main/resources/application.properties

### Key Entities & Relationships
- **User** (1:N) **ReadingList**: Users can have multiple reading lists
- **ReadingList** (M:N) **Book**: Many-to-many through `reading_list_books` table
- **User** (M:N) **UserRole**: User roles via `user_roles` table

### Important Database Constraints
- MySQL database with `createDatabaseIfNotExist=true`
- Default connection: `localhost:3306/bookclub`
- Use `@CreationTimestamp` and `@UpdateTimestamp` for audit fields
- Implement soft deletes where appropriate for data integrity

## API Design Patterns

### RESTful Conventions
- **Collections**: `/api/books`, `/api/readinglists`
- **Resources**: `/api/books/{id}`, `/api/readinglists/{id}`
- **Sub-resources**: `/api/readinglists/{readingListId}/books/{bookId}`
- **Search**: `/api/books/public/search?query=`
- **Public endpoints**: Use `/public` suffix for unauthenticated access

### Response Patterns
- Return appropriate HTTP status codes (200, 201, 404, 403, etc.)
- Use consistent error response format
- Implement proper pagination for list endpoints
- Return DTOs, not entities, in API responses

## Business Logic Guidelines

### Service Layer Responsibilities
- **BookService**: Book CRUD, search functionality, validation
- **ReadingListService**: Reading list management, book associations
- **UserService**: User management, authentication support
- **SecurityService**: Authentication, authorisation checks

### Key Business Rules
- Users can only access their own reading lists
- Admins can manage all resources
- Books must be unique by ISBN (if implemented)
- Reading lists must have unique names per user
- Implement proper cascade operations for entity relationships

## Testing Strategy

### Test Structure
```
src/test/java/
├── controller/    # @WebMvcTest for controllers
├── service/       # @MockitoExtension for services  
├── repository/    # @DataJpaTest for repositories
└── integration/   # @SpringBootTest for full integration
```

### Testing Patterns
- Mock external dependencies in unit tests
- Use `@Transactional` for database tests
- Test security with `@WithMockUser` annotations
- Validate API responses and error handling

## Configuration Management

### Application Properties Structure
```properties
# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/bookclub?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false (production)

# Security
spring.security.user.name=admin
spring.security.user.password=admin
```

### Profile-Specific Configurations
- `application-dev.properties`: Development settings
- `application-test.properties`: Test environment
- `application-prod.properties`: Production configuration

## Common Patterns & Best Practices

### Exception Handling
- Use `@ControllerAdvice` for global exception handling
- Create custom exceptions: `BookNotFoundException`, `UnauthorisedAccessException`
- Return meaningful error messages and appropriate HTTP status codes

### Validation Patterns
```java
// Entity validation
@NotBlank(message = "Book title is required")
private String title;

// DTO validation
@Valid @RequestBody BookDto bookDto
```

### Security Patterns
```java
// Check resource ownership
@PreAuthorize("#readingListId == authentication.principal.id or hasRole('ADMIN')")

// Method-level security
@PreAuthorize("@readingListService.isOwner(#id, authentication.name)")
```

## Known Constraints & Considerations

### Current Limitations
- Single database configuration (MySQL only)
- Basic authentication (no OAuth2/JWT)
- No email verification implemented yet
- No book rating/review system

### Performance Considerations
- Implement pagination for book listings
- Use proper indexing on frequently queried fields
- Consider caching for frequently accessed data
- Optimise N+1 query problems with `@EntityGraph`

## Development Guidelines

### Code Quality
- Follow SOLID principles, especially Single Responsibility
- Keep controllers thin - delegate to services
- Use meaningful exception messages
- Document complex business logic
- Maintain consistent error handling patterns

### When Adding New Features
- Follow the established layered architecture
- Add proper security annotations
- Include comprehensive tests
- Update API documentation
- Consider database migration scripts if schema changes

### Refactoring Preferences
- Prioritise maintainability over minimal changes
- Extract common functionality into utility classes
- Use Spring's built-in features rather than custom solutions
- Keep methods focused and well-named

## Future Enhancement Considerations
- Email verification system
- Password reset functionality  
- Book rating and review system
- Reading progress tracking
- External API integration (Google Books, etc.)
- Microservices architecture for scalability