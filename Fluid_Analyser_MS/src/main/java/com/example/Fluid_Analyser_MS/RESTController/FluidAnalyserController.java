package com.example.Fluid_Analyser_MS.RESTController;
//import com.netflix.appinfo.InstanceInfo;
//import com.netflix.discovery.EurekaClient;
//import com.netflix.discovery.shared.Application;
import com.example.Fluid_Analyser_MS.Motor;
import com.fasterxml.jackson.databind.util.JSONPObject;
import netscape.javascript.JSObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.bson.json.JsonObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


@RestController
public class FluidAnalyserController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String msg){
        kafkaTemplate.send("analyse",msg);
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

    @KafkaListener(topics="analyse", groupId = "1")
    public void listen(ConsumerRecord<?, ?> record ){


        if(record.key().toString().equals("Fluid-Analysis-Result")) {
            JSONObject obj = new JSONObject(record.value().toString());
            JSONArray array = obj.getJSONArray("Fluid");

            for (int i = 0; i < array.length(); i++) {
                JSONObject innerObject = array.getJSONObject(i);
                for (Iterator it = innerObject.keys(); it.hasNext(); ) {
                    String key = (String) it.next();
                    System.out.println("Received Message in group - group-id " + key + " " + innerObject.get(key));
                }
            }
        }

    }
    @GetMapping(path="/information")
    public String showInfo() {return "Name: Fluid-Analyser\nType: Microservice\nVersion: 1.0.0";}

    @PostMapping(path="/analyse")
    public ResponseEntity<List<FluidInformation>> getData(@RequestBody FluidInformation providedFluid){
        try {
            List<FluidInformation> fluidData = Collections.singletonList(providedFluid);
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));

            JSONObject fluidJson = new JSONObject();
            JSONArray fluidpropertiesArray = new JSONArray();
            //JSONObject fluidpropertiesJson = new JSONObject();
            JSONObject fuelsystemObject = new JSONObject().put("Fuel System", providedFluid.fuelsystem);
            JSONObject exhaustsystemObject = new JSONObject().put("Exhaust System", providedFluid.exhaustsystem);
            JSONObject statusObject = new JSONObject().put("Status","Analysis Completed");
            JSONObject successObject = new JSONObject().put("Success","True");

            fluidpropertiesArray.put(fuelsystemObject);
            fluidpropertiesArray.put(exhaustsystemObject);
            fluidpropertiesArray.put(statusObject);
            fluidpropertiesArray.put(successObject);

            //fluidpropertiesArray.put(fluidpropertiesJson);
            fluidJson.put("Fluid",fluidpropertiesArray);
            kafkaTemplate.send(new ProducerRecord<String,String>("analyse","Fluid-Analysis-Result",fluidJson.toString()));
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
