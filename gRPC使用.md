# Springboot+gRPC

## 目标
grpc服务端用python构建，客户端通过springboot用java构建

## 实施方法
### 1. 编写`proto`文件

```protobuf
syntax = "proto3";

option java_multiple_files = true;
option java_package = "com.ntf.grpc";
option java_outer_classname = "HelloWorldProto";
//option objc_class_prefix = "HLW";

package helloworld;

// The greeting service definition.
service HelloService {
  // Sends a greeting
  rpc SayHello (HelloRequest) returns (HelloReply) {}
  rpc SayHelloAgain (HelloRequest) returns (HelloReply) {}
}

// The request message containing the user's name.
message HelloRequest {
  string name = 1;
}

// The response message containing the greetings
message HelloReply {
  string message = 1;
}
```
### 2. 生成代码

- 2.1 python服务端

```shell
python3 -m grpc_tools.protoc -I../../protos --python_out=. --pyi_out=. --grpc_python_out=. ../../protos/helloworld.proto
```
- 2.2 Java客户端

利用maven插件进行代码生成，pom依赖与官方example相同

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.8</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.ntf</groupId>
    <artifactId>grpc-springboot</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>grpc-springboot</name>
    <description>grpc-springboot</description>
    <properties>
        <java.version>17</java.version>
        <grpc.version>1.52.0</grpc.version><!-- CURRENT_GRPC_VERSION -->
        <protobuf.version>3.21.7</protobuf.version>
        <protoc.version>3.21.7</protoc.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-netty-shaded</artifactId>
            <version>1.52.1</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-protobuf</artifactId>
            <version>1.52.0</version>
        </dependency>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-stub</artifactId>
            <version>1.52.0</version>
        </dependency>
        <dependency>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java-util</artifactId>
            <version>${protobuf.version}</version>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.9.0</version> <!-- prevent downgrade via protobuf-java-util -->
        </dependency>
        <dependency>
            <groupId>org.apache.tomcat</groupId>
            <artifactId>annotations-api</artifactId>
            <version>6.0.53</version>
            <scope>provided</scope> <!-- not needed at runtime -->
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <extensions>
            <extension>
                <groupId>kr.motd.maven</groupId>
                <artifactId>os-maven-plugin</artifactId>
                <version>1.6.2</version>
            </extension>
        </extensions>

        <plugins>
            <plugin>
                <groupId>org.xolstice.maven.plugins</groupId>
                <artifactId>protobuf-maven-plugin</artifactId>
                <version>0.6.1</version>
                <configuration>
                    <protocArtifact>com.google.protobuf:protoc:${protoc.version}:exe:${os.detected.classifier}</protocArtifact>
                    <pluginId>grpc-java</pluginId>
                    <pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>compile-custom</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-enforcer-plugin</artifactId>
                <version>1.4.1</version>
                <executions>
                    <execution>
                        <id>enforce</id>
                        <goals>
                            <goal>enforce</goal>
                        </goals>
                        <configuration>
                            <rules>
                                <requireUpperBoundDeps/>
                            </rules>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```
执行compile即可生成代码，结构如下
![](./images/生成代码.png)



### 3. 代码编写

- 3.1 python服务端

参考官方example，代码如下

```python
from concurrent import futures
import logging

import grpc
import helloworld_pb2
import helloworld_pb2_grpc


class Greeter(helloworld_pb2_grpc.HelloServiceServicer):

    def SayHello(self, request, context):
        print("server receive: " + request.name)
        return helloworld_pb2.HelloReply(message='Hello, %s!' % request.name)

    def SayHelloAgain(self, request, context):
        return helloworld_pb2.HelloReply(message='Hello again, %s!' % request.name)


def serve():
    port = '50051'
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    helloworld_pb2_grpc.add_HelloServiceServicer_to_server(Greeter(), server)
    server.add_insecure_port('[::]:' + port)
    server.start()
    print("Server started, listening on " + port)
    server.wait_for_termination()


if __name__ == '__main__':
    logging.basicConfig()
    serve()
```

- 3.2 Java客户端
	

**编写配置类，自动注入channel**

```java
@Configuration
public class GRpcConfig {

    @Value("${grpc.server}")    //localhost:50051
    private String target;

    @Bean
    public Channel channel() {
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
                .build();
        return channel;
    }

}
```

**编写client代码，发出请求，接收响应**

```java
@Service
public class GRpcClient {

    private static final Logger logger = Logger.getLogger(GRpcClient.class.getName());

    private final HelloServiceGrpc.HelloServiceBlockingStub myServiceStub;


    public GRpcClient(Channel channel) {
        myServiceStub = HelloServiceGrpc.newBlockingStub(channel);
    }

    public String hello(String name) {
        logger.info("Will try to request: " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = myServiceStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return "RPC failed: " + e.getStatus();
        }
        logger.info("Response: " + response.getMessage());
        return response.getMessage();
    }

    public String helloAgain(String name) {
        logger.info("Will try to request: " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = myServiceStub.sayHelloAgain(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return "RPC failed: " + e.getStatus();
        }
        logger.info("Response: " + response.getMessage());
        return response.getMessage();
    }

}
```



如此一来，便可以完成RPC的调用，并得到服务器的返回结果。



参考：https://cloud.tencent.com/developer/article/2191348
