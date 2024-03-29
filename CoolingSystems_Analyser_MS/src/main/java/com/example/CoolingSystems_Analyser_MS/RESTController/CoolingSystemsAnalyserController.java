package com.example.CoolingSystems_Analyser_MS.RESTController;

import com.google.gson.Gson;
import com.netflix.discovery.EurekaClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
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

@EnableEurekaClient
@RestController
public class CoolingSystemsAnalyserController {


        @Autowired
        private EurekaClient eurekaClient;
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

                        if (recordKey.equals("WF_Starts_Coolingsystemelements_Analysis")) {

                                HashMap startMap = (HashMap)record.value();
                                String id = (String)startMap.get("id");
                                CoolingSystemInformation coolingdata = new CoolingSystemInformation();
                                coolingdata.id = id;
                                coolingdata.oilsystem = (String)startMap.get("oil_system");
                                coolingdata.coolingsystem = (String)startMap.get("cooling_system");
                                performAnalysis(id,coolingdata);

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
        public String getStatus(){
                return status;
        }
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
                        Map analysisResult = performAnalysis(coolingDataList.get(0).id, coolingDataList.get(0));
                        return ResponseEntity.ok(analysisResult);
                }catch (Exception ex){
                        System.out.println(ex);
                }
                return null;
        }


        private Map createAnalysisValues(String id, CoolingSystemInformation data){
                Random rand = new Random();
                Map analysisValuesMap = new HashMap();
                analysisValuesMap.put("id",id);
                if((data.coolingsystem != null) && (data.coolingsystem != "")) {
                        HashMap coolingsystemMap = new HashMap();
                        coolingsystemMap.put(data.coolingsystem,(rand.nextFloat() * (100 - 1) + 1));
                        analysisValuesMap.put("coolingsystem",coolingsystemMap );
                }else {
                        analysisValuesMap.put("coolingsystem",null);
                }
                if((data.oilsystem != null) && (data.oilsystem != "")) {
                        HashMap oilsystemMap = new HashMap();
                        oilsystemMap.put(data.oilsystem,(rand.nextFloat() * (100 - 1) + 1));
                        analysisValuesMap.put("oilsystem", oilsystemMap);
                } else {
                        analysisValuesMap.put("oilsystem",null);
                }

                return analysisValuesMap;
        }

        private Map performAnalysis(String id, CoolingSystemInformation data)
        {
                try{
                        HashMap startingMap = new HashMap();
                        startingMap.put("id",data.id);

                        SplittableRandom random = new SplittableRandom();
                        // Probability of 20% to fail
                        if(random.nextInt(1,11) <= 3)
                        {
                                System.out.println("An Error occured for the Cooling-analyser...");
                                this.status = "Error";
                                kafkaTemplate.send(new ProducerRecord<String,Map>("coolingsystemelements_analysis","Analyser_In_Error-State",startingMap));
                                return null;
                        }

                        kafkaTemplate.send(new ProducerRecord<String,Map>("coolingsystemelements_analysis","Analyser_Starts_Analysis",startingMap));
                        status = "Started";
                        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));
                        Map calculationResults = createAnalysisValues(id, data);
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
