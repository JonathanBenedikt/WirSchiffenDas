package com.Workflow_Engine;

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

    @Value(value= "localhost:9092")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin(){
        Map<String,Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic coolingsystemelements_analyse(){
        return new NewTopic("coolingsystemelements_analyse", 1, (short) 1);
    }

    @Bean
    public NewTopic fluidelements_analyse(){
        return new NewTopic("fluidelements_analyse", 1, (short) 1);
    }

    @Bean
    public NewTopic powertransmissionelements_analyse(){
        return new NewTopic("powertransmissionelements_analyse", 1, (short) 1);
    }

    @Bean
    public NewTopic startingelements_analyse(){
        return new NewTopic("startingelements_analyse", 1, (short) 1);
    }
    @Bean
    public NewTopic wf_bff(){
        return new NewTopic("wf_bff", 1, (short) 1);
    }
}
