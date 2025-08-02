[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

_[中文版 |Chinese version](README_zh-CN.md)_

<img src="docs/imgs/logo.png" alt="LOGO" width="300">

A MyBatis extension framework for use in [Spring Boot](https://spring.io/projects/spring-boot "Spring Boot") projects.

Main features provided by Hybatis:

1. Direct SQL execution
2. Provides a basic `CrudMapper` interface
3. Encapsulates query conditions passed directly from a SpringMVC Controller to a Mapper

## 0. Enabling Hybatis

To enable Hybatis, you first need to compile this project and include it in your 
project's dependencies. If you use Maven, you can install it to your local 
Maven repository directly with `mvn install -maven.test.skip=true`.

### Adding Hybatis to your project

In a Spring Boot project that includes the MyBatis framework, you just need to 
add the`@Import(HybatisConfigurator.class)` annotation to your configuration class. 
For example:

```java 
@SpringBootApplication
@Import(HybatisConfigurator.class)  // Add this line
public class MyApplication {
   public static void main(String[] args) {
      SpringApplication.run(MyApplication.class, args);
   }
}
```


## 1. Executing SQL

After enabling Hybatis, you will have a `Hybatis` object that can be injected 
at any time via `@Autowired`. Here is an example:

```java 
@Service
public class SampleService {
    
    @Autowired
    private Hybatis hybatis;
    
    // Execute an insert statement
    public void insert(User user) {
        val sql = "insert into user(id, name) values(?,?)";
        hybatis.execute(sql, user.getId(), user.getName());
    }
    
    // Or use the encapsulated Insert object
    public void insert2(User user) {
        hybatis.execute(Sql.Insert("user")
                .Values("id", user.getId())
                .Values("name", user.getName())
        );
    }
    
    // Query results are encapsulated in a Row object, which is a subclass of Map
    public List<Row> queryUsersByKeyword(String userNameKeyword) {
        return hybatis.queryList(
                "select * from user where name like ?", "%" + userNameKeyword + "%");
    }
    
    // Execute a database transaction
    public void insertUsers(List<User> users) {
        val sql = "insert into user(id, name) values(?,?)";
        hybatis.runTransaction(() -> {
            for (User user : users) {
                hybatis.execute(sql, user.getId(), user.getName());
            }
        });
    }
    
    // Execute a batch
    public void insertUsers2(List<User> users) {
        var batchCommand = new BatchCommand();
        batchCommand.setStatement("insert into user(id, name) values(?, ?)");
        batchCommand.setParams(users.stream().map(
                user -> List.of(user.getId(), user.getName())
        ));
        var effectedRows = hybatis.execute(batchCommand);
    }
    
    // Automatically insert large amounts of data in batches
    public void insertUsers3(Stream<User> users) {
        var sql = "insert into user(id, name) values(?,?)";
        var batchSize = 100;
        var batchExecutor = new BatchExecutor(hybatis, sql, batchSize);
        
        users
            .map(user -> List.of(user.getId(), user.getName()))
            .forEach(list -> batchExecutor.feed(list));
        
        batchExecutor.finish();
    }
}
```


## 2. Handling Dynamic Conditions

This framework will automatically generate and register a `MappedStatement` for Mapper methods 
that meet the conditions, which saves developers some work:

1. Developers do not need to write SQL for these methods in annotations or XML;
2. Developers do not need to maintain the SQL corresponding to these methods due to 
   changes in table design.

### Implementation principle:

1. When the SpringBoot project starts, Hybatis will automatically scan all Mapper interfaces 
   and find methods that have not been registered with the MyBatis framework;
2. It iterates through these methods. If a method meets the prerequisites, Hybatis will 
   generate a `MappedStatement` for it and register it with Mybatis.

Note: You can add Hybatis to an existing project at any time. Hybatis will not change 
      the Mapper methods that have already been implemented in the project.

### Which Mapper methods can Hybatis handle?

1. The method must be annotated with `@HbSelect`,`@HbInsert`, or `@HbUpdate` to identify 
   the operation type;
2. Methods annotated with `@HbSelect` must have only one parameter, and the parameter type 
   must meet one of the following conditions:
    1. The type is`com.hyd.hybatis.Conditions`
    2. It is a JavaBean class that contains one or more members of type`com.hyd.hybatis.Condition`;
3. Methods annotated with`@HbInsert` must have only one parameter, and the parameter type 
   must be a JavaBean class;
4. Methods annotated with`@HbUpdate` must have two parameters. The first parameter 
   meets condition 2, and the second parameter meets condition 3;

> Note: If an `@HbSelect` method returns a numeric type, Hybatis will consider it a count method
> and generate SQL like `select count(1) from ...`

### Usage steps

> About testing: The example database is packaged in the `src/test/data/employees.7z` file. 
> Unzip the file content into the `.local/` folder under the project root directory 
> (please create the `.local/` folder yourself) to run the `HybatisSpringBootTestApplication` class 
> in `src/test/java`

#### 1. Add the`@Import(HybatisConfigurator.class)`annotation to the`@SpringBootApplication` class:

```java 
@SpringBootApplication
@Import(HybatisConfigurator.class)
public class Application {
    // ...
}
```

#### 2. Take the following Mapper interface as an example:

Hybatis will handle the methods in the Mapper interface that have not been handled by Mybatis 
and meet Hybatis's own prerequisites.

```java 
@Mapper
public interface SampleMapper {
    
    // This method has been handled by Mybatis, so Hybatis will not handle it.
    @Select("SELECT * FROM sample")
    List<Sample> selectAll();
    
    // This method has neither a Mybatis annotation nor is it associated with XML, so Mybatis will not handle it.
    // Hybatis finds that this method is annotated with @HbInsert, so it will automatically generate a 
    // MappedStatement and register it with Mybatis.
    @HbInsert(table = "sample")
    int insertSample(Sample sample);
}
```


### Writing Queries

Hybatis provides the `com.hyd.hybatis.Condition` class to encapsulate the query conditions 
for a field. When `Condition` is used as a Mapper method parameter, Hybatis will automatically 
generate a dynamic query condition for the parameter:

```java 
@Mapper
public interface SampleMapper {
    
    @HbSelect(table = "sample")
    List<Sample> selectByCondition(Condition condition);
    
    // Usage example
    default List<Sample> selectById(Long id) {
        Condition condition = Condition.of("id").eq(id);
        return selectByCondition(condition);
    }
}
```

If there are multiple query conditions, you can use the `com.hyd.hybatis.Conditions` class 
to encapsulate them:

```java 
@Mapper
public interface SampleMapper {
    
    @HbSelect(table = "sample")
    List<Sample> selectByConditions(Conditions conditions);
    
    // Usage example
    default List<Sample> selectByConditions(String nameKeyword, int minId, int maxId) {
        Conditions conditions = new Conditions()
                .with("id", c -> c.between(minId, maxId))
                .with("name", c -> c.contains(nameKeyword));
        return selectByConditions(conditions);
    }
}

// Conditions can be passed directly from a SpringMVC Controller method:
@Controller
public class SampleController {
    
    @Autowired
    private SampleMapper sampleMapper;
    
    // Usage example
    // curl "http://host:port/sample/search?projection=id,name&id.between=1,9&name.contains=test&limit=5"
    // will result in the query "SELECT id, name FROM sample WHERE id BETWEEN 1 AND 9 AND name LIKE '%test%' LIMIT 5".
    @GetMapping("/sample/search")
    public List<Sample> selectByConditions(Conditions conditions) {
        return sampleMapper.selectByConditions(conditions);
    }
}
```

You can also encapsulate conditions in a JavaBean:

```java 
import com.hyd.hybatis.Condition;
@Data
public class SampleQuery {
    private Condition<String> nameKeyword;
}
@Mapper
public interface SampleMapper {
    @HbSelect(table = "sample")
    List<Sample> selectByQuery(SampleQuery query);
    
    // Usage example
    default List<Sample> selectByQuery(String nameKeyword) {
        SampleQuery query = new SampleQuery();
        query.setNameKeyword(new Condition<>().contains(nameKeyword));
        return selectByQuery(query);
    }
}

// SampleQuery can be passed directly from a SpringMVC Controller method:
@Controller
public class SampleController {
    
    @Autowired
    private SampleMapper sampleMapper;
    
    // Usage example
    // curl http://host:port/sample/search?nameKeyword.contains=test
    // will result in the query condition "name_keyword LIKE '%test%'".
    @GetMapping("/sample/search")
    public List<Sample> selectByConditions(SampleQuery query) {
        return sampleMapper.selectByQuery(query);
    }
}
```
