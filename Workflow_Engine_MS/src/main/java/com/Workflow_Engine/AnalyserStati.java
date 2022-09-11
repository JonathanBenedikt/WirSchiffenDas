package com.Workflow_Engine;

public class AnalyserStati {
    String id;
    String coolingsystem;
    String fluidsystem;
    String powertransmissionsystem;
    String startingelements;

    public AnalyserStati(String id) {
        this.id = id;
        this.coolingsystem = "not running";
        this.fluidsystem = "not running";
        this.powertransmissionsystem = "not running";
        this.startingelements = "not running";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoolingsystem() {
        return coolingsystem;
    }

    public void setCoolingsystem(String coolingsystem) {
        this.coolingsystem = coolingsystem;
    }

    public String getFluidsystem() {
        return fluidsystem;
    }

    public void setFluidsystem(String fluidsystem) {
        this.fluidsystem = fluidsystem;
    }

    public String getPowertransmissionsystem() {
        return powertransmissionsystem;
    }

    public void setPowertransmissionsystem(String powertransmissionsystem) {
        this.powertransmissionsystem = powertransmissionsystem;
    }

    public String getStartingelements() {
        return startingelements;
    }

    public void setStartingelements(String startingelements) {
        this.startingelements = startingelements;
    }
}
