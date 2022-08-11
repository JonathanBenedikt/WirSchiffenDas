package com.Workflow_Engine;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public abstract class Motor {
    @Id
    public int ID = 0;
    public String name = null;
    public boolean air_starter = false;
    public String auxiliary_PTO = "";
    public String oil_system = null;
    public String fuel_system = "";
    public String cooling_system = "";
    public boolean exhaust_system = false;
    public boolean resilient_mounts = false;
    public boolean in_compliance = false;
    public boolean blueVision = false;
    public boolean torsionally_resilient_coupling = false;
    public String gearbox_options= "";

}
