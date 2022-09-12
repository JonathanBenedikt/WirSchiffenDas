package com.example.PowerTransmissionElements_Analyser_MS.RESTController;

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
public class PowerTransmissionElementsAnalyserController {

    @Autowired
    private EurekaClient eurekaClient;
    @Autowired
    private KafkaTemplate<String, Map> kafkaTemplate;

    @Autowired
    private ApplicationContext appContext;

    private String status;

    public PowerTransmissionElementsAnalyserController(){
        status = "Idle";
    }
    public void sendMessage(String msg){

        kafkaTemplate.send("powertransmissionsystemelements_analysis",new HashMap(){{put("Message",msg);}});
    }

    @KafkaListener(topics="powertransmissionsystemelements_analysis", groupId = "One")
    public void listen(ConsumerRecord<?, ?> record ){
        try {
            String recordKey = record.key().toString();

            if (recordKey.equals("WF_Starts_Powertransmissionsystemelements_Analysis")) {
                HashMap startMap = (HashMap)record.value();
                String id = (String)startMap.get("id");

                PowerTransmissionElementsInformation data = new PowerTransmissionElementsInformation();
                data.id = id;
                data.resilientmounts = (Boolean)startMap.get("resilient_mounts");
                data.bluevision = (Boolean)startMap.get("bluevision");
                data.torsionallyresilientcoupling = (Boolean)startMap.get("torsionally_resilient_coupling");
                ArrayList<String> gearboxArray = (ArrayList) startMap.get("gearbox_options");
                data.gearboxoptions = gearboxArray.toArray(new String[0]);
                performAnalysis(id,data);

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

    @GetMapping(path="/status")
    public String getStatus(){ return status;}
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
            Map analysisResult = performAnalysis(powertransmissionData.get(0).id,powertransmissionData.get(0));
            return ResponseEntity.ok(analysisResult);
        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }

    private Map createAnalysisValues(String id, PowerTransmissionElementsInformation data){
        Random rand = new Random();
        Map analysisValuesMap = new HashMap();
        analysisValuesMap.put("id",id);

        if((data.resilientmounts != null)) {
            HashMap resilientmountsMap = new HashMap();
            resilientmountsMap.put(data.resilientmounts,(rand.nextFloat() * (100 - 1) + 1));
            analysisValuesMap.put("resilient_mounts",resilientmountsMap);
        }else{
            analysisValuesMap.put("resilient_mounts", null);
        }

        if((data.bluevision != null)) {
            HashMap bluevisionMap = new HashMap();
            bluevisionMap.put(data.bluevision,(rand.nextFloat() * (100 - 1) + 1));
            analysisValuesMap.put("bluevision",bluevisionMap);
        }else {
            analysisValuesMap.put("bluevision",null);
        }

        if((data.torsionallyresilientcoupling != null)) {
            HashMap torsionallyresMap = new HashMap();
            torsionallyresMap.put(data.torsionallyresilientcoupling,(rand.nextFloat() * (100 - 1) + 1));
            analysisValuesMap.put("torsionally_resilient_coupling",torsionallyresMap);
        }else{
            analysisValuesMap.put("torsionally_resilient_coupling",null);
        }

        if((data.gearboxoptions != null)){
            HashMap gearboxMap = new HashMap();
            for(Object o: data.gearboxoptions)
            {
                gearboxMap.put(o,(rand.nextFloat() * (100 - 1) + 1));
            }
            analysisValuesMap.put("gearboxoptions",gearboxMap);
        }else{
            analysisValuesMap.put("gearboxoptions",null);
        }

        return analysisValuesMap;
    }

    private Map performAnalysis(String id, PowerTransmissionElementsInformation data)
    {
        try{
            HashMap startingMap = new HashMap();
            startingMap.put("id",data.id);

            SplittableRandom random = new SplittableRandom();
            // Probability of 20% to fail
            if(random.nextInt(1,11) <= 3)
            {
                this.status = "Error";
                kafkaTemplate.send(new ProducerRecord<String,Map>("powertransmissionsystemelements_analysis","Analyser_In_Error-State",startingMap));
                return null;
            }


            kafkaTemplate.send(new ProducerRecord<String,Map>("powertransmissionsystemelements_analysis","Analyser_Starts_Analysis",startingMap));
            status = "Started";

            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));
            Map calculationResults = createAnalysisValues(id,data);

            status = "Finished";
            kafkaTemplate.send(new ProducerRecord<String,Map>("powertransmissionsystemelements_analysis","Analyser_Finished",calculationResults));
            return calculationResults;
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
        return null;
    }

    public static class PowerTransmissionElementsInformation {

        private String id;
        private Boolean resilientmounts;
        private Boolean bluevision;
        private Boolean torsionallyresilientcoupling;
        private String[] gearboxoptions;

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }


        public Boolean getResilientmounts() {
            return resilientmounts;
        }
        public void setResilientmounts(Boolean resilientmounts) {
            this.resilientmounts = resilientmounts;
        }

        public Boolean getBluevision() {return bluevision;}
        public void setBluevision(Boolean bluevision) { this.bluevision = bluevision;}

        public Boolean getTorsionallyresilientcoupling() { return torsionallyresilientcoupling; }
        public void setTorsionallyresilientcoupling(Boolean torsionallyresilientcoupling) { this.torsionallyresilientcoupling = torsionallyresilientcoupling; }

        public String[] getGearboxoptions() { return gearboxoptions; }
        public void setGearboxoptions(String[] gearboxoptions) { this.gearboxoptions = gearboxoptions; }
    }
}
