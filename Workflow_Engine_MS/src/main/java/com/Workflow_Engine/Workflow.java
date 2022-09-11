package com.Workflow_Engine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Workflow {
    String ID;
    Configdata configdata;
    Simulationresults simulationresults;

    Map<String, String> analyserStati;

    public Workflow(){

    }

    public Workflow(String ID, Configdata configdata, Simulationresults simulationresults) {
        this.ID = ID;
        this.configdata = configdata;
        this.simulationresults = simulationresults;
        this.analyserStati = new HashMap<>();
        this.analyserStati.put("Coolingsystem", "not running");
        this.analyserStati.put("Fluidsystem", "not running");
        this.analyserStati.put("Powertransmissionelementsystem", "not running");
        this.analyserStati.put("Startingsystem", "not running");
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Configdata getConfigdata() {
        return configdata;
    }

    public void setConfigdata(Configdata configdata) {
        this.configdata = configdata;
    }

    public Simulationresults getSimulationresults() {
        return simulationresults;
    }

    public void setSimulationresults(Simulationresults simulationresults) {
        this.simulationresults = simulationresults;
    }

    boolean isDone(){
        return this.analyserStati.values().stream().allMatch(status -> status.equals("ready"));
    }
}
