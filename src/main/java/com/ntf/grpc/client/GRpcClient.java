package com.ntf.grpc.client;

import com.ntf.grpc.HelloReply;
import com.ntf.grpc.HelloRequest;
import com.ntf.grpc.HelloServiceGrpc;
import io.grpc.*;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

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
