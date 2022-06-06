package com.example.Workflow_Engine_MS;

import org.springframework.stereotype.Repository;

@Repository
public class Diesel implements Motor{
    public boolean air_starter = false;
    public String auxiliary_PTO;
    public String oil_system;
    public String fuel_system;
    public String cooling_system;
    public String gearbox_options;
    public boolean exhaust_system = false;
    public boolean resilient_mounts = false;
    public boolean in_compliance = false;
    public boolean blueVision = false;
    public boolean torsionally_resilient_coupling = false;
}
