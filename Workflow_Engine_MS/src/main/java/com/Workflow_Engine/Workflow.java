package com.Workflow_Engine;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Workflow {
    public int ID;
    public int State; //Which state the workflow is in
    private Motor motor;

    public void start_workflow(){
        this.motor = new Diesel();
    };
    public void start_analyser(ArrayList<String> arg){
        //start
        start_fluidanalyser();
        //TODO start other microservices

    }

    private void start_fluidanalyser(){
        ArrayList fluiddata = new ArrayList<String>();
        fluiddata.add(this.motor.oil_system);
        fluiddata.add(this.motor.fuel_system);
        fluiddata.add(this.motor.cooling_system);
        //start fluidanalyser (Eureka)

    }

    public void send_workflowdata(){
        return;
    }


}
