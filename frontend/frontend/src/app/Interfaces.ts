export interface Motor{
  name : string;
  motorconfig : Motorconfig;
  simulationresults : Simulationresults;
}

export interface Motorconfig{
  Coolingsystem_Config : Coolingsystem_config;
  Fluidsystem_config : Fluidsystem_config;
  Powertransmissionsystem_config: Powertransmissionsystem_config;
  Startingsystem_config : Startingsystem_config;
}
export interface Coolingsystem_config {
  oil_system : string[];
  cooling_system : string[];
}

export interface Fluidsystem_config {
  fuel_system : string[];
  exhaust_system: boolean;
}

export interface Powertransmissionsystem_config {
  resilient_mounts: boolean;
  bluevision: boolean;
  torsionally_resilient_coupling: boolean;
  gearbox_options: string[];
}

export interface Startingsystem_config {
  airstarter: boolean;
  auxiliary_PTO: string[];
  in_compliance: boolean;
}

export interface Simulationresults {
  Property : string;
  Configuration : string;
  Result : number;
}
