package com.example.StartingElments_Analyser_MS.RESTController;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StartingElementsAnalyserController {

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
    public String showInfo() {return "Name: StartingElements-Analyser\nType: Microservice\nVersion: 1.0.0";}

    @GetMapping(path="/shutdown")
    public void shutdown(){
        SpringApplication.exit(appContext, () -> 0);
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
