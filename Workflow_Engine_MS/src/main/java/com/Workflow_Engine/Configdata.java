package com.Workflow_Engine;

import java.util.Arrays;

public class Configdata {

    String id;

    //Info for Coolingsystem
    String oil_system;

    String cooling_system;

    //Info for Fluidsystem
    String fuel_system;
    Boolean exhaust_system;

    //Info for Powertransmissionsystem
    Boolean resilient_mounts;
    Boolean bluevision;
    Boolean torsionally_resilient_coupling;
    String[] gearbox_options;

    //Info for Startingsystem
    Boolean air_starter;
    String auxiliary_PTO;
    Boolean engine_management_system;

    public String getOil_system() {
        return oil_system;
    }

    public void setOil_system(String oil_system) {
        this.oil_system = oil_system;
    }

    public String getCooling_system() {
        return cooling_system;
    }

    public void setCooling_system(String cooling_system) {
        this.cooling_system = cooling_system;
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
