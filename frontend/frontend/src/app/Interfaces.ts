export interface MotorConfig{
  ID : number;
  name : string;
  air_starter : boolean;
  auxiliary_PTO : string;
  oil_system : string;
  fuel_system : string;
  cooling_system : string;
  exhaust_system: boolean;
  resilient_mounts : boolean;
  in_compliance : boolean;
  blueVision : boolean;
  torsionally_resilient_coupling : boolean;
  gearbox_options : string;
}
