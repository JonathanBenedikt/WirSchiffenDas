package com.example.CoolingSystems_Analyser_MS.RESTController;

import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@RestController
public class CoolingSystemsAnalyserController {


        @Autowired
        private KafkaTemplate<String, String> kafkaTemplate;

        @Autowired
        private ApplicationContext appContext;

        public void sendMessage(String msg){
                kafkaTemplate.send("analyse",msg);
        }

        @KafkaListener(topics="analyse", groupId = "1")
        public void listen(ConsumerRecord<?, ?> record ){

        }

        @GetMapping(path="/information")
        public String showInfo() {return "Name: CoolingSystem-Analyser\nType: Microservice\nVersion: 1.0.0";}

        @GetMapping(path="/shutdown")
        public void shutdown(){
                SpringApplication.exit(appContext, () -> 0);
        }

        @PostMapping(path="/analyse")
        public ResponseEntity<List<CoolingSystemInformation>> getData(@RequestBody CoolingSystemInformation providedCoolingInfos){
                try {
                        List<CoolingSystemInformation> fluidData = Collections.singletonList(providedCoolingInfos);
                        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));
                        Gson gson = new Gson();
                        String json = gson.toJson(providedCoolingInfos);
                        kafkaTemplate.send(new ProducerRecord<String,String>("analyse","Fluid-Analysis-Result",json));
                        return ResponseEntity.ok(fluidData);
                }catch (Exception ex){
                        System.out.println(ex);
                }
                return null;
        }
        public static class CoolingSystemInformation {

                private int id;
                private String name;
                private String coolingsystem;

                private String oilsystem;

                public int getId() {
                        return id;
                }

                public void setId(int id) {
                        this.id = id;
                }

                public String getName() {
                        return name;
                }

                public void setName(String name) {
                        this.name = name;
                }

                public String getCoolingsystem() {
                        return coolingsystem;
                }

                public void setCoolingsystem(String coolingsystem) {
                        this.coolingsystem = coolingsystem;
                }

                public String getOilsystem() {return oilsystem;}

                public void setOilsystem(String oilsystem) { this.oilsystem = oilsystem;}

        }
}
