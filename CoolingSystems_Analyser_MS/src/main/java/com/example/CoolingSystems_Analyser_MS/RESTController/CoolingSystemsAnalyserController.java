package com.example.CoolingSystems_Analyser_MS.RESTController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoolingSystemsAnalyserController {


        @Autowired
        private KafkaTemplate<String, String> kafkaTemplate;

        @Autowired
        private ApplicationContext appContext;

}
