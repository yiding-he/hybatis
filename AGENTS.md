# Hybatis - Agent Instructions

## 构建限制

**禁止执行编译或构建命令。** LLM Agent 不允许运行以下类型的命令：
- `mvn compile`, `mvn clean compile`
- `mvn test`, `mvn test -Dtest=...`
- `mvn package`, `mvn install`
- 任何其他 Maven 构建命令

代码编写完成后，直接结束任务，由用户自行编译和测试。

## 项目结构

- Java 17, Maven build
- Spring Boot 3.4.0, MyBatis 3.5.11
- Base package: `com.hyd.hybatis`
- Source: `src/main/java/com/hyd/hybatis/`
- Tests: `src/test/java/com/hyd/hybatis/`

## Code Style

### Formatting
- Indent: 4 spaces (Java), 2 spaces (XML)
- Line endings: LF
- Charset: UTF-8
- Max line length: ~120 chars (follow existing patterns)

### Imports
- Group imports: java.* first, then javax.*, then third-party, then project
- Use `var` for local variable type inference (Java 10+)
- Use static imports for test assertions: `import static org.junit.jupiter.api.Assertions.*`

### Naming
- Classes: PascalCase (e.g., `HybatisCore`, `SqlHelper`)
- Methods: camelCase, start with verb (e.g., `processMapperMethod`, `buildSelect`)
- Fields: camelCase (e.g., `mappedStatementFactories`)
- Constants: UPPER_SNAKE_CASE
- Test classes: Suffix with `Test` (e.g., `HybatisBasicTest`)

### Types
- Prefer `var` for local variables
- Use `List<T>`, `Map<K,V>` from `java.util`
- Use Lombok `@Data`, `@Slf4j` for boilerplate reduction

### Error Handling
- Use specific exceptions, wrap in `HybatisException` when appropriate
- Log errors with SLF4J: `log.error("message", exception)`
- Prefer Optional over null where applicable

### Annotations
- Use Lombok: `@Data`, `@Slf4j`, `@NoArgsConstructor`
- Spring: `@SpringBootTest` for integration tests
- Test: JUnit 5 (`@Test`, `@BeforeEach`)

### Comments
- Javadoc for public APIs
- Use `//` for inline comments, `/* */` for multi-line
- Separate logical sections with comment banners: `////////////////////////////////////////`

### Testing
- Extend `HybatisSpringBootTestApplicationTest` for Spring Boot tests
- Use `@Autowired` to inject `Hybatis` instance
- Use JUnit 5 assertions: `assertTrue()`, `assertFalse()`, `assertEquals()`
- Test methods are `public void` and throw `Exception`

## Key Conventions

1. Use `var` keyword for local variables (Java 10+)
2. Method chaining style: `Sql.Select("*").From(tableName)`
3. Log with SLF4J via Lombok `@Slf4j`
4. Use `Row` class for dynamic query results
5. Entity annotations: `@HbEntity`, `@HbColumn` for table mapping
6. Mapper interfaces extend `CrudMapper<T>` for CRUD operations
