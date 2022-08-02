package com.Workflow_Engine;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


//Allows communication between the internal Workflow, the Analyse-MS and the angular frontend
@RestController
public class WorkflowController {
    @Autowired
    private Workflow workflow;
    @Autowired
    KafkaTemplate<String, Map> kafkaJsontemplate;
    String analyse_topic_name = "analyse";

    @KafkaListener(topics = "analyse", groupId = "One")
    void analyselistener(ConsumerRecord<String, Map> record){
        try {
            String recordKey = record.key().toString();
            if (recordKey.equals("Analyser_Starts_Analysis")) {
                return; //Nothing to do with it
            } else if (recordKey.equals("Analyser_Finished")) {
                //integrate data into the workflow
                //persist it(?)
            }
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    @KafkaListener(topics = "wf_bff", groupId = "One") //group doesn't really matter atm
    void wfbfflistener(ConsumerRecord<String, Map> record){
        try {
            String recordKey = record.key();
            if (recordKey.equals("BFF_Passes_Configdata")) {
                // unpack the json data from send by the bff
                // save config
                //send kafkatemplates into the respective channels (e.g. fluid, etc.)
            } else {
                System.out.println(record.key());
            }
        }catch (Exception ex)
        {
            System.out.println(ex);
        }
    }


    //Just to check if kafka works locally
    @PostMapping("/sendtestJson")
    public void sendtestJson(){
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


    @PostMapping("/startworkflow")
    public void startworkflow(String data){
        workflow.start_workflow();
    }

    @GetMapping("/workflowdata")
    public ResponseEntity<Workflow> read() {
        return ResponseEntity.ok(workflow);
    }

}
