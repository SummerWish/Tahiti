## Getting Started

### Install ProtoBuf

Tahiti uses ProtoBuf as a data serializer and deserializer between clients and the server. The general step to use ProtoBuf is:

1. Write `.proto` file which defines the schema of the data
2. Generate source code from `.proto` by using ProtoBuf compiler `protoc`
3. Use the generated class to serialize and deserialize data

`.proto` is already contained in this repository, however all generated stuff (including generated source code and compiled files) is not included, which means you need to complete step 2 before you could do anything else.

The good news is that we have already integrated step 2 into the maven goal `compile`, so you only need to execute `mvn compile` to generate the source code of the protocol serializer. But first of all you need to install the ProtoBuf compiler.

Documentation: https://github.com/google/protobuf#protocol-compiler-installation

For OS X: You can use [homebrew](http://brew.sh/) to install it.

### Compile Protocol Serializer

After obtaining the ProtoBuf compiler, you could start to generate the serializer source code.

```bash
cd Tahiti/Protocol
mvn compile
```

Then should find out that you can use `octoteam.tahiti.protocol.SocketMessageProtos` now, which is the protocol serializer and deserializer.

### Start Server

Run `Server` configuration in Intellij IDEA, or:

```bash
cd Tahiti/Server
mvn exec:exec
```

### Start Client

Run `Client` configuration in Intellij IDEA, or:

```bash
cd Tahiti/Client
mvn exec:exec
```
