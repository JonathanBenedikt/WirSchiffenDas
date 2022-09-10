package com.Workflow_Engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

//TODO Wir muessen wahrscheinlich Saga Pattern implementieren...
//Allows communication between the internal Workflow, the Analyse-MS and the angular frontend
@RestController
public class WorkflowController {
    @Autowired
    KafkaTemplate<String, Map> kafkaJsontemplate;
    String analyse_topic_name = "analyse";

    private Motor motor;


    @Autowired
    private MotorRepository motors;


    //----------------------------------------------------------------------------------------------------------------
    //BFF


    @KafkaListener(topics = "wf_bff", groupId = "One") //group doesn't really matter atm
    void wfbfflistener(ConsumerRecord<String, Map> record){
        try {
            String recordKey = record.key();
            if(recordKey.equals("BFF_AnalysisStartingRequest")){
                HashMap resultMap = (HashMap) record.value();

                LinkedHashMap configmap = (LinkedHashMap) resultMap.get("configdata");
               Configdata configdata = createConfigData(configmap);


                start_coolingsystemelements_analyser((String)resultMap.get("id"),configdata);
                start_fluidsystemelements_analyser((String)resultMap.get("id"),configdata);
                start_powertransmissionsystemelements_analyse((String)resultMap.get("id"),configdata);
                start_startingsystemelements_analyser((String)resultMap.get("id"),configdata);


            }
         /*   if (recordKey.equals("BFF_Passes_Configdata")) {
                save_bff_configdata(record.value());
            } else if (recordKey.equals("BFF_Ready_For_Analysisresults")) {
                //how to get the ID here?
                send_analysisresults("");
            } else {
                System.out.println(record.key());
            }

          */
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    public Configdata createConfigData(LinkedHashMap map)
    {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(map);
        Configdata config =  gson.fromJson(jsonElement, Configdata.class);
        return config;
    }
    @PostMapping("/send_analysisresults")
    public void send_analysisresults(@RequestParam(value="ID", defaultValue = "0") String ID) {
        Motor m = findmotorbyID(ID);

    }

    private void save_bff_configdata(Map data){ //TODO write as type converter (?)
        this.motor = new Diesel(data);
        motors.save(motor);
    }


    //Aggregation of Starting Methods
    @PostMapping("/start_analysers")
    public void start_analysers(@RequestParam(value = "ID", defaultValue = "") String ID,Configdata configdata){
        start_coolingsystemelements_analyser(ID,configdata);
        start_fluidsystemelements_analyser(ID, configdata);
        start_powertransmissionsystemelements_analyse(ID, configdata);
        start_startingsystemelements_analyser(ID, configdata);
    }

    //Aggregation of Restarting Methods (maybe not necessary if direct communication between
    @PostMapping("/restart_analysers")
    public void restart_analysers(@RequestParam(value = "ID", defaultValue = "") String ID){
        restart_start_coolingsystemelements_analyser(ID);
        restart_fluidelements_analyser(ID);
        restart_powertransmissionelements_analyser(ID);
        restart_startingelements_analyser(ID);
    }


    private Motor findmotorbyID(String ID) {
        Optional<Motor> opt = this.motors.findById(ID);
        Motor m = null;
        if (opt.isEmpty()) {
            System.out.println("ID not Found");
        } else {
            m = opt.get();
        }
        return m;
    }



    //----------------------------------------------------------------------------------------------------------------
    //Coolingsystemelements

    @KafkaListener(topics = "coolingsystemelements_analysis", groupId = "Three")
    void coolingsystemelements_analyser_listener(ConsumerRecord<String, Map> record) {
        try {
            String recordKey = record.key().toString();
            if (recordKey.equals("Analyser_Starts_Analysis")) {
                return; //Nothing to do with it
            } else if (recordKey.equals("Analyser_Finished")) {
                //integrate data into the workflow
                //persist it(?)
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    void start_coolingsystemelements_analyser(String ID, Configdata confidata){
        //Motor m = findmotorbyID(ID);
        // cut up the data (
        Map coolingdata = new HashMap();
        coolingdata.put("id",ID);
        //configdata.put("oil_system", m.exhaust_system);
        coolingdata.put("oil_system", confidata.oil_system);
        //configdata.put("cooling_system", m.fuel_system);
        coolingdata.put("cooling_system",confidata.cooling_system);
        this.kafkaJsontemplate.send(new ProducerRecord<>("coolingsystemelements_analysis", "WF_Starts_Coolingsystemelements_Analysis", coolingdata));
    }



    private void restart_start_coolingsystemelements_analyser(String ID) {
    }


    //----------------------------------------------------------------------------------------------------------------
    //Fluidsystemelements

    @KafkaListener(topics = "fluidsystemelements_analysis", groupId = "Three")
    void fluidsystemelements_analyser_listener(ConsumerRecord<String, Map> record) {
        try {
            String recordKey = record.key().toString();
            if (recordKey.equals("Analyser_Starts_Analysis")) {
                return; //Nothing to do with it
            } else if (recordKey.equals("Analyser_Finished")) {
                //integrate data into the workflow
                //persist it(?)
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    void start_fluidsystemelements_analyser(String ID, Configdata configdata){
        //Motor m = findmotorbyID(ID);
        // cut up the data
        Map fluiddata = new HashMap();
        fluiddata.put("id",ID);
        fluiddata.put("exhaust_system", configdata.exhaust_system);
        fluiddata.put("fuel_system", configdata.fuel_system);
        this.kafkaJsontemplate.send(new ProducerRecord<>("fluidsystemelements_analysis", "WF_Starts_Fluidsystemelements_Analysis", fluiddata));
    }

    private void restart_fluidelements_analyser(String ID) {
    }

    //----------------------------------------------------------------------------------------------------------------
    //Powertransmissionelements
    @KafkaListener(topics = "powertransmissionsystemelements_analysis", groupId = "Three")
    void powertransmissionsystemelements_analyser_listener(ConsumerRecord<String, Map> record) {
        try {
            String recordKey = record.key().toString();
            if (recordKey.equals("Analyser_Starts_Analysis")) {
                return; //Nothing to do with it
            } else if (recordKey.equals("Analyser_Finished")) {
                //integrate data into the workflow
                //persist it(?)
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    void start_powertransmissionsystemelements_analyse(String ID, Configdata configdata){
        //Motor m = findmotorbyID(ID);
        // cut up the data
        Map powerdata = new HashMap();
        powerdata.put("id",ID);
        powerdata.put("resilient_mounts", configdata.resilient_mounts);
        powerdata.put("bluevision", configdata.bluevision);
        powerdata.put("torsionally_resilient_coupling", configdata.torsionally_resilient_coupling);
        powerdata.put("gearbox_options", configdata.gearbox_options);
        this.kafkaJsontemplate.send(new ProducerRecord<>("powertransmissionsystemelements_analysis", "WF_Starts_Powertransmissionsystemelements_Analysis", powerdata));
    }

    private void restart_powertransmissionelements_analyser(String ID) {
    }

    //----------------------------------------------------------------------------------------------------------------
    //Startingsystemelements

    @KafkaListener(topics = "startingsystemelements_analysis", groupId = "Three")
    void fluidelements_analyser_listener(ConsumerRecord<String, Map> record) {
        try {
            String recordKey = record.key().toString();
            if (recordKey.equals("Analyser_Starts_Analysis")) {
                return; //Nothing to do with it
            } else if (recordKey.equals("Analyser_Finished")) {
                //integrate data into the workflow
                //persist it(?)
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    void start_startingsystemelements_analyser(String ID, Configdata configdata){
        //Motor m = findmotorbyID(ID);
        // cut up the data
        Map startingdata = new HashMap();
        startingdata.put("id",ID);
        startingdata.put("air_starter", configdata.air_starter);
        startingdata.put("auxiliary_PTO", configdata.auxiliary_PTO);
        startingdata.put("engine_management_system", configdata.engine_management_system);
        this.kafkaJsontemplate.send(new ProducerRecord<>("startingsystemelements_analysis", "WF_Starts_Startingsystemelements_Analysis", startingdata));
    }

    private void restart_startingelements_analyser(String ID) {
    }

    //----------------------------------------------------------------------------------------------------------------



    //Just to check if kafka works locally
    @PostMapping("/sendtestJson")
    public void sendtestJson() {
        Motor testdata = new Diesel();
        testdata.ID = 0;
        testdata.oil_system = "oily";
        testdata.cooling_system = "icy";
        HashMap<String, Object> testmap = new HashMap<>();
        testmap.put("OrderNr", testdata.ID);
        testmap.put("oil_system", testdata.oil_system);
        testmap.put("cooling_system", testdata.cooling_system);
        kafkaJsontemplate.send(analyse_topic_name, testmap);
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
