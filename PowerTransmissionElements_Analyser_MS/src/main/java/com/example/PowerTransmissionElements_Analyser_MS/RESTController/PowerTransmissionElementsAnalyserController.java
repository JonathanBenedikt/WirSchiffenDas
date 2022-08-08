package com.example.PowerTransmissionElements_Analyser_MS.RESTController;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PowerTransmissionElementsAnalyserController {

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
    public String showInfo() {return "Name: PowerTransmissionElements-Analyser\nType: Microservice\nVersion: 1.0.0";}

    @GetMapping(path="/shutdown")
    public void shutdown(){
        SpringApplication.exit(appContext, () -> 0);
    }

    public static class PowerTransmissionElementsInformation {

        private int id;
        private String name;
        private String mountingsystem;
        private String monitoringsystem;
        private String powertransmission;
        private String gearboxoptions;

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

        public String getMountingsystem() {
            return mountingsystem;
        }
        public void setMountingsystem(String mountingsystem) {
            this.mountingsystem = mountingsystem;
        }

        public String getMonitoringsystem() {return monitoringsystem;}
        public void setMonitoringsystem(String monitoringsystem) { this.monitoringsystem = monitoringsystem;}

        public String getPowertransmission() { return powertransmission; }
        public void setPowertransmission(String powertransmission) { this.powertransmission = powertransmission; }

        public String getGearboxoptions() { return gearboxoptions; }
        public void setGearboxoptions(String gearboxoptions) { this.gearboxoptions = gearboxoptions; }
    }
}
