[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

_[中文版 | Chinese version](README_zh-CN.md)_

# Hybatis

A MyBatis extension framework for [Spring Boot](https://spring.io/projects/spring-boot) projects.

## Features

1. **Direct SQL Execution** - Execute SQL queries directly without writing XML mappers
2. **CRUD Mapper Interface** - Predefined CRUD operations with automatic SQL generation
3. **Dynamic Query Conditions** - Flexible query condition building with Spring MVC integration
4. **Batch Operations** - Efficient batch processing for large datasets
5. **Transaction Management** - Built-in transaction support with Spring integration

## Spring Boot 3 Branch

This is the Spring Boot 3 branch. Note the following requirements:

### 1. Clone Command

Use the following command to clone this branch:

```bash
git clone https://github.com/yiding-he/hybatis.git --depth=1 --branch=spring-boot-3
```

### 2. Build Requirements

This project requires JDK 17. If your javac path is `[JAVA17_JAVAC]`, use this Maven command:

```shell
mvn -Dmaven.compiler.fork=true -Dmaven.compiler.executable=[JAVA17_JAVAC] -Dmaven.test.skip=true clean install
```

## Quick Start

### Installation

Add Hybatis to your Spring Boot project:

```java
@SpringBootApplication
@Import(HybatisConfigurator.class)  // Enable Hybatis
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

### Basic Usage

```java
@Service
public class SampleService {
    
    @Autowired
    private Hybatis hybatis;
    
    // Execute insert SQL
    public void insert(User user) {
        String sql = "insert into user(id, name) values(?,?)";
        hybatis.execute(sql, user.getId(), user.getName());
    }
    
    // Query results as Row objects
    public List<Row> queryUsers(String keyword) {
        return hybatis.queryList(
            "select * from user where name like ?", "%" + keyword + "%");
    }
    
    // Execute transaction
    public void insertUsers(List<User> users) {
        hybatis.runTransaction(() -> {
            for (User user : users) {
                hybatis.execute("insert into user(id, name) values(?,?)", 
                    user.getId(), user.getName());
            }
        });
    }
}
```

## Core Features

### 1. Dynamic Query Conditions

Hybatis automatically generates SQL for Mapper methods with specific annotations:

```java
@Mapper
public interface UserMapper {
    
    @HbSelect(table = "user")
    List<User> selectByCondition(Condition condition);
    
    @HbSelect(table = "user")
    List<User> selectByConditions(Conditions conditions);
    
    @HbInsert(table = "user")
    int insertUser(User user);
    
    @HbUpdate(table = "user")
    int updateUser(Conditions conditions, User user);
}
```

#### Using Conditions

```java
// Single condition
Condition condition = Condition.of("name").contains("john");
List<User> users = userMapper.selectByCondition(condition);

// Multiple conditions
Conditions conditions = new Conditions()
    .with("id", c -> c.between(1, 100))
    .with("name", c -> c.contains("john"))
    .with("status", c -> c.eq("active"));
List<User> users = userMapper.selectByConditions(conditions);
```

#### Spring MVC Integration

```java
@Controller
public class UserController {
    
    @Autowired
    private UserMapper userMapper;
    
    // URL: /users/search?column=id,name&id.between=1,100&name.contains=john
    @GetMapping("/users/search")
    public List<User> searchUsers(Conditions conditions) {
        return userMapper.selectByConditions(conditions);
    }
}
```

### 2. CRUD Mapper

Extend `CrudMapper<T>` for automatic CRUD operations:

```java
@Mapper
public interface UserRepository extends CrudMapper<User> {
    // Inherits: insert, update, selectList, count, delete, selectOne, findById, updateById, deleteById
}

// Usage
User user = new User();
user.setId(1L);
user.setName("John");
userRepository.insert(user);

User found = userRepository.findById(1L);
userRepository.updateById(1L, user);
userRepository.deleteById(1L);
```

### 3. Entity Annotations

```java
@Data
@HbEntity(table = "user", primaryKeys = {"id"})
public class User {
    @HbColumn("id")
    private Long id;
    
    @HbColumn("name")
    private String name;
    
    @HbColumn("email")
    private String email;
}
```

### 4. Batch Operations

```java
// Batch insert
public void batchInsertUsers(List<User> users) {
    BatchCommand batchCommand = new BatchCommand();
    batchCommand.setStatement("insert into user(id, name) values(?, ?)");
    batchCommand.setParams(users.stream()
        .map(user -> List.of(user.getId(), user.getName()))
        .collect(Collectors.toList()));
    
    int affectedRows = hybatis.execute(batchCommand);
}

// Stream-based batch processing
public void streamInsertUsers(Stream<User> users) {
    String sql = "insert into user(id, name) values(?,?)";
    BatchExecutor executor = new BatchExecutor(hybatis, sql, 100);
    
    users.map(user -> List.of(user.getId(), user.getName())))
        .forEach(executor::feed);
    
    executor.finish();
}
```

## Requirements

- Java 17+
- Spring Boot 3.x
- MyBatis 3.5+
- Maven 3.6+

## Dependencies

- Spring Boot Starter Web
- MyBatis Spring Boot Starter
- MyBatis PageHelper (optional)
- Caffeine Cache (optional)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE.txt](LICENSE.txt) file for details.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## More Information

For detailed documentation and examples, see [README_zh-CN.md](README_zh-CN.md) (Chinese version).
