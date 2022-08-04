package com.Workflow_Engine;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;


public class Diesel extends Motor{
    public Diesel(){

    }
    public Diesel(Map data){
        this.air_starter = (boolean) data.get("air_starter");
        this.name = (String) data.get("name");
        this.auxiliary_PTO = (String) data.get("auxiliary_PTO");
        this.oil_system = (String) data.get("oil_system");
        this.fuel_system = (String) data.get("fuel_system");
        this.cooling_system = (String) data.get("cooling_system");
        this.gearbox_options = (String) data.get("gearbox_options");
        this.exhaust_system = (boolean) data.get("exhaust_system");
        this.resilient_mounts = (boolean) data.get("resilient_mounts");
        this.in_compliance = (boolean) data.get("in_compliance");
        this.blueVision = (boolean) data.get("blueVision");
        this.torsionally_resilient_coupling = (boolean) data.get("torsionally_resilient_coupling");
    }
}
