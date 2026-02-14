# Hybatis Agent Guidelines

This file contains guidelines for agentic coding assistants working on the Hybatis project.

## Agent Restrictions

**LLM agents are not allowed to run any shell command or build/compile code, only text editing is allowed.**

## Build and Test Commands

Hybatis is a Maven-based Java project using Spring Boot 2.7.5 and Java 11.

### Build Commands
- `mvn clean compile` - Clean and compile the project
- `mvn package` - Build the JAR with sources
- `mvn install` - Install to local Maven repository
- `mvn install -DskipTests` - Install without running tests

### Test Commands
- `mvn test` - Run all tests
- `mvn test -Dtest=ClassName` - Run specific test class
- `mvn test -Dtest=ClassName#methodName` - Run specific test method
- `mvn test -Dtest=com.hyd.hybatis.tests.mapper.EmployeeMapperTest#testSelectByQuery` - Example single test

### Profiles
- `mvn install -P mainland-prc-china` - Use Aliyun Maven repository (China)
- `mvn deploy -P github` - Deploy to GitHub Packages
- `mvn deploy -P maven-central-for-deploy` - Deploy to Maven Central (requires GPG signing)

## Code Style Guidelines

### Indentation and Formatting
- Java: 4 spaces indentation (defined in .editorconfig)
- XML: 2 spaces indentation
- End of line: LF (Unix-style)
- Trim trailing whitespace in Java/XML files
- Insert final newline in Java/XML files

### Imports
- Organize imports logically: standard library, third-party, project-specific
- Use wildcard imports sparingly; prefer specific imports
- Place static imports at the top, grouped separately

### Naming Conventions
- Classes: PascalCase (e.g., `CrudMapper`, `HybatisConfiguration`)
- Methods: camelCase (e.g., `queryOne`, `findById`)
- Variables: camelCase (e.g., `dataSource`, `sqlSessionFactory`)
- Constants: UPPER_SNAKE_CASE (e.g., `EMPTY`, `serialVersionUID`)
- Packages: lowercase with dots (e.g., `com.hyd.hybatis.sql`)
- Database columns: snake_case (automatically mapped to camelCase in Java)

### Type and Generics
- Use generic types properly: `Condition<T>`, `List<T>`, `CrudMapper<T>`
- Always specify generic type parameters (avoid raw types)
- Use `@SuppressWarnings("unchecked")` only when unavoidable and document why
- Declare method return types explicitly (avoid `var` for public APIs)

### Annotations
- Use Lombok annotations (`@Data`, `@Getter`, `@Slf4j`) to reduce boilerplate
- Use `@Deprecated` for deprecated methods with explanation
- Use `@Override` consistently for interface implementations
- Annotate public methods with Javadoc when part of public API

### Error Handling
- Wrap checked exceptions in runtime exceptions where appropriate
- Use custom `HybatisException` for framework-specific errors
- Log exceptions with appropriate levels using SLF4J
- Use `SQLExceptionWrapper` to unwrap SQLException in stream operations

### Method Design Patterns
- Return `this` in builder-style methods for method chaining (e.g., `Condition.eq().lt()`)
- Use Optional or return null for optional single results
- Use functional interfaces for callbacks: `RowConsumer`, `EntityConsumer<T>`, `ConnectionFunction<T>`
- Provide default methods in interfaces for common operations (see `CrudMapper`)

### Stream and Resource Management
- Always close `Stream<Row>` and `Stream<T>` returned from query methods
- Use try-with-resources for JDBC resources
- Add onClose handlers to streams for cleanup

### Logging
- Use SLF4J with `@Slf4j` annotation
- Log SQL statements at DEBUG level: `log.debug("Preparing sql: {} {}", statement, params)`
- Log exceptions at WARN or ERROR level appropriately

### Mapper Interface Patterns
- Use `@HbSelect`, `@HbInsert`, `@HbUpdate`, `@HbDelete` annotations for auto-generated SQL
- Return `List<T>` for multiple results, `T` or `Row` for single results
- Return `Long` for count queries (numeric return types)
- Parameter types: `Condition`, `Conditions`, or JavaBean with `Condition` fields

### Structured Query API (New in dev-new-query-model)
- Use `Table.of(EntityClass)` to create query objects from entity classes
- Use `Column` interface for type-safe column references with lambda expressions: `col(Entity::getField)`
- Use `Filter` static methods to create conditions: `Filter.equal(col, value)`, `Filter.in(col, list)`
- Chain filters with `Filter.AND(...)` and `Filter.OR(...)` for composite conditions
- Use `Query.leftJoin()` and `Query.join()` for joins between queries
- Use `Column` factory methods for expressions: `Column.sum()`, `Column.count()`, `Column.concat()`, `Column.cases()`
- Avoid circular references in subqueries - use separate `Table` instances for inner queries
- The framework automatically detects circular references in EXISTS subqueries

### Circular Reference Detection
- EXISTS subqueries cannot reference the same query object used in outer query
- Create new `Table` instances for subqueries to avoid circular references
- Framework throws `IllegalStateException` with detailed message on circular reference detection

### Constants and Utilities
- Place utility classes in `com.hyd.hybatis.utils` package
- Static factory methods: `Condition.of()`, `Sql.Insert()`, `Sql.Select()`
- String conversion utilities: `Str.camel2Underline()`, `Str.underline2Camel()`

### Thread Safety
- Most classes are NOT thread-safe (meant for Spring-managed singletons)
- Document thread-safety requirements in class javadoc
- Use thread-safe collections when needed (Caffeine cache, etc.)

### Testing
- Extend `HybatisSpringBootTestApplicationTest` for integration tests
- Use JUnit 5 (`@Test`, `@BeforeEach`, `@AfterEach`)
- Use `@Autowired` for dependency injection in tests
- Assert with `org.junit.jupiter.api.Assertions`
- Test database files in `src/test/data/` (extracted to `.local/` for runtime)

### Project Structure
- Main code: `src/main/java/com/hyd/hybatis/`
- Test code: `src/test/java/com/hyd/hybatis/`
- Key packages: `sql`, `statement`, `mapper`, `jdbc`, `utils`, `annotations`
