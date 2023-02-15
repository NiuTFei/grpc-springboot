package com.ntf.grpc.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class MyConsumer {

    @KafkaListener(topics = "test-topic", groupId = "group-1")
    public void listen(ConsumerRecord<String, String> record/*, Acknowledgment ack*/) {

        System.out.println("record:" + record);
        System.out.println("record.value = " + record.value());

        //手动提交offset
//        ack.acknowledge();
    }

}
