package com.backendforfrontend.RESTController;

import com.backendforfrontend.StatusRequestService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.ConfigData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@RestController
//@CrossOrigin(origins = {'http:local'})
public class BackendForFrontendController {

    @Autowired
    private KafkaTemplate<String, Map> kafkaTemplate;
    @Autowired
    private ApplicationContext appContext;

    private static Map<String, ArrayList<AnalyserStatus>> analysisMapper;

    private CircuitBreaker fluidCircuitBreaker;
    private CircuitBreaker powerCircuitBreaker;
    private CircuitBreaker coolingCircuitBreaker;
    private CircuitBreaker startingCircuitBreaker;

    private static String lastFluidReponse;
    private static String lastPowerRepsonse;
    private static String lastCoolingResponse;
    private static String lastStartingResponse;

    public BackendForFrontendController()
    {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom().slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED).slidingWindowSize(10).failureRateThreshold(70.0f).build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        fluidCircuitBreaker = registry.circuitBreaker("fluidStatusService");
        powerCircuitBreaker = registry.circuitBreaker("powerStatusService");
        coolingCircuitBreaker = registry.circuitBreaker("coolingStatusService");
        startingCircuitBreaker = registry.circuitBreaker("startingStatusService");
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
        /*
        StatusRequestService requestService = new StatusRequestService("http://localhost:8081/status");
        RetryConfig config = RetryConfig.custom().maxAttempts(10).waitDuration(Duration.of(2, ChronoUnit.SECONDS)).build();
        RetryRegistry registry = RetryRegistry.of(config);
        Retry retry = registry.retry("requestServiceRetry");
        Supplier<String> statusRequest = () -> requestService.fetchStatus();
        Supplier<String> retryingStatusRequest = Retry.decorateSupplier(retry,statusRequest);
        return retryingStatusRequest.get();
        */
        StatusRequestService requestService = new StatusRequestService("http://localhost:8081/status");
        Supplier<String> statusSupplier = () -> requestService.fetchStatus();
        Supplier<String> decoratedStatusSupplier = Decorators.ofSupplier(statusSupplier).withCircuitBreaker(fluidCircuitBreaker).withFallback( e -> this.getFluidsystemFallback()).decorate();
        String response = decoratedStatusSupplier.get();
        if(response != null)
            lastFluidReponse = response;
        return response;

    }

    public String getFluidsystemFallback()
    {
        return "The Fluidsystem-Analyser is unreachable. The last good response was "+lastFluidReponse;
    }



    @GetMapping(path="/getPowertransmissionsystemStatus")
    public String getPowertransmissionsystemStatus()
    {
        StatusRequestService requestService = new StatusRequestService("http://localhost:8082/status");
        Supplier<String> statusSupplier = () -> requestService.fetchStatus();
        Supplier<String> decoratedStatusSupplier = Decorators.ofSupplier(statusSupplier).withCircuitBreaker(powerCircuitBreaker).withFallback( e -> this.getPowerTransmissionsystemFallback()).decorate();
        String response = decoratedStatusSupplier.get();
        if(response != null)
            lastPowerRepsonse = response;
        return response;
    }

    public String getPowerTransmissionsystemFallback()
    {
        return "The PowerTransmissionsystem-Analyser is unreachable. The last good response was "+lastPowerRepsonse;
    }


    @GetMapping(path="/getAllStatusresults")
    public String[] getAllStatusresults(){
        String[] currentStati = new String[4];
        currentStati[0] = this.getCoolingsystemStatus();
        currentStati[1] = this.getFluidsystemStatus();
        currentStati[2] = this.getPowertransmissionsystemStatus();
        currentStati[3] = this.getStartingsystemStatus();
        return currentStati;
    }

    @GetMapping(path="/getCoolingsystemStatus")
    public String getCoolingsystemStatus()
    {
        StatusRequestService requestService = new StatusRequestService("http://localhost:8080/status");
        Supplier<String> statusSupplier = () -> requestService.fetchStatus();
        Supplier<String> decoratedStatusSupplier = Decorators.ofSupplier(statusSupplier).withCircuitBreaker(coolingCircuitBreaker).withFallback( e -> this.getCoolingsystemFallback()).decorate();
        String response = decoratedStatusSupplier.get();
        if(response != null)
            lastCoolingResponse = response;
        return response;
    }

    public String getCoolingsystemFallback()
    {
        return "The Coolingsystem-Analyser is unreachable. The last good response was "+lastCoolingResponse;
    }

    @GetMapping(path="/getStartingsystemStatus")
    public String getStartingsystemStatus()
    {
        StatusRequestService requestService = new StatusRequestService("http://localhost:8083/status");
        Supplier<String> statusSupplier = () -> requestService.fetchStatus();
        Supplier<String> decoratedStatusSupplier = Decorators.ofSupplier(statusSupplier).withCircuitBreaker(startingCircuitBreaker).withFallback( e -> this.getStartingsystemFallback()).decorate();
        String response = decoratedStatusSupplier.get();
        if(response != null)
            lastStartingResponse = response;
        return response;
    }

    public String getStartingsystemFallback()
    {
        return "The Startingsystem-Analyser is unreachable. The last good response was "+lastStartingResponse;
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
    public ResponseEntity<Map> getData(@RequestBody  Configdata configData){
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
            startingMap.put("configdata",configData);
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


    public static class Configdata{
        String oil_system;
        String cooling_system;

        String fuel_system;
        Boolean exhaust_system;

        Boolean resilient_mounts;
        Boolean bluevision;
        Boolean torsionally_resilient_coupling;
        String[] gearbox_options;

        Boolean air_starter;
        String auxiliary_PTO;
        Boolean engine_management_system;

        public String getOil_system() {
            return oil_system;
        }

        public void setOil_system(String oil_system) {
            this.oil_system = oil_system;
        }

        public String getFuel_system() {
            return fuel_system;
        }

        public void setFuel_system(String fuel_system) {
            this.fuel_system = fuel_system;
        }

        public Boolean getExhaust_system() {
            return exhaust_system;
        }

        public void setExhaust_system(Boolean exhaust_system) {
            this.exhaust_system = exhaust_system;
        }

        public Boolean getResilient_mounts() {
            return resilient_mounts;
        }

        public void setResilient_mounts(Boolean resilient_mounts) {
            this.resilient_mounts = resilient_mounts;
        }

        public Boolean getBluevision() {
            return bluevision;
        }

        public void setBluevision(Boolean bluevision) {
            this.bluevision = bluevision;
        }

        public Boolean getTorsionally_resilient_coupling() {
            return torsionally_resilient_coupling;
        }

        public void setTorsionally_resilient_coupling(Boolean torsionally_resilient_coupling) {
            this.torsionally_resilient_coupling = torsionally_resilient_coupling;
        }

        public String[] getGearbox_options() {
            return gearbox_options;
        }

        public void setGearbox_options(String[] gearbox_options) {
            this.gearbox_options = gearbox_options;
        }

        public Boolean getAir_starter() {
            return air_starter;
        }

        public void setAir_starter(Boolean air_starter) {
            this.air_starter = air_starter;
        }

        public String getAuxiliary_PTO() {
            return auxiliary_PTO;
        }

        public void setAuxiliary_PTO(String auxiliary_PTO) {
            this.auxiliary_PTO = auxiliary_PTO;
        }

        public Boolean getEngine_management_system() {
            return engine_management_system;
        }

        public void setEngine_management_system(Boolean engine_management_system) {
            this.engine_management_system = engine_management_system;
        }

        @Override
        public String toString() {
            return "Configdata{" +
                    "oil_system='" + oil_system + '\'' +
                    ", cooling_system='" + cooling_system + '\'' +
                    ", fuel_system='" + fuel_system + '\'' +
                    ", exhaust_system=" + exhaust_system +
                    ", resilient_mounts=" + resilient_mounts +
                    ", bluevision=" + bluevision +
                    ", torsionally_resilient_coupling=" + torsionally_resilient_coupling +
                    ", gearbox_options=" + Arrays.toString(gearbox_options) +
                    ", air_starter=" + air_starter +
                    ", auxiliary_PTO='" + auxiliary_PTO + '\'' +
                    ", engine_management_system=" + engine_management_system +
                    '}';
        }
    }
}
