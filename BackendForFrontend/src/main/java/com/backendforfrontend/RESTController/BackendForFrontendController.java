package com.backendforfrontend.RESTController;




import com.backendforfrontend.StatusRequestService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Supplier;

@RestController
public class BackendForFrontendController {

    @Autowired
    private KafkaTemplate<String, Map> kafkaTemplate;
    @Autowired
    private ApplicationContext appContext;

    private static Map<String, ArrayList<AnalyserStatus>> analysisMapper;

    public BackendForFrontendController()
    {
       analysisMapper = new HashMap<String, ArrayList<AnalyserStatus>>();
    }

    public void setAnalysisStatus(String id, String analyserName, String currentStatus)
    {
        if(analysisMapper == null)
            return;

        ArrayList<AnalyserStatus> currentAnalyserList = analysisMapper.get(id);
        if(currentAnalyserList != null) {
            for (AnalyserStatus ana : currentAnalyserList) {
                if (ana.name == analyserName) {
                    ana.analysisStatus = currentStatus;
                }
            }
        }
    }



    public String getAnalysisStatus(String id, String analyserName)
    {
        ArrayList<AnalyserStatus> currentAnalyserList = analysisMapper.get(id);
        for(AnalyserStatus ana : currentAnalyserList)
        {
            if(ana.name == analyserName)
                return ana.analysisStatus;
        }
        return "Error";
    }

    private boolean checkAllAnalyserFinished(ArrayList<AnalyserStatus> statusList)
    {
        return statusList.stream().allMatch(t -> t.analysisStatus == "Finished");
    }

    private boolean checkAnalyzerResponded(ArrayList<AnalyserStatus> status)
    {
        return status.stream().allMatch(t -> t.analysisStatus != null);
    }

    private void performResponse()
    {

    }

    @GetMapping(path="/information")
    public String showInfo() {return "Name: BackendForFrontEnd\nType: Microservice\nVersion: 1.0.0";}

    @GetMapping(path="/shutdown")
    public void shutdown(){
        SpringApplication.exit(appContext, () -> 0);
    }

    @KafkaListener(topics="coolingsystemelements_analysis", groupId = "Two")
    public void listenToCoolingSystem(ConsumerRecord<?, ?> record ){
        try {
            String recordKey = record.key().toString();
            System.out.println("Coolingsystem responded");

            if(recordKey.equals("Analyser_Starts_Analysis"))
            {
                HashMap resultMap = (HashMap) record.value();
                setAnalysisStatus((String)resultMap.get("id"),"coolingsystem","Started");
            }
            else if (recordKey.equals("Analyser_Finished"))
            {
                HashMap resultMap = (HashMap) record.value();
                setAnalysisStatus((String)resultMap.get("id"),"coolingsystem","Finished");
            }
            else if(recordKey.equals("Status_Response"))
            {

            }

        }catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    @KafkaListener(topics="fluidsystemelements_analysis", groupId = "Two")
    public void listenToFluidSystem(ConsumerRecord<?,?> record){
        try {
            String recordKey = record.key().toString();
            System.out.println("FluidAnalyser responded");
            if(recordKey.equals("Analyser_Starts_Analysis"))
            {
                HashMap resultMap = (HashMap) record.value();
                setAnalysisStatus((String)resultMap.get("id"),"fluidsystem","Started");
            }
            else if (recordKey.equals("Analyser_Finished"))
            {
                HashMap resultMap = (HashMap) record.value();
                setAnalysisStatus((String)resultMap.get("id"),"fluidsystem","Finished");
            }

        }catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    @KafkaListener(topics="powertransmissionsystemelements_analysis", groupId="Two")
    public void listenToPowerTransmissionSystem(ConsumerRecord<?,?> record){
        try {
            String recordKey = record.key().toString();
            System.out.println("PowertransmissionAnalyser responded");
            if(recordKey.equals("Analyser_Starts_Analysis"))
            {
                HashMap resultMap = (HashMap) record.value();
                setAnalysisStatus((String)resultMap.get("id"),"powertransmissionsystem","Started");
            }
            else if (recordKey.equals("Analyser_Finished"))
            {
                HashMap resultMap = (HashMap) record.value();
                setAnalysisStatus((String)resultMap.get("id"),"powertransmissionsystem","Finished");
            }

        }catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    @KafkaListener(topics="startingsystemelements_analysis", groupId="Two")
    public void listenToStartingSystem(ConsumerRecord<?,?> record){
        try {
            String recordKey = record.key().toString();
            System.out.println("Startingsytemelementsanalyser responded");
            if(recordKey.equals("Analyser_Starts_Analysis"))
            {
                HashMap resultMap = (HashMap) record.value();
                setAnalysisStatus((String)resultMap.get("id"),"startingsystem","Started");
            }
            else if (recordKey.equals("Analyser_Finished"))
            {
                HashMap resultMap = (HashMap) record.value();
                setAnalysisStatus((String)resultMap.get("id"),"startingsystem","Finished");
            }

        }catch (Exception ex)
        {
            System.out.println(ex);
        }
    }


    @GetMapping(path="/getFluidsystemStatus")
    public String getFluidsystemStatus()
    {

        StatusRequestService requestService = new StatusRequestService("http://localhost:8081/status");
        RetryConfig config = RetryConfig.custom().maxAttempts(10).waitDuration(Duration.of(2, ChronoUnit.SECONDS)).build();
        RetryRegistry registry = RetryRegistry.of(config);
        Retry retry = registry.retry("requestServiceRetry");
        Supplier<String> statusRequest = () -> requestService.fetchStatus();
        Supplier<String> retryingStatusRequest = Retry.decorateSupplier(retry,statusRequest);
        return retryingStatusRequest.get();

    }



    @GetMapping(path="/getPowertransmissionsystemStatus")
    public String getPowertransmissionsystemStatus()
    {
        final String uri = "http://localhost:8082/status";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }

    @GetMapping(path="/getCoolingsystemStatus")
    public String getCoolingsystemStatus()
    {
        final String uri = "http://localhost:8080/status";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }

    @GetMapping(path="/getStartingsystemStatus")
    public String getStartingsystemStatus()
    {
        final String uri = "http://localhost:8083/status";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }

    @PostMapping(path="/getAnalyzerStatus")
    public ResponseEntity<Map> getCurrentAnalyserStatus(@RequestBody String analyserInfo)
    {

        HashMap statusResponse = new HashMap();
        String id = UUID.randomUUID().toString();
        switch (analyserInfo){
            case "Coolingsystem":
                ArrayList<AnalyserStatus> coolingList = new ArrayList<>();
                AnalyserStatus coolingsystemStatus = new AnalyserStatus(){{name = "coolingsystem";}};
                coolingList.add(coolingsystemStatus);
                analysisMapper.put(id,coolingList);
                kafkaTemplate.send(new ProducerRecord<String,Map>("coolingsystemelements_analysis","Status_Request",null));

                while(!checkAnalyzerResponded(coolingList))
                {}
                statusResponse.put("analyser",analyserInfo);
                statusResponse.put("status",coolingsystemStatus);
                analysisMapper.remove(coolingList);
                break;
            case "Fluidsystem":
                ArrayList<AnalyserStatus> fluidList = new ArrayList<>();
                AnalyserStatus fluidsystemStatus = new AnalyserStatus(){{name = "fluidsystem";}};
                fluidList.add(fluidsystemStatus);
                analysisMapper.put(id,fluidList);
                kafkaTemplate.send(new ProducerRecord<String,Map>("fluidsystemelements_analysis","Status_Request",null));

                while(!checkAnalyzerResponded(fluidList))
                {}
                statusResponse.put("analyser",analyserInfo);
                statusResponse.put("status",fluidsystemStatus);

                analysisMapper.remove(fluidList);
                break;
            case "Powertransmissionsystem":
                ArrayList<AnalyserStatus> powertransmissionList = new ArrayList<>();
                AnalyserStatus powertransmissionStatus = new AnalyserStatus(){{name = "powertransmissionsystem";}};
                powertransmissionList.add(powertransmissionStatus);
                analysisMapper.put(id,powertransmissionList);
                kafkaTemplate.send(new ProducerRecord<String,Map>("powertransmissionsystemelements_analysis","Status_Request",null));

                while(!checkAnalyzerResponded(powertransmissionList))
                {}
                statusResponse.put("analyser",analyserInfo);
                statusResponse.put("status",powertransmissionStatus);

                analysisMapper.remove(powertransmissionList);
                break;
            case "Startingsystem":
                ArrayList<AnalyserStatus> startingsystemList = new ArrayList<>();
                AnalyserStatus startingsystemStatus = new AnalyserStatus(){{name = "startingsystem";}};
                startingsystemList.add(startingsystemStatus);
                analysisMapper.put(id,startingsystemList);
                kafkaTemplate.send(new ProducerRecord<String,Map>("startingsystemelements_analysis","Status_Request",null));

                while(!checkAnalyzerResponded(startingsystemList))
                {}
                statusResponse.put("analyser",analyserInfo);
                statusResponse.put("status",startingsystemStatus);

                analysisMapper.remove(startingsystemList);
                break;
            default:
               ResponseEntity.notFound();
        }


        return ResponseEntity.ok(statusResponse);
    }

    @PostMapping(path="/startAnalysis")
    public ResponseEntity<Map> getData(@RequestBody CoolingSystemInformation providedCoolingInfos){
        try {

            String id = UUID.randomUUID().toString();
            ArrayList<AnalyserStatus> analyserStatusList = new ArrayList<AnalyserStatus>();
            analysisMapper.put(id,analyserStatusList);

            AnalyserStatus coolingsystemstatus = new AnalyserStatus();
            coolingsystemstatus.name = "coolingsystem";
            analyserStatusList.add(coolingsystemstatus);

            AnalyserStatus fluidstatus = new AnalyserStatus();
            fluidstatus.name = "fluidsystem";
            analyserStatusList.add(fluidstatus);

            AnalyserStatus powertransmissionstatus = new AnalyserStatus();
            powertransmissionstatus.name = "powertransmissionsystem";
            analyserStatusList.add(powertransmissionstatus);

            AnalyserStatus startingelementsstatus = new AnalyserStatus();
            startingelementsstatus.name = "startingsystem";
            analyserStatusList.add(startingelementsstatus);

            HashMap startingMap = new HashMap();
            startingMap.put("id",id);
            startingMap.put("name","Maria Kosovo");
            this.kafkaTemplate.send(new ProducerRecord<>("wf_bff", "BFF_AnalysisStartingRequest", startingMap));

            while(!checkAllAnalyserFinished(analyserStatusList))
            {}

            analysisMapper.remove(analyserStatusList);
            return ResponseEntity.ok(null);
        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }

    class AnalyserStatus
    {
        String name;
        String analysisStatus;
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
