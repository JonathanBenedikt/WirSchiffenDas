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

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@RestController
public class CoolingSystemsAnalyserController {


        @Autowired
        private KafkaTemplate<String, Map> kafkaTemplate;

        @Autowired
        private ApplicationContext appContext;

        private String status;

        public CoolingSystemsAnalyserController(){
                status = "Idle";
        }
        public void sendMessage(String msg){

                kafkaTemplate.send("coolingsystemelements_analysis",new HashMap(){{put("Message",msg);}});
        }

        @KafkaListener(topics="coolingsystemelements_analysis", groupId = "One")
        public void listen(ConsumerRecord<?, ?> record ){
                try {
                        String recordKey = record.key().toString();

                        if (recordKey.equals("BFF_Starts_Coolingsystem_Analysis")) {
                                HashMap startMap = (HashMap)record.value();
                                String id = (String)startMap.get("id");
                                String name = (String)startMap.get("name");
                                performAnalysis(id,name);

                        } else if (recordKey.equals("Analyser_Finished")) {
                                HashMap resultMap = (HashMap) record.value();

                                System.out.println("The analysis for id "+resultMap.get("id")+" with the name "+resultMap.get("name")+" is finished");
                                System.out.println("Results -> cooling-system: "+resultMap.get("coolingsystem")+", oil system: "+resultMap.get("oilsystem"));
                        }
                        else if (recordKey.equals("Status_Request"))
                        {
                                HashMap statusMap = new HashMap();
                                statusMap.put("status",status);
                                kafkaTemplate.send(new ProducerRecord<String,Map>("coolingsystemelements_analysis","Status_Response",statusMap));
                        }
                }catch (Exception ex)
                {
                        System.out.println(ex);
                }
        }

        @GetMapping(path="/status")
        public String getStatus(){ return status;}
        @GetMapping(path="/information")
        public String showInfo() {return "Name: CoolingSystem-Analyser\nType: Microservice\nVersion: 1.0.0";}

        @GetMapping(path="/shutdown")
        public void shutdown(){
                SpringApplication.exit(appContext, () -> 0);
        }

        @PostMapping(path="/analyse")
        public ResponseEntity<Map> getData(@RequestBody CoolingSystemInformation providedCoolingInfos){
                try {
                        List<CoolingSystemInformation> coolingDataList = Collections.singletonList(providedCoolingInfos);
                        Map analysisResult = performAnalysis(coolingDataList.get(0).id, coolingDataList.get(0).name);
                        return ResponseEntity.ok(analysisResult);
                }catch (Exception ex){
                        System.out.println(ex);
                }
                return null;
        }


        private Map createAnalysisValues(String id, String name){
                Random rand = new Random();
                Map analysisValuesMap = new HashMap();
                analysisValuesMap.put("id",id);
                analysisValuesMap.put("name",name);
                analysisValuesMap.put("coolingsystem",(rand.nextFloat() * (100 - 1) + 1));
                analysisValuesMap.put("oilsystem",(rand.nextFloat() * (100 - 1) + 1));
                return analysisValuesMap;
        }

        private Map performAnalysis(String id, String name)
        {
                try{

                        kafkaTemplate.send(new ProducerRecord<String,Map>("coolingsystemelements_analysis","Analyser_Starts_Analysis",null));
                        status = "Started";
                        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));
                        Map calculationResults = createAnalysisValues(id, name);
                        status = "Finished";
                        kafkaTemplate.send(new ProducerRecord<String,Map>("coolingsystemelements_analysis","Analyser_Finished",calculationResults));
                        return calculationResults;
                }catch (Exception ex)
                {
                        System.out.println(ex);
                }
                return null;
        }

        public static class CoolingSystemInformation {

                private String id;
                private String name;
                private String coolingsystem;

                private String oilsystem;

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
