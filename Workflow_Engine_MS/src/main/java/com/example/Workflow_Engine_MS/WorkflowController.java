package com.example.Workflow_Engine_MS;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.converters.Auto;
import com.netflix.discovery.shared.Application;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//Allows communication between the internal Workflow, the Analyse-MS and the angular frontend


//erstmal super simpel damit rumspielen bis ich's gut genug verstehe und das ding richtig code
@RestController
@EnableEurekaClient
public class WorkflowController {
    @Autowired
    private Workflow workflow;

    @Autowired
    private EurekaClient eurekaClient;

    @GetMapping("/")
    public String callProvider() {
        Application application = eurekaClient.getApplication("Fluid_Analyser");
        if(application != null) {
            InstanceInfo instanceInfo = application.getInstances().get(0);
            String url = "http://" + instanceInfo.getIPAddr() + ":" + instanceInfo.getPort() + "/information";
            return "";
            //Todo Absenden von anfrage
        } else {
            return "Provider nicht verfügbar";
        }
    }

    //get data from angular (I don't know how)
    @PostMapping("/startworkflow")
    public void startworkflow(String data){
        workflow.start_workflow();
    }

    @GetMapping("/workflowdata")
    public ResponseEntity<Workflow> read() {
        return ResponseEntity.ok(workflow);
    }

}
