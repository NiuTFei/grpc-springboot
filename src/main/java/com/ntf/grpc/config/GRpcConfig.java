package com.ntf.grpc.config;

import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GRpcConfig {

    @Value("${grpc.server.host}")    //localhost:50051
    private String host;

    @Value("${grpc.server.port}")
    private String port;


    @Bean
    public Channel channel() {
        String target = host + ":" + port;
        return Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
                .build();
    }

}

