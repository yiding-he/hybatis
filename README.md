[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

_[中文版 |Chinese version](README_zh-CN.md)_

# Hybatis

这是一个 SpringBoot3 分支，clone 和安装需要注意的地方：

### 1. clone 命令

使用下面的命令来 clone 本分支：

```bash
git clone https://github.com/yiding-he/hybatis.git --depth=1 --branch=spring-boot-3
```

### 2. mvn 命令

编译项目需要 JDK 17，假设 javac 路径为 `[JAVA17_JAVAC]`，则 mvn 命令为

```shell
mvn -Dmaven.compiler.fork=true -Dmaven.compiler.executable=[JAVA17_JAVAC] -Dmaven.test.skip=true clean install
```
