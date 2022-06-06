package com.example.Workflow_Engine_MS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


//Allows communication between the internal Workflow, the Analyse-MS and the angular frontend
@RestController
public class WorkflowController {
    @Autowired
    private Workflow workflow;

    @PostMapping("/init")
    public void initializeWorkflow() {
        return;
    }

    @GetMapping("/getWorkflow")
    public Workflow getWorkflow() {
        return this.workflow;
    }
}
