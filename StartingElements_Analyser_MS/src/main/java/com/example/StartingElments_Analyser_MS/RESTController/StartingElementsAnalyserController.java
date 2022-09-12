package com.example.StartingElments_Analyser_MS.RESTController;

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

            if (recordKey.equals("WF_Starts_Startingsystemelements_Analysis")) {
                HashMap startMap = (HashMap)record.value();
                String id = (String)startMap.get("id");

                StartingElementsInformation data = new StartingElementsInformation();
                data.id = id;
                data.air_starter = (Boolean)startMap.get("air_starter");
                data.auxiliarypto = (String)startMap.get("auxiliary_PTO");
                data.enginemanagementsystem = (Boolean)startMap.get("engine_management_system");
                performAnalysis(id,data);

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
            Map analysisResult = performAnalysis(startingSystemData.get(0).id,startingSystemData.get(0));
            return ResponseEntity.ok(analysisResult);
        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }

    private Map createAnalysisValues(String id, StartingElementsInformation data){
        Random rand = new Random();Map analysisValuesMap = new HashMap();
        analysisValuesMap.put("id",id);

        if((data.air_starter!= null)) {
            HashMap airstarterMap = new HashMap();
            airstarterMap.put(data.air_starter,(rand.nextFloat() * (100 - 1) + 1));
            analysisValuesMap.put("air_starter", airstarterMap);
        } else {
            analysisValuesMap.put("air_starter",null);
        }

        if((data.auxiliarypto != null) && (data.auxiliarypto != "")){
            HashMap auxiliaryMap = new HashMap();
            auxiliaryMap.put(data.auxiliarypto,(rand.nextFloat() * (100 - 1) + 1));
            analysisValuesMap.put("auxiliary_PTO",auxiliaryMap);
        } else {
            analysisValuesMap.put("auxiliary_PTO",null);
        }

        if((data.enginemanagementsystem != null)){
            HashMap engineMap = new HashMap();
            engineMap.put(data.enginemanagementsystem,(rand.nextFloat() * (100 - 1) + 1));
            analysisValuesMap.put("engine_management_system",engineMap);
        } else {
            analysisValuesMap.put("engine_management_system",null);
        }

        return analysisValuesMap;
    }

    private Map performAnalysis(String id, StartingElementsInformation data)
    {
        try{
            HashMap starterMap = new HashMap();
            starterMap.put("id",data.id);

            SplittableRandom random = new SplittableRandom();
            // Probability of 20% to fail
            if(random.nextInt(1,11) <= 3)
            {
                this.status = "Error";
                kafkaTemplate.send(new ProducerRecord<String,Map>("startingsystemelements_analysis","Analyser_In_Error-State",starterMap));
                return null;
            }


            kafkaTemplate.send(new ProducerRecord<String,Map>("startingsystemelements_analysis","Analyser_Starts_Analysis",starterMap));
            status = "Started";
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));
            Map calculationResults = createAnalysisValues(id, data);
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
        private Boolean air_starter;
        private String auxiliarypto;
        private Boolean enginemanagementsystem;


        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }


        public Boolean getAir_starter() {
            return air_starter;
        }
        public void setAir_starter(Boolean air_starter) {
            this.air_starter = air_starter;
        }

        public String getAuxiliarypto() {return auxiliarypto;}
        public void setAuxiliarypto(String auxiliarypto) { this.auxiliarypto = auxiliarypto;}

        public Boolean getEnginemanagementsystem() { return enginemanagementsystem; }
        public void setEnginemanagementsystem(Boolean enginemanagementsystem) { this.enginemanagementsystem = enginemanagementsystem; }

    }
}
