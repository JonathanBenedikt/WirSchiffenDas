package com.example.StartingElments_Analyser_MS;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value= "10.0.4.2:9092")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin(){
        Map<String,Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic startingsystemelements_analysis(){
        return new NewTopic("startingsystemelements_analysis", 1, (short) 1);
    }
}
