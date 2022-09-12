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

    @GetMapping(path="/status")
    public String getStatus(){ return status;}

    @GetMapping("sendKafkaMessage/{message}")
    public HttpStatus sendKafkaMessage(@PathVariable String message){
        sendMessage(message);
        return HttpStatus.OK;
    }

    @KafkaListener(topics="fluidsystemelements_analysis", groupId = "One")
    public void listen(ConsumerRecord<?, ?> record ){
        try {
            String recordKey = record.key().toString();

            if (recordKey.equals("WF_Starts_Fluidsystemelements_Analysis")) {
                HashMap startMap = (HashMap)record.value();
                String id = (String)startMap.get("id");

                FluidInformation data = new FluidInformation();
                data.id = id;
                data.exhaustsystem = (Boolean)startMap.get("exhaust_system");
                data.fuelsystem = (String)startMap.get("fuel_system");

                performAnalysis(id,data);

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
            Map analysisResult = performAnalysis(fluidData.get(0).id,fluidData.get(0));
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


    private Map createAnalysisValues(String id, FluidInformation data){
        Random rand = new Random();
        Map analysisValuesMap = new HashMap();
        analysisValuesMap.put("id",id);

        if((data.fuelsystem != null) && (data.fuelsystem != "")) {
            HashMap fuelsystemMap = new HashMap();
            fuelsystemMap.put(data.fuelsystem,(rand.nextFloat() * (100 - 1) + 1));
            analysisValuesMap.put("fuel_system", fuelsystemMap);
        }else{
            analysisValuesMap.put("fuel_system",null);
        }
        if((data.exhaustsystem != null)) {
            HashMap exhaustsystemMap = new HashMap();
            exhaustsystemMap.put(data.exhaustsystem,(rand.nextFloat() * (100 - 1) + 1));
            analysisValuesMap.put("exhaust_system", exhaustsystemMap);
        }else{
            analysisValuesMap.put("exhaust_system",null);
        }

        return analysisValuesMap;
    }

    private Map performAnalysis(String id, FluidInformation data)
    {
        try {

            HashMap startingMap = new HashMap();
            startingMap.put("id",data.id);

            SplittableRandom random = new SplittableRandom();
            // Probability of 20% to fail
            if(random.nextInt(1,11) <= 3)
            {
                this.status = "Error";
                kafkaTemplate.send(new ProducerRecord<String,Map>("fluidsystemelements_analysis","Analyser_In_Error-State",startingMap));
                return null;
            }

            kafkaTemplate.send(new ProducerRecord<String, Map>("fluidsystemelements_analysis", "Analyser_Starts_Analysis", startingMap));
            status = "Started";
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));
            Map calculationResults = createAnalysisValues(id, data);
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
        private String fuelsystem;

        private Boolean exhaustsystem;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFuelsystem() {
            return fuelsystem;
        }

        public void setFuelsystem(String fuelsystem) {
            this.fuelsystem = fuelsystem;
        }

        public Boolean getExhaustsystem() {return exhaustsystem;}

        public void setExhaustsystem(Boolean exhaustsystem) { this.exhaustsystem = exhaustsystem;}

    }
}
