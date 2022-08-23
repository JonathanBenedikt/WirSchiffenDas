package com.example.PowerTransmissionElements_Analyser_MS.RESTController;

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

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@RestController
public class PowerTransmissionElementsAnalyserController {

    @Autowired
    private KafkaTemplate<String, Map> kafkaTemplate;

    @Autowired
    private ApplicationContext appContext;

    public void sendMessage(String msg){

        kafkaTemplate.send("powertransmissionsystemelements_analysis",new HashMap(){{put("Message",msg);}});
    }

    @KafkaListener(topics="powertransmissionsystemelements_analysis", groupId = "One")
    public void listen(ConsumerRecord<?, ?> record ){
        try {
            String recordKey = record.key().toString();

            if (recordKey.equals("WF_Starts_Powertransmissionsystemelements_Analysis")) {
                HashMap startMap = (HashMap)record.value();
                int id = (int)startMap.get("id");
                String name = (String)startMap.get("name");
                performAnalysis(id,name);

            } else if (recordKey.equals("Analyser_Finished")) {
                HashMap resultMap = (HashMap) record.value();
                System.out.println("The analysis for id "+resultMap.get("id")+" with the name "+resultMap.get("name")+" is finished");
                System.out.println("Results -> mounting system: "+resultMap.get("mountingsystem")+", monitoringsystem: "+resultMap.get("monitoringsystem")+", powertransmission: "+resultMap.get("powertransmission")+", gearboxpotions: "+resultMap.get("gearboxoptions"));
            }
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
    }
    @GetMapping(path="/information")
    public String showInfo() {return "Name: PowerTransmissionElements-Analyser\nType: Microservice\nVersion: 1.0.0";}

    @GetMapping(path="/shutdown")
    public void shutdown(){
        SpringApplication.exit(appContext, () -> 0);
    }

    @PostMapping(path="/analyse")
    public ResponseEntity<Map> getData(@RequestBody PowerTransmissionElementsInformation providedPowerInfos){
        try {
            List<PowerTransmissionElementsInformation> powertransmissionData = Collections.singletonList(providedPowerInfos);
            Map analysisResult = performAnalysis(powertransmissionData.get(0).id,powertransmissionData.get(0).name);
            return ResponseEntity.ok(analysisResult);
        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }

    private Map createAnalysisValues(int id, String name){
        Random rand = new Random();
        Map analysisValuesMap = new HashMap();
        analysisValuesMap.put("id",id);
        analysisValuesMap.put("name",name);
        analysisValuesMap.put("mountingsystem",(rand.nextFloat() * (100 - 1) + 1));
        analysisValuesMap.put("monitoringsystem",(rand.nextFloat() * (100 - 1) + 1));
        analysisValuesMap.put("powertransmission",(rand.nextFloat() * (100 - 1) + 1));
        analysisValuesMap.put("gearboxoptions",(rand.nextFloat() * (100 - 1) + 1));
        return analysisValuesMap;
    }

    private Map performAnalysis(int id, String name)
    {
        try{
            kafkaTemplate.send(new ProducerRecord<String,Map>("powertransmissionsystemelements_analysis","Analyser_Starts_Analysis",null));
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));
            Map calculationResults = createAnalysisValues(id,name);
            kafkaTemplate.send(new ProducerRecord<String,Map>("powertransmissionsystemelements_analysis","Analyser_Finished",calculationResults));
            return calculationResults;
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
        return null;
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
