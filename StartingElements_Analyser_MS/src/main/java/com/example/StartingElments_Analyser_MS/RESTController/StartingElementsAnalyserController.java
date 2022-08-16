package com.example.StartingElments_Analyser_MS.RESTController;

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
public class StartingElementsAnalyserController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ApplicationContext appContext;

    public void sendMessage(String msg){
        kafkaTemplate.send("startingsystemelements_analysis",msg);
    }

    @KafkaListener(topics="startingsystemelements_analysis", groupId = "One")
    public void listen(ConsumerRecord<?, ?> record ){

    }

    @GetMapping(path="/information")
    public String showInfo() {return "Name: StartingElements-Analyser\nType: Microservice\nVersion: 1.0.0";}

    @GetMapping(path="/shutdown")
    public void shutdown(){
        SpringApplication.exit(appContext, () -> 0);
    }

    @PostMapping(path="/analyse")
    public ResponseEntity<List<StartingElementsInformation>> getData(@RequestBody StartingElementsInformation providedStartingInfos){
        try {
            List<StartingElementsInformation> fluidData = Collections.singletonList(providedStartingInfos);
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));
            Gson gson = new Gson();
            String json = gson.toJson(providedStartingInfos);
            kafkaTemplate.send(new ProducerRecord<String,String>("startingsystemelements_analysis","Analyser_Finished",json));
            return ResponseEntity.ok(fluidData);
        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }
    public static class StartingElementsInformation {

        private int id;
        private String name;
        private String startingsystem;
        private String auxilliarypto;
        private String enginemanagementsystem;


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

        public String getStartingsystem() {
            return startingsystem;
        }
        public void setStartingsystem(String startingsystem) {
            this.startingsystem = startingsystem;
        }

        public String getAuxilliarypto() {return auxilliarypto;}
        public void setAuxilliarypto(String auxilliarypto) { this.auxilliarypto = auxilliarypto;}

        public String getEnginemanagementsystem() { return enginemanagementsystem; }
        public void setEnginemanagementsystem(String enginemanagementsystem) { this.enginemanagementsystem = enginemanagementsystem; }

    }
}
