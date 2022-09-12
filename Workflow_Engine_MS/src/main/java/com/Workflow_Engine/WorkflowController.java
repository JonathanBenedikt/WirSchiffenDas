package com.Workflow_Engine;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
public class WorkflowController {
    @Autowired
    KafkaTemplate<String, Map> kafkaJsontemplate;

    Database Database = new Database();

    //Testmethoden
    @GetMapping("/get")



    //----------------------------------------------------------------------------------------------------------------
    //BFF


    @KafkaListener(topics = "wf_bff", groupId = "Two")
    void wfbfflistener(ConsumerRecord<String, Map> record){
        try {
            String recordKey = record.key();
            HashMap payload = (HashMap) record.value();
            Configdata configdata = initialMessageToConfigdata(payload);
            if(recordKey.equals("BFF_AnalysisStartingRequest")){
                //Persistence
                //this.Database.save_new_workflow(configdata);
                //starting der Analyser
                start_coolingsystemelements_analyser(configdata);
                start_fluidsystemelements_analyser(configdata);
                start_powertransmissionsystemelements_analyse(configdata);
                start_startingsystemelements_analyser(configdata);
            } else if(recordKey.equals("BFF_RestartingCoolingsystem"))
            {
                start_coolingsystemelements_analyser(configdata);
            } else if(recordKey.equals("BFF_RestartingFluidsystem"))
            {
                start_fluidsystemelements_analyser(configdata);
            } else if(recordKey.equals("BFF_RestartingPowersystem"))
            {
                start_powertransmissionsystemelements_analyse(configdata);
            } else if(recordKey.equals("BFF_RestartingStartingsystem"))
            {
                start_startingsystemelements_analyser(configdata);
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

    public Configdata initialMessageToConfigdata(HashMap data){
        LinkedHashMap configmap = (LinkedHashMap) data.get("configdata");
        String id = (String)data.get("id");
        configmap.put("id", id);
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(configmap);
        Configdata config =  gson.fromJson(jsonElement, Configdata.class);
        return config;
    }

//    public Configdata createConfigData(LinkedHashMap map)
//    {
//        Gson gson = new Gson();
//        JsonElement jsonElement = gson.toJsonTree(map);
//        Configdata config =  gson.fromJson(jsonElement, Configdata.class);
//        return config;
//    }
//
//    public Configdata createConfigData(String id, LinkedHashMap map)
//    {
//        map.put("id", id);
//        Gson gson = new Gson();
//        JsonElement jsonElement = gson.toJsonTree(map);
//        Configdata config =  gson.fromJson(jsonElement, Configdata.class);
//        return config;
//    }

    //Aggregation of Starting Methods
//    @PostMapping("/start_analysers")
//    public void start_analysers(@RequestParam(value = "ID", defaultValue = "") String ID,Configdata configdata){
//        start_coolingsystemelements_analyser(ID,configdata);
//        start_fluidsystemelements_analyser(ID, configdata);
//        start_powertransmissionsystemelements_analyse(ID, configdata);
//        start_startingsystemelements_analyser(ID, configdata);
//    }

    /*//Aggregation of Restarting Methods (maybe not necessary if direct communication between
    @PostMapping("/restart_analysers")
    public void restart_analysers(@RequestParam(value = "ID", defaultValue = "") String ID){
        restart_start_coolingsystemelements_analyser(ID);
        restart_fluidelements_analyser(ID);
        restart_powertransmissionelements_analyser(ID);
        restart_startingelements_analyser(ID);
    }*/

    //----------------------------------------------------------------------------------------------------------------
    //Coolingsystemelements

    @KafkaListener(topics = "coolingsystemelements_analysis", groupId = "Three")
    void coolingsystemelements_analyser_listener(ConsumerRecord<String, Map> record) {
        try {

            String recordKey = record.key();
            if(recordKey.equals("Analyser_In_Error-State"))
            {
                return;
            }
            Map payload = record.value();
            String id = payload.get("id").toString();
            //AnalyserStati analyserStati = this.Database.getAnalyserStatiByID(id);

            if (recordKey.equals("Analyser_Starts_Analysis")) {
               // this.Database.update_analyserstatus(id, "coolingsystem", "running");
            } else if (recordKey.equals("Analyser_Finished")) {
                //Extract new Information
                /*
                Map csd  = (HashMap) payload.get("coolingsystem");
                Map osd = (HashMap) payload.get("oilsystem");
                Map<String, Double> partAndResult = new HashMap<>();
                if (!(csd == null)){
                    partAndResult.put("cooling_system", (double) csd.values().toArray()[0]);
                }
                if (!(osd == null)){
                    partAndResult.put("oil_system", (double) osd.values().toArray()[0]);
                }
                //Save to Database
                this.Database.update_simulationresuls(id, partAndResult);
                this.Database.update_analyserstatus(id, "coolingsystem", "ready");
                */

            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    void start_coolingsystemelements_analyser(Configdata configdata){
        Map coolingdata = new HashMap();
        coolingdata.put("id",configdata.id);
        coolingdata.put("oil_system", configdata.oil_system);
        coolingdata.put("cooling_system",configdata.cooling_system);
        this.kafkaJsontemplate.send(new ProducerRecord<>("coolingsystemelements_analysis", "WF_Starts_Coolingsystemelements_Analysis", coolingdata));
    }


    //----------------------------------------------------------------------------------------------------------------
    //Fluidsystemelements

    @KafkaListener(topics = "fluidsystemelements_analysis", groupId = "Three")
    void fluidsystemelements_analyser_listener(ConsumerRecord<String, Map> record) {
        try {
            String recordKey = record.key();
            if(recordKey.equals("Analyser_In_Error-State"))
            {
                return;
            }

            Map payload = record.value();
            String id = payload.get("id").toString();
            //AnalyserStati analyserStati = this.Database.getAnalyserStatiByID(id);
            if (recordKey.equals("Analyser_Starts_Analysis")) {
               // this.Database.update_analyserstatus(id, "fluidsystem", "running");
            } else if (recordKey.equals("Analyser_Finished")) {
                //Extract new Information
                /*
                Map fsd  = (HashMap) payload.get("fuel_system");
                Map esd = (HashMap) payload.get("exhaust_system");
                Map<String, Double> partAndResult = new HashMap<>();
                if (!(fsd == null)){
                    partAndResult.put("fuel_system", (double) fsd.values().toArray()[0]);
                }
                if (!(esd == null)){
                    partAndResult.put("exhaust_system", (double) esd.values().toArray()[0]);
                }
                //Save to Database
                this.Database.update_simulationresuls(id, partAndResult);
                this.Database.update_analyserstatus(id, "fluidsystem", "ready");

                 */
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    void start_fluidsystemelements_analyser(Configdata configdata){
        Map fluiddata = new HashMap();
        fluiddata.put("id",configdata.id);
        fluiddata.put("exhaust_system", configdata.exhaust_system);
        fluiddata.put("fuel_system", configdata.fuel_system);
        this.kafkaJsontemplate.send(new ProducerRecord<>("fluidsystemelements_analysis", "WF_Starts_Fluidsystemelements_Analysis", fluiddata));
    }


    //----------------------------------------------------------------------------------------------------------------
    //Powertransmissionelements
    @KafkaListener(topics = "powertransmissionsystemelements_analysis", groupId = "Three")
    void powertransmissionsystemelements_analyser_listener(ConsumerRecord<String, Map> record) {
        try {
            String recordKey = record.key();
            if(recordKey.equals("Analyser_In_Error-State"))
            {
                return;
            }

            Map payload = record.value();
            String id = payload.get("id").toString();
            //AnalyserStati analyserStati = this.Database.getAnalyserStatiByID(id);
            if (recordKey.equals("Analyser_Starts_Analysis")) {
                //this.Database.update_analyserstatus(id, "powertransmissionsystem", "running");
            } else if (recordKey.equals("Analyser_Finished")) {
                //Extract new Information
                /*
                Map rm  = (HashMap) payload.get("resilient_mounts");
                Map bv = (HashMap) payload.get("bluevision");
                Map tsc = (HashMap) payload.get("torsionally_resilient_coupling");
                Map gbo = (HashMap) payload.get("gearboxoptions");
                Map<String, Double> partAndResult = new HashMap<>();
                if (!(rm == null)){
                    partAndResult.put("fuel_system", (double) rm.values().toArray()[0]);
                }
                if (!(bv == null)){
                    partAndResult.put("exhaust_system", (double) bv.values().toArray()[0]);
                }
                if (!(tsc == null)){
                    partAndResult.put("exhaust_system", (double) tsc.values().toArray()[0]);
                }
                if (!(gbo == null)){
                    partAndResult.put("exhaust_system", (double) gbo.values().toArray()[0]);
                }
                //Save to Database
                this.Database.update_simulationresuls(id, partAndResult);
                this.Database.update_analyserstatus(id, "powertransmissionsystem", "ready");

                 */
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    void start_powertransmissionsystemelements_analyse(Configdata configdata){
        Map powerdata = new HashMap();
        powerdata.put("id",configdata.id);
        powerdata.put("resilient_mounts", configdata.resilient_mounts);
        powerdata.put("bluevision", configdata.bluevision);
        powerdata.put("torsionally_resilient_coupling", configdata.torsionally_resilient_coupling);
        powerdata.put("gearbox_options", configdata.gearbox_options);
        this.kafkaJsontemplate.send(new ProducerRecord<>("powertransmissionsystemelements_analysis", "WF_Starts_Powertransmissionsystemelements_Analysis", powerdata));
    }


    //----------------------------------------------------------------------------------------------------------------
    //Startingsystemelements

    @KafkaListener(topics = "startingsystemelements_analysis", groupId = "Three")
    void fluidelements_analyser_listener(ConsumerRecord<String, Map> record) {
        try {
            String recordKey = record.key();
            if(recordKey.equals("Analyser_In_Error-State"))
            {
                return;
            }

            Map payload = record.value();
            String id = payload.get("id").toString();
            //AnalyserStati analyserStati = this.Database.getAnalyserStatiByID(id);
            if (recordKey.equals("Analyser_Starts_Analysis")) {
                //this.Database.update_analyserstatus(id, "startingelements", "running");
            } else if (recordKey.equals("Analyser_Finished")) {
                //Extract new Information
                /*
                Map aux  = (HashMap) payload.get("auxiliary_PTO");
                Map ems = (HashMap) payload.get("engine_management_system");
                Map<String, Double> partAndResult = new HashMap<>();
                if (!(aux == null)){
                    partAndResult.put("fuel_system", (double) aux.values().toArray()[0]);
                }
                if (!(ems == null)){
                    partAndResult.put("exhaust_system", (double) ems.values().toArray()[0]);
                }
                //Save to Database
                this.Database.update_simulationresuls(id, partAndResult);
                this.Database.update_analyserstatus(id, "startingelements", "ready");

                 */
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    void start_startingsystemelements_analyser(Configdata configdata){
        Map startingdata = new HashMap();
        startingdata.put("id",configdata.id);
        startingdata.put("air_starter", configdata.air_starter);
        startingdata.put("auxiliary_PTO", configdata.auxiliary_PTO);
        startingdata.put("engine_management_system", configdata.engine_management_system);
        this.kafkaJsontemplate.send(new ProducerRecord<>("startingsystemelements_analysis", "WF_Starts_Startingsystemelements_Analysis", startingdata));
    }

    //----------------------------------------------------------------------------------------------------------------

}
