package com.Workflow_Engine;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
            if (recordKey.equals("BFF_Passes_Configdata")) {
                save_bff_configdata(record.value());
            } else if (recordKey.equals("BFF_Ready_For_Analysisresults")) {
                //how to get the ID here?
                send_analysisresults("");
            } else {
                System.out.println(record.key());
            }
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
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
    public void start_analysers(@RequestParam(value = "ID", defaultValue = "") String ID){
        start_coolingsystemelements_analyser(ID);
        start_fluidsystemelements_analyser(ID);
        start_powertransmissionsystemelements_analyse(ID);
        start_startingsystemelements_analyser(ID);
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

    @KafkaListener(topics = "coolingsystemelements_analysis", groupId = "One")
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

    void start_coolingsystemelements_analyser(String ID){
        Motor m = findmotorbyID(ID);
        // cut up the data (
        Map configdata = new HashMap();
        configdata.put("oil_system", m.exhaust_system);
        configdata.put("cooling_system", m.fuel_system);
        this.kafkaJsontemplate.send(new ProducerRecord<>("coolingsystemelements_analysis", "WF_Starts_Coolingsystemelements_Analysis", configdata));
    }



    private void restart_start_coolingsystemelements_analyser(String ID) {
    }


    //----------------------------------------------------------------------------------------------------------------
    //Fluidsystemelements

    @KafkaListener(topics = "fluidsystemelements_analysis", groupId = "One")
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

    void start_fluidsystemelements_analyser(String ID){
        Motor m = findmotorbyID(ID);
        // cut up the data
        Map configdata = new HashMap();
        configdata.put("exhaust_system", m.exhaust_system);
        configdata.put("fuel_system", m.fuel_system);
        this.kafkaJsontemplate.send(new ProducerRecord<>("fluidsystemelements_analysis", "WF_Starts_Fluidsystemelements_Analysis", configdata));
    }

    private void restart_fluidelements_analyser(String ID) {
    }

    //----------------------------------------------------------------------------------------------------------------
    //Powertransmissionelements
    @KafkaListener(topics = "powertransmissionsystemelements_analysis", groupId = "One")
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

    void start_powertransmissionsystemelements_analyse(String ID){
        Motor m = findmotorbyID(ID);
        // cut up the data
        Map configdata = new HashMap();
        configdata.put("mounting_system", m.in_compliance);
        configdata.put("monitoring_control_system", m.blueVision);
        configdata.put("powertransmission", m.torsionally_resilient_coupling);
        configdata.put("gearbox_options", m.gearbox_options);
        this.kafkaJsontemplate.send(new ProducerRecord<>("powertransmissionsystemelements_analysis", "WF_Starts_Powertransmissionsystemelements_Analysis", configdata));
    }

    private void restart_powertransmissionelements_analyser(String ID) {
    }

    //----------------------------------------------------------------------------------------------------------------
    //Startingsystemelements

    @KafkaListener(topics = "startingsystemelements_analysis", groupId = "One")
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

    void start_startingsystemelements_analyser(String ID){
        Motor m = findmotorbyID(ID);
        // cut up the data
        Map configData = new HashMap();
        configData.put("air_starter", m.exhaust_system);
        configData.put("auxiliary_PTO", m.fuel_system);
        configData.put("engine_management_system", m.in_compliance);
        this.kafkaJsontemplate.send(new ProducerRecord<>("startingsystemelements_analysis", "WF_Starts_Startingsystemelements_Analysis", configData));
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

}
