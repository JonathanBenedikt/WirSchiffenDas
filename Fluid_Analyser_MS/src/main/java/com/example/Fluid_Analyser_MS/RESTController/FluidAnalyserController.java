package com.example.Fluid_Analyser_MS.RESTController;
//import com.netflix.appinfo.InstanceInfo;
//import com.netflix.discovery.EurekaClient;
//import com.netflix.discovery.shared.Application;
import com.google.gson.Gson;
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
            Gson gson = new Gson();
            String recordKey = record.key().toString();

            if (recordKey.equals("WF_Starts_Fluidsystemelements_Analysis")) {
                /*JSONObject analysisRequest = new JSONObject(record.value().toString());
                JSONArray analysisParameter = analysisRequest.getJSONArray("Analysis Parameter");
                JSONObject analysisResult = createAnalysisObject(analysisParameter.getJSONObject(0).getString("Fuel System"), analysisParameter.getJSONObject(1).getString("Exhaust System"));
                */
                Map analysisResultMap = new HashMap();
                analysisResultMap.put("exhaust_system",true);
                analysisResultMap.put("fuel_system", 10);

                //kafkaTemplate.send(new ProducerRecord<String,Map>("fluidsystemelements_analysis","Analyser_Finished", createAnalysisValues()));
            } else if (recordKey.equals("Fluid-Analysis-Result")) {
                FluidInformation fluid = gson.fromJson(record.value().toString(), FluidInformation.class);
                System.out.println("Received Message in group - 1 - "+ recordKey.toString() + " " +fluid.name);
            }
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
    }
    @GetMapping(path="/information")
    public String showInfo() {return "Name: Fluid-Analyser\nType: Microservice\nVersion: 1.0.0";}

    @PostMapping(path="/analyse")
    public ResponseEntity<List<FluidInformation>> getData(@RequestBody FluidInformation providedFluid){
        try {
            kafkaTemplate.send(new ProducerRecord<String,Map>("fluidsystemelements_analysis","Analyser_Starts_Analysis",null));
            List<FluidInformation> fluidData = Collections.singletonList(providedFluid);
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));
            //Gson gson = new Gson();
            //String json = gson.toJson(providedFluid);
            kafkaTemplate.send(new ProducerRecord<String,Map>("fluidsystemelements_analysis","Analyser_Finished",createAnalysisValues(fluidData)));
            return ResponseEntity.ok(fluidData);
        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }
    @GetMapping(path="/shutdown")
    public void shutdown(){
        SpringApplication.exit(appContext, () -> 0);
    }


    private Map createAnalysisValues(List<FluidInformation> requestData){
        Map analysisValuesMap = new HashMap();
        analysisValuesMap.put("","");
        return analysisValuesMap;
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

        private int id;
        private String name;
        private String fuelsystem;

        private String exhaustsystem;

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
