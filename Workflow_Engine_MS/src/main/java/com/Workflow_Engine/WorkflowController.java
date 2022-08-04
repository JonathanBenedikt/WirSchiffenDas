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

    @KafkaListener(topics = "wf_bff", groupId = "One") //group doesn't really matter atm
    void wfbfflistener(ConsumerRecord<String, Map> record){
        try {
            String recordKey = record.key();
            if (recordKey.equals("BFF_Passes_Configdata")) {
                save_bff_configdata(record.value());

                //send kafkatemplates into the respective channels (e.g. fluid, etc.)
            } else {
                System.out.println(record.key());
            }
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
    }
    private void save_bff_configdata(Map data){ //TODO write as type converter (?)
        this.motor = new Diesel(data);
        motors.save(motor);
    }

    //Starts Analysis (send out Kafkatemplates into respective channels (could I do this with a pattern easier to extend)
    public void start_analysis(String orderID){
        Optional<Motor> opt = motors.findById(orderID);
        if (opt.isEmpty()){
            System.out.println("ID not Found");
        } else {
            Motor m = opt.get();
            start_fluidanalyser(m); //Start all analysers
        }
    }

    void start_fluidanalyser(Motor m){
        // cut up the data
        Map fluidconfigdata = new HashMap();
        fluidconfigdata.put("exhaust_system", m.exhaust_system);
        fluidconfigdata.put("fuel_system", m.fuel_system);
        this.kafkaJsontemplate.send(new ProducerRecord<>("analyse", "WF_Starts_FluidAnalysis", fluidconfigdata));
    }

    @KafkaListener(topics = "analyse", groupId = "One")
    void analyselistener(ConsumerRecord<String, Map> record) {
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
