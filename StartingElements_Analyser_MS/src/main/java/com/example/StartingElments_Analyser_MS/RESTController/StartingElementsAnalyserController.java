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

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@RestController
public class StartingElementsAnalyserController {

    @Autowired
    private KafkaTemplate<String, Map> kafkaTemplate;

    @Autowired
    private ApplicationContext appContext;

    private String status;

    public StartingElementsAnalyserController(){
        status = "Idle";
    }
    public void sendMessage(String msg){
        kafkaTemplate.send(
                "startingsystemelements_analysis",new HashMap(){{put("Message",msg);}});
    }

    @KafkaListener(topics="startingsystemelements_analysis", groupId = "One")
    public void listen(ConsumerRecord<?, ?> record ){
        try {
            String recordKey = record.key().toString();

            if (recordKey.equals("BFF_Starts_Startingsystem_Analysis")) {
                HashMap startMap = (HashMap)record.value();
                String id = (String)startMap.get("id");
                String name = (String)startMap.get("name");
                performAnalysis(id,name);

            } else if (recordKey.equals("Analyser_Finished")) {
                HashMap resultMap = (HashMap) record.value();

                System.out.println("The analysis for id "+resultMap.get("id")+" with the name "+resultMap.get("name")+" is finished");
                System.out.println("Results -> startingsystem: "+resultMap.get("startingsystem")+", auxilliarypto: "+resultMap.get("auxilliarypto")+", enginemanagementsystem: "+resultMap.get("enginemanagementsystem"));
            }
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    @GetMapping(path="/status")
    public String getStatus(){ return status;}
    @GetMapping(path="/information")
    public String showInfo() {return "Name: StartingElements-Analyser\nType: Microservice\nVersion: 1.0.0";}

    @GetMapping(path="/shutdown")
    public void shutdown(){
        SpringApplication.exit(appContext, () -> 0);
    }

    @PostMapping(path="/analyse")
    public ResponseEntity<Map> getData(@RequestBody StartingElementsInformation providedStartingInfos){
        try {
            List<StartingElementsInformation> startingSystemData = Collections.singletonList(providedStartingInfos);
            Map analysisResult = performAnalysis(startingSystemData.get(0).id,startingSystemData.get(0).name);
            return ResponseEntity.ok(analysisResult);
        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }

    private Map createAnalysisValues(String id, String name){
        Random rand = new Random();Map analysisValuesMap = new HashMap();
        analysisValuesMap.put("id",id);
        analysisValuesMap.put("name",name);
        analysisValuesMap.put("startingsystem",(rand.nextFloat() * (100 - 1) + 1));
        analysisValuesMap.put("auxilliarypto",(rand.nextFloat() * (100 - 1) + 1));
        analysisValuesMap.put("enginemanagementsystem",(rand.nextFloat() * (100 - 1) + 1));
        return analysisValuesMap;
    }

    private Map performAnalysis(String id, String name)
    {
        try{
            kafkaTemplate.send(new ProducerRecord<String,Map>("startingsystemelements_analysis","Analyser_Starts_Analysis",null));
            status = "Started";
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));
            Map calculationResults = createAnalysisValues(id, name);
            status = "Finished";
            kafkaTemplate.send(new ProducerRecord<String,Map>("startingsystemelements_analysis","Analyser_Finished", calculationResults));
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
        return null;
    }

    public static class StartingElementsInformation {

        private String id;
        private String name;
        private String startingsystem;
        private String auxilliarypto;
        private String enginemanagementsystem;


        public String getId() {
            return id;
        }
        public void setId(String id) {
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
