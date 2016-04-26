# Tahiti

## Reuseable Components

[Config Management 配置管理构件](https://github.com/SummerWish/TahitiConfigManager)：动态地加载配置，支持 JSON、YAML 格式

[Performance Monitor 性能监控构件](https://github.com/SummerWish/TahitiPerformanceMonitor)：监控计数型指标和数值型指标，定时生成报告写入文件

[Quota Limiter 配额限制构件](https://github.com/SummerWish/TahitiQuotaLimiter)：限制每秒请求次数限制或限制总计请求次数

## Documentation
[Component Function Extension Report 构件功能扩展文档](https://github.com/SummerWish/Tahiti/blob/master/doc/构件功能扩展文档.md)

[Component Usage Report 构件使用文档](https://github.com/SummerWish/Tahiti/blob/master/doc/%E6%9E%84%E4%BB%B6%E4%BD%BF%E7%94%A8%E6%96%87%E6%A1%A3.md)

[Component Selection 构件选择文档](https://github.com/SummerWish/Tahiti/blob/master/doc/%E6%9E%84%E4%BB%B6%E9%80%89%E6%8B%A9%E6%96%87%E6%A1%A3.md)

[Discussion Solutions 讨论课解决方案](https://github.com/SummerWish/Tahiti/tree/master/doc/discussion_1)

[API Documentation 程序文档](https://summerwish.github.io/Tahiti/)

[Reuse Documentation 复用文档](https://github.com/SummerWish/Tahiti/blob/master/doc/Tahiti%20%E6%9E%84%E4%BB%B6%E8%BD%AF%E4%BB%B6%E5%A4%8D%E7%94%A8%E6%96%87%E6%A1%A3.md)

[Manage Documentation 管理文档](https://github.com/SummerWish/Tahiti/blob/master/doc/Tahiti%20%E7%AE%A1%E7%90%86%E6%96%87%E6%A1%A3.md)


## Group Members

李平山 1352840 [@nicktogo](https://github.com/nicktogo)

张航 1352960 [@HermanZzz](https://github.com/HermanZzz)

施闻轩 1352978 [@SummerWish](https://github.com/SummerWish/)

王雨晴 1354202 [@yuqingwang15](https://github.com/yuqingwang15)

刘林青 1354361 [@likicode](https://github.com/likicode)

## Architecture Introduction

### Server

Tahiti is based on the fantastic networking library [Netty](http://netty.io/). Pipeline handlers and events are heavily used.

![Server Overall Architecture](https://raw.githubusercontent.com/SummerWish/Tahiti/master/doc/architecture/server_overall.png)

### Client

Client uses [lantern](https://github.com/mabe02/lanterna) as its Comandline UI renderring library. The UI is state driven, which means UI renderer will render exactly the same UI by giving the same state. By doing so, the data flow of Tahiti client is simple and clean. Unforunately, the state driven UI architecutre is not powered by 3rd-party libraries and we invented our own. See `octoteam.tahiti.client.ui.Store`, which enables storing and retriving states reactively.

![Client Overall Architecture](https://raw.githubusercontent.com/SummerWish/Tahiti/master/doc/architecture/client_overall.png)

## Getting Started

### IDE

Tahiti includes JetBrains Intellij IDEA project files. You can directly use IDEA to open the project.

### Install ProtoBuf

Tahiti uses ProtoBuf as a data serializer and deserializer between clients and the server (see [SocketMessage.proto](https://github.com/SummerWish/Tahiti/blob/master/Protocol/src/main/proto/SocketMessage.proto) for the definition of the protocol schema). The general step to use ProtoBuf is:

1. Write `.proto` file which defines the schema of the data
2. Generate source code from `.proto` by using ProtoBuf compiler `protoc`
3. Use the generated class to serialize and deserialize data

`.proto` is already contained in this repository, however all generated stuff (including generated source code and compiled files) is not included, which means you need to complete step 2 before you could do anything else.

The good news is that we have already integrated step 2 into the maven goal `compile`, so you only need to execute `mvn compile` to generate the source code of the protocol serializer. But first of all you need to install the ProtoBuf compiler.

Documentation: https://github.com/google/protobuf#protocol-compiler-installation

Notice: You need to download or compile protoc version 3.0.

> For OS X: You can use [homebrew](http://brew.sh/) to install: `brew install protobuf --devel --c++11`

### Setting Environment Variables for Maven

`Tahiti.Protocol` will invoke `protoc` based on the path from the environment variable. In Intellij IDEA, you could set up the env var as follows:

1. Open Maven window by clicking `View` menu -> `Tool Windows`

2. In Maven window, right click `tahiti.protocol` -> `Lifecycles` -> `compile`, choose `Create [tahiti.protocol] (compile)` to create a new profile

3. Select `Runner` tab in the new window and uncheck `Use project settings`

4. Add a environment variable called `PROTOC_PATH` (case sensitive), whose value is the path of your `protoc` binary, e.g, `/usr/local/bin/protoc`.

5. Save

### Compile Protocol Serializer

Now you can start to generate the serializer source code by executing your newly created profile.

Then should find out that you can use `octoteam.tahiti.protocol.SocketMessageProtos` now, which is the protocol serializer and deserializer.

## Build JAR

```bash
# Compile
cd Tahiti
mvn install

# Pack Server JAR
cd Server
mvn assembly:single
cd ..

# Pack Client JAR
cd Client
mvn assembly:single
cd ..
```

## Command line

```bash
# Run client
java -jar Client/target/tahiti.client-1.0-SNAPSHOT-jar-with-dependencies.jar

# Run server
java -jar Server/target/tahiti.server-1.0-SNAPSHOT-jar-with-dependencies.jar

# Add user "foo"
java -jar Server/target/tahiti.server-1.0-SNAPSHOT-jar-with-dependencies.jar \
--username foo --password bar --add
```
