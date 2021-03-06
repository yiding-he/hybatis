[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

_[中文版 |Chinese version](README_zh-CN.md)_

# Hybatis

A MyBatis extension for [Spring Boot](https://spring.io/projects/spring-boot) projects.

It generates functions for unmapped Mapper methods.

### Usage

#### 1. Add `@Import(HybatisConfigurator.class)` to your `@SpringBootApplication` class:

```java
@SpringBootApplication
@Import(HybatisConfigurator.class)
public class Application {
    // ...
}
```

#### 2. Sample Mapper class:

Any unmapped method in the Mapper class will be processed by Hybatis, 
and be mapped automatically if possible.

```java
@Mapper
public interface SampleMapper {
    
    // Mapped if method is annotated by Mybatis annotation or associated with XML script.
    // Hybatis will ignore this method
    @Select("SELECT * FROM sample")
    List<Sample> selectAll();
    
    // Unmapped method, will be processed by Hybatis
    int insertSample(Sample sample);
}
```

You don't need to write any code for unmapped methods. Method `insertSample()` can be used instantly.

```java
@Service
public class SampleService {
    
    @Autowired
    private SampleMapper sampleMapper;
    
    public int saveNewSample(Sample sample) {
        return sampleMapper.insertSample(sample);
    }
}
```

For querying, Hybatis provides a `Condition` class. To use it, 
you need to create a query class:

```java
@Data
@HbQuery(table = "sample")
public class SampleQuery {
    private Condition<String> name;
    private Condition<Long> id;
}
```

Then you can use `SampleQuery` as parameter in mapper methods:

```java
@Mapper
public interface SampleMapper {

    // Unmapped method, will be processed by Hybatis
    List<Sample> selectSamples(SampleQuery query);

}
```

For SpringMVC framework, `SampleQuey` can be easily parsed by a SpringMVC Controller:

```java
@RestController
public class SampleController {
    
    @Autowired
    private SampleMapper sampleMapper;
    
    // Query sample:
    // curl "http://localhost:8080/query?name.contains=John&id.in=1,2,3"
    // "contains" and "in" are directives defined in `Condition` class.
    @GetMapping("/query")
    public List<Sample> selectSamples(SampleQuery query) {
        return sampleMapper.selectSamples(query);
    }
}
```