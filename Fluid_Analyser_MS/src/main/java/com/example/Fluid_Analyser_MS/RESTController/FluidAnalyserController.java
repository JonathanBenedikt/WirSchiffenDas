package com.example.Fluid_Analyser_MS.RESTController;
//import com.netflix.appinfo.InstanceInfo;
//import com.netflix.discovery.EurekaClient;
//import com.netflix.discovery.shared.Application;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;


import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


@RestController
public class FluidAnalyserController {

    @Autowired
    private KafkaTemplate<String, Map> kafkaTemplate;

    private String status;

    public FluidAnalyserController()
    {
        status = "Idle";
    }
    public void sendMessage(String msg){
        kafkaTemplate.send("fluidsystemelements_analysis", new HashMap(){{put("Message",msg);}});
    }

    //@Autowired
    //private EurekaClient eurekaClient;

    @Autowired
    private ApplicationContext appContext;

    @GetMapping("sendKafkaMessage/{message}")
    public HttpStatus sendKafkaMessage(@PathVariable String message){
        sendMessage(message);
        return HttpStatus.OK;
    }

    @KafkaListener(topics="fluidsystemelements_analysis", groupId = "One")
    public void listen(ConsumerRecord<?, ?> record ){
        try {
            String recordKey = record.key().toString();

            if (recordKey.equals("BFF_Starts_Fluidsystem_Analysis")) {
                HashMap startMap = (HashMap)record.value();
                String id = (String)startMap.get("id");
                String name = (String)startMap.get("name");
                performAnalysis(id,name);

            } else if (recordKey.equals("Analyser_Finished")) {
                HashMap resultMap = (HashMap) record.value();

                System.out.println("The analysis for id "+resultMap.get("id")+" with the name "+resultMap.get("name")+" is finished");
                System.out.println("Results -> exhaust-system: "+resultMap.get("exhaust_system")+", fuel system: "+resultMap.get("fuel_system"));
            }
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
    }
    @GetMapping(path="/information")
    public String showInfo() {return "Name: Fluid-Analyser\nType: Microservice\nVersion: 1.0.0";}

    @PostMapping(path="/analyse")
    public ResponseEntity<Map> getData(@RequestBody FluidInformation providedFluid){
        try {
            List<FluidInformation> fluidData = Collections.singletonList(providedFluid);
            Map analysisResult = performAnalysis(fluidData.get(0).id,fluidData.get(0).name);
            return ResponseEntity.ok(analysisResult);
        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }
    @GetMapping(path="/shutdown")
    public void shutdown(){
        SpringApplication.exit(appContext, () -> 0);
    }


    private Map createAnalysisValues(String id, String name){
        Random rand = new Random();
        Map analysisValuesMap = new HashMap();
        analysisValuesMap.put("id",id);
        analysisValuesMap.put("name",name);
        analysisValuesMap.put("fuel_system",(rand.nextFloat() * (100 - 1) + 1));
        analysisValuesMap.put("exhaust_system",(rand.nextFloat() * (100 - 1) + 1));

        return analysisValuesMap;
    }

    private Map performAnalysis(String id, String name)
    {
        try {
            kafkaTemplate.send(new ProducerRecord<String, Map>("fluidsystemelements_analysis", "Analyser_Starts_Analysis", null));
            status = "Started";
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));
            Map calculationResults = createAnalysisValues(id, name);
            status = "Finished";
            kafkaTemplate.send(new ProducerRecord<String, Map>("fluidsystemelements_analysis", "Analyser_Finished", calculationResults));
            return calculationResults;
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
        return null;
    }

    private JSONObject createJSONAnalysis(String fuelsystem, String exhaustsystem)
    {

        JSONObject fluidJson = new JSONObject();
        JSONArray fluidpropertiesArray = new JSONArray();
        //JSONObject fluidpropertiesJson = new JSONObject();
        JSONObject fuelsystemObject = new JSONObject().put("Fuel System", fuelsystem);
        JSONObject exhaustsystemObject = new JSONObject().put("Exhaust System", exhaustsystem);
        JSONObject statusObject = new JSONObject().put("Status","Analysis Completed");
        JSONObject successObject = new JSONObject().put("Success","True");

        fluidpropertiesArray.put(fuelsystemObject);
        fluidpropertiesArray.put(exhaustsystemObject);
        fluidpropertiesArray.put(statusObject);
        fluidpropertiesArray.put(successObject);

        //fluidpropertiesArray.put(fluidpropertiesJson);
        fluidJson.put("Fluid",fluidpropertiesArray);
        return fluidJson;
    }

    public static class FluidInformation {

        private String id;
        private String name;
        private String fuelsystem;

        private String exhaustsystem;

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

        public String getFuelsystem() {
            return fuelsystem;
        }

        public void setFuelsystem(String fuelsystem) {
            this.fuelsystem = fuelsystem;
        }

        public String getExhaustsystem() {return exhaustsystem;}

        public void setExhaustsystem(String exhaustsystem) { this.exhaustsystem = exhaustsystem;}

    }
}
