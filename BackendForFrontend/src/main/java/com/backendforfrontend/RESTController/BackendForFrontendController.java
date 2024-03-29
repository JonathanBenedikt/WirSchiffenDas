package com.backendforfrontend.RESTController;

import com.backendforfrontend.StatusRequestService;
import com.google.gson.Gson;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;
import java.util.function.Supplier;

@EnableEurekaClient
@RestController
//@CrossOrigin(origins = {'http:local'})
public class BackendForFrontendController {

    @Autowired
    private EurekaClient discoveryClient;
    @Autowired
    private KafkaTemplate<String, Map> kafkaTemplate;
    @Autowired
    private ApplicationContext appContext;

    private static Map<String, ArrayList<AnalyserStatus>> analysisMapper;
    private static Map<String, Map> responseMapper;
    private static Map<String, Map> requestMapper;

    private CircuitBreaker fluidCircuitBreaker;
    private CircuitBreaker powerCircuitBreaker;
    private CircuitBreaker coolingCircuitBreaker;
    private CircuitBreaker startingCircuitBreaker;

    private static String lastFluidReponse;
    private static String lastPowerRepsonse;
    private static String lastCoolingResponse;
    private static String lastStartingResponse;

    private static Boolean coolingAnalyserWorking = false;
    private static Boolean fluidAnalyserWorking = false;
    private static Boolean powerAnalyserWorking = false;
    private static Boolean startingAnalyserWorking = false;
    public BackendForFrontendController()
    {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom().slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED).slidingWindowSize(10).failureRateThreshold(70.0f).build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        fluidCircuitBreaker = registry.circuitBreaker("fluidStatusService");
        powerCircuitBreaker = registry.circuitBreaker("powerStatusService");
        coolingCircuitBreaker = registry.circuitBreaker("coolingStatusService");
        startingCircuitBreaker = registry.circuitBreaker("startingStatusService");
        analysisMapper = new HashMap<String, ArrayList<AnalyserStatus>>();
        responseMapper = new HashMap<>();
        requestMapper = new HashMap<>();
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
            HashMap resultMap = (HashMap) record.value();

            if(recordKey.equals("Analyser_In_Error-State"))
            {
                setAnalysisStatus((String)resultMap.get("id"),"coolingsystem","Error");
                coolingAnalyserWorking = false;
            }
            else if(recordKey.equals("Analyser_Starts_Analysis"))
            {
                setAnalysisStatus((String)resultMap.get("id"),"coolingsystem","Started");
                coolingAnalyserWorking = true;
            }
            else if (recordKey.equals("Analyser_Finished"))
            {
                HashMap responseMap = (HashMap) responseMapper.get((String)resultMap.get("id"));
                Iterator it = resultMap.entrySet().iterator();
                while(it.hasNext())
                {
                    Map.Entry pair = (Map.Entry)it.next();
                    responseMap.put(pair.getKey(),pair.getValue());
                }

                setAnalysisStatus((String)resultMap.get("id"),"coolingsystem","Finished");
                coolingAnalyserWorking = false;
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
            HashMap resultMap = (HashMap) record.value();

            if(recordKey.equals("Analyser_In_Error-State"))
            {
                setAnalysisStatus((String)resultMap.get("id"),"fluidsystem","Error");
                fluidAnalyserWorking = false;
            }
            else if(recordKey.equals("Analyser_Starts_Analysis"))
            {
                setAnalysisStatus((String)resultMap.get("id"),"fluidsystem","Started");
                fluidAnalyserWorking = true;
            }
            else if (recordKey.equals("Analyser_Finished"))
            {
                HashMap responseMap = (HashMap) responseMapper.get((String)resultMap.get("id"));
                Iterator it = resultMap.entrySet().iterator();
                while(it.hasNext())
                {
                    Map.Entry pair = (Map.Entry)it.next();
                    responseMap.put(pair.getKey(),pair.getValue());
                }
                setAnalysisStatus((String)resultMap.get("id"),"fluidsystem","Finished");
                fluidAnalyserWorking = false;
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
            HashMap resultMap = (HashMap) record.value();

            if(recordKey.equals("Analyser_In_Error-State"))
            {
                setAnalysisStatus((String)resultMap.get("id"),"powertransmissionsystem","Error");
                powerAnalyserWorking = false;
            }
            else if(recordKey.equals("Analyser_Starts_Analysis"))
            {

                setAnalysisStatus((String)resultMap.get("id"),"powertransmissionsystem","Started");
                powerAnalyserWorking = true;
            }
            else if (recordKey.equals("Analyser_Finished"))
            {
                HashMap responseMap = (HashMap) responseMapper.get((String)resultMap.get("id"));
                Iterator it = resultMap.entrySet().iterator();
                while(it.hasNext())
                {
                    Map.Entry pair = (Map.Entry)it.next();
                    responseMap.put(pair.getKey(),pair.getValue());
                }

                setAnalysisStatus((String)resultMap.get("id"),"powertransmissionsystem","Finished");
                powerAnalyserWorking = false;
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
            HashMap resultMap = (HashMap) record.value();

            if(recordKey.equals("Analyser_In_Error-State"))
            {
                setAnalysisStatus((String)resultMap.get("id"),"startingsystem","Error");
                startingAnalyserWorking = false;
            }
            else if(recordKey.equals("Analyser_Starts_Analysis"))
            {
                setAnalysisStatus((String)resultMap.get("id"),"startingsystem","Started");
                startingAnalyserWorking = true;
            }
            else if (recordKey.equals("Analyser_Finished"))
            {

                HashMap responseMap = (HashMap) responseMapper.get((String)resultMap.get("id"));
                Iterator it = resultMap.entrySet().iterator();
                while(it.hasNext())
                {
                    Map.Entry pair = (Map.Entry)it.next();
                    responseMap.put(pair.getKey(),pair.getValue());
                }
                setAnalysisStatus((String)resultMap.get("id"),"startingsystem","Finished");
                startingAnalyserWorking = false;
            }

        }catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    @GetMapping(path="/retryFluidsystem")
    public void retryFluidsystem()
    {
        for(Map map : requestMapper.values())
        {
            this.kafkaTemplate.send(new ProducerRecord<>("wf_bff", "BFF_RestartingFluidsystem", map));
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
        if(fluidAnalyserWorking)
            return "Running";

        InstanceInfo info = discoveryClient.getApplication("Fluid_Analyser").getInstances().get(0);
        StatusRequestService requestService = new StatusRequestService("http://"+info.getIPAddr()+":"+info.getPort()+"/status");
        Supplier<String> statusSupplier = () -> requestService.fetchStatus();
        Supplier<String> decoratedStatusSupplier = Decorators.ofSupplier(statusSupplier).withCircuitBreaker(fluidCircuitBreaker).withFallback( e -> this.getFluidsystemFallback()).decorate();
        String response = decoratedStatusSupplier.get();
        return response;

    }

    public String getFluidsystemFallback()
    {
        return "The Fluidsystem-Analyser is currently unreachable...";
    }

    @GetMapping(path="/retryPowersystem")
    public void retryPowersystem()
    {
        for(Map map : requestMapper.values())
        {
            this.kafkaTemplate.send(new ProducerRecord<>("wf_bff", "BFF_RestartingPowersystem", map));
        }


    }

    @GetMapping(path="/getPowertransmissionsystemStatus")
    public String getPowertransmissionsystemStatus()
    {
        if(powerAnalyserWorking)
            return "Running";

        InstanceInfo info = discoveryClient.getApplication("PowerTransmissionElements_Analyser").getInstances().get(0);
        StatusRequestService requestService = new StatusRequestService("http://"+info.getIPAddr()+":"+info.getPort()+"/status");
        Supplier<String> statusSupplier = () -> requestService.fetchStatus();
        Supplier<String> decoratedStatusSupplier = Decorators.ofSupplier(statusSupplier).withCircuitBreaker(powerCircuitBreaker).withFallback( e -> this.getPowerTransmissionsystemFallback()).decorate();
        String response = decoratedStatusSupplier.get();
        return response;
    }

    public String getPowerTransmissionsystemFallback()
    {
        return "The PowerTransmissionsystem-Analyser is currently unreachable...";
    }


    @GetMapping(path="/getAllStati")
    public String[] getAllStatusresults(){
        String[] currentStati = new String[4];
        currentStati[0] = this.getCoolingsystemStatus();
        currentStati[1] = this.getFluidsystemStatus();
        currentStati[2] = this.getPowertransmissionsystemStatus();
        currentStati[3] = this.getStartingsystemStatus();
        return currentStati;
    }

    @GetMapping(path="/retryCoolingsystem")
    public void retryCoolingsystem()
    {
        for(Map map : requestMapper.values())
        {
            this.kafkaTemplate.send(new ProducerRecord<>("wf_bff", "BFF_RestartingCoolingsystem", map));
        }

    }

    @GetMapping(path="/getCoolingsystemStatus")
    public String getCoolingsystemStatus()
    {
        if(coolingAnalyserWorking)
            return "Running";

       // Gson gson = new Gson();
       // String json = gson.toJson(responseMapper.get(currentID));

        InstanceInfo info = discoveryClient.getApplication("CoolingSystems_Analyser").getInstances().get(0);
        StatusRequestService requestService = new StatusRequestService("http://"+info.getIPAddr()+":"+info.getPort()+"/status");
        Supplier<String> statusSupplier = () -> requestService.fetchStatus();
        Supplier<String> decoratedStatusSupplier = Decorators.ofSupplier(statusSupplier).withCircuitBreaker(coolingCircuitBreaker).withFallback( e -> this.getCoolingsystemFallback()).decorate();
        String response = decoratedStatusSupplier.get();
        return response;
    }

    public String getCoolingsystemFallback()
    {
        return "The Coolingsystem-Analyser is currently unreachable...";
    }

    @GetMapping(path="/retryStartingsystem")
    public void retryStartingsystem()
    {
        for(Map map : requestMapper.values())
        {
            this.kafkaTemplate.send(new ProducerRecord<>("wf_bff", "BFF_RestartingStartingsystem", map));
        }

    }

    @GetMapping(path="/getStartingsystemStatus")
    public String getStartingsystemStatus()
    {
        if(startingAnalyserWorking)
            return "Running";

        InstanceInfo info = discoveryClient.getApplication("StartingElements_Analyser").getInstances().get(0);
        StatusRequestService requestService = new StatusRequestService("http://"+info.getIPAddr()+":"+info.getPort()+"/status");
        Supplier<String> statusSupplier = () -> requestService.fetchStatus();
        Supplier<String> decoratedStatusSupplier = Decorators.ofSupplier(statusSupplier).withCircuitBreaker(startingCircuitBreaker).withFallback( e -> this.getStartingsystemFallback()).decorate();
        String response = decoratedStatusSupplier.get();
        return response;
    }

    public String getStartingsystemFallback()
    {
        return "The Startingsystem-Analyser is currently unreachable...";
    }

    @PostMapping(path="/startAnalysis")
    public ResponseEntity<String> getData(@RequestBody  Configdata configData){
        try {
            System.out.println(configData);
            String id = UUID.randomUUID().toString();
            this.currentID = id;
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
            requestMapper.put(id,startingMap);

            HashMap finalResponseMap = new HashMap();
            finalResponseMap.put("id",id);
            responseMapper.put(id,finalResponseMap);

            this.kafkaTemplate.send(new ProducerRecord<>("wf_bff", "BFF_AnalysisStartingRequest", startingMap));
            /*
            while(!checkAllAnalyserFinished(analyserStatusList))
            {}

            analysisMapper.remove(analyserStatusList);
            Gson gson = new Gson();
            String resultJSON = gson.toJson(responseMapper.get(id));
            finalResponseMap = null;
            responseMapper.remove(id);
            */
            return ResponseEntity.ok(null);
        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }

    private String currentID;

    @GetMapping("/getSimulationresults")
    public List<Simulationresult> getSimulationresults(){
        ArrayList<AnalyserStatus> stati = this.analysisMapper.get(this.currentID);
        if(! stati.stream().allMatch((status) -> status.analysisStatus.equals("Finished"))){
            return null;
        }
        List<Simulationresult> simres = new LinkedList<>();
        HashMap<String, Map> simulationresultResponses = (HashMap<String, Map>) this.responseMapper.get(this.currentID);

        for (var entry : simulationresultResponses.entrySet()) {
            String name = entry.getKey();
            if(name.equals("id"))
            {
                continue;
            }
            Map tmp = entry.getValue();
            if (tmp == null) {
                simres.add(new Simulationresult(name, "", 0.0));
            } else {
                tmp.forEach((k,v) -> simres.add(new Simulationresult(name, (String) k, (double) v)));
            }
        }

        return simres;
    }

    class Simulationresult{
        String name;
        String choosenOption;
        double simulationresult;

        public Simulationresult(){

        }

        @Override
        public String toString() {
            return "Simulationresult{" +
                    "name='" + name + '\'' +
                    ", choosenOption='" + choosenOption + '\'' +
                    ", simulationresult=" + simulationresult +
                    '}';
        }

        public Simulationresult(String name, String choosenOption, double simulationresult) {
            this.name = name;
            this.choosenOption = choosenOption;
            this.simulationresult = simulationresult;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getChoosenOption() {
            return choosenOption;
        }

        public void setChoosenOption(String choosenOption) {
            this.choosenOption = choosenOption;
        }

        public double getSimulationresult() {
            return simulationresult;
        }

        public void setSimulationresult(float simulationresult) {
            this.simulationresult = simulationresult;
        }
    }

    class AnalyserStatus
    {
        String name;
        String analysisStatus;
    }

    public static class Configdata{
        //Info for Coolingsystem
        String oil_system;

        String cooling_system;

        //Info for Fluidsystem
        String fuel_system;
        Boolean exhaust_system;

        //Info for Powertransmissionsystem
        Boolean resilient_mounts;
        Boolean bluevision;
        Boolean torsionally_resilient_coupling;
        String[] gearbox_options;

        //Info for Startingsystem
        Boolean air_starter;
        String auxiliary_PTO;
        Boolean engine_management_system;

        public String getOil_system() {
            return oil_system;
        }

        public void setOil_system(String oil_system) {
            this.oil_system = oil_system;
        }

        public String getCooling_system() {
            return cooling_system;
        }

        public void setCooling_system(String cooling_system) {
            this.cooling_system = cooling_system;
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
