package com.ntf.grpc.controller;

import com.ntf.grpc.client.GRpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private GRpcClient client;


    @GetMapping("/hello")
    public String hello() {

        return client.hello("gRPC-java-python");
    }

    @GetMapping("/hello/again")
    public String helloAgain() {

        return client.helloAgain("gRPC-java-python");
    }
}
