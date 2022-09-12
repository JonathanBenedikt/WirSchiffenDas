export interface Motor{
  name : string;
  motorconfig : Motorconfig;
  simulationresults : Simulationresults;
}

export type Motorconfig = Coolingsystem_config & Fluidsystem_config &
    Powertransmissionsystem_config & Startingsystem_config

export type Coolingsystem_config = {
  oil_system : string[];
  cooling_system : string[];
}

export type Fluidsystem_config = {
  fuel_system : string[];
  exhaust_system: boolean;
}

export type Powertransmissionsystem_config = {
  resilient_mounts: boolean;
  bluevision: boolean;
  torsionally_resilient_coupling: boolean;
  gearbox_options: string[];
}

export type Startingsystem_config = {
  airstarter: boolean;
  auxiliary_PTO: string[];
  in_compliance: boolean;
}

export interface Simulationresults {
  name : string;
  choosenOption : string;
  simulationresult : number;
}
