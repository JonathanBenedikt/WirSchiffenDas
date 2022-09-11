package com.Workflow_Engine;

public class Simulationresults {

    public Simulationresults(String id){
        this.id = id;
        double oil_system = 0.0;
        double cooling_system = 0.0;
        double fuel_system = 0.0;
        double exhaust_system = 0.0;
        double resilient_mounts = 0.0;
        double bluevision = 0.0;
        double torsionally_resilient_coupling = 0.0;
        double gearbox_options = 0.0;

        //Info for Startingsystem
        double air_starter = 0.0;
        double auxiliary_PTO = 0.0;
        double engine_management_system = 0.0;

    }

    String id;
    //Info for Coolingsystem
    Float oil_system;

    Float cooling_system;

    //Info for Fluidsystem
    Float fuel_system;
    Float exhaust_system;

    //Info for Powertransmissionsystem
    Float resilient_mounts;
    Float bluevision;
    Float torsionally_resilient_coupling;
    Float gearbox_options;

    //Info for Startingsystem
    Float air_starter;
    Float auxiliary_PTO;
    Float engine_management_system;
}
