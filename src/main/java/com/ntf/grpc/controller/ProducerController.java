package com.ntf.grpc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {

    private static final String TOPIC_NAME = "test-topic";

    @Autowired
    private KafkaTemplate<String, String> template;

    @RequestMapping("/send")
    public String send() {
        template.send(TOPIC_NAME, 0, "key", "test message --1");
        return "send success!";
    }
}
