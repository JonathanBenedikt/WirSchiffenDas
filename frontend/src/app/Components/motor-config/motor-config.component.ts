import {Component, EventEmitter, Output} from '@angular/core';
import { FormBuilder } from '@angular/forms';
import {Coolingsystem_config, Fluidsystem_config, Powertransmissionsystem_config, Startingsystem_config} from "../../Interfaces"
import {HTTPBackendCommunicationService} from "../../Services/httpbackend-communication.service";


@Component({
  selector: 'app-motor-config',
  templateUrl: './motor-config.component.html',
  styleUrls: ['./motor-config.component.css']
})
export class MotorConfigComponent {

  @Output() signalDone = new EventEmitter<number>();


  Coolingsystem_config : Coolingsystem_config = {
    oil_system: ['', 'basic', 'performance', 'supreme'],
    cooling_system: ['', 'cool', 'cooler', 'icecold']
  }

  Fluidsystem_config: Fluidsystem_config = {
    fuel_system: ['', 'duplex fuel pre-filter', 'divert valve', 'monitoring fuel leaks'],
    exhaust_system: true
  }

  Powertransmissionsystem_config : Powertransmissionsystem_config = {
  resilient_mounts: true,
  bluevision: true,
  torsionally_resilient_coupling: true,
  gearbox_options: ['reverse reduction gearbox','el. actuated','gearbox mounts','trolling mode for dead-slow propulsion',
    'free auxiliary PTO', 'hydraulic pump drive']
}

  Startingsystem_config : Startingsystem_config = {
  airstarter: true,
  auxiliary_PTO: ['', '140A', '190A', '250A'],
  in_compliance: true
}

  MotorConfigForm = this.fb.group({
    coolingsystem: this.fb.group({
        oil_system: [''],
        cooling_system: ['']
    }),
    fluidsystem: this.fb.group({
      exhaust_system: [''],
      fuel_system: ['']
    }),
    powertransmissionsystem: this.fb.group({
      resilient_mounts: [''],
      bluevision: [''],
      torsionally_resilient_coupling: [''],
      gearbox_options: [['']]
    }),
    startingsystem: this.fb.group({
      air_starter: [''],
      auxiliary_PTO:[''],
      engine_management_system: ['']
    })
  })

  constructor(private bffcommunicator: HTTPBackendCommunicationService, private fb: FormBuilder){
  }

  saveForm(){
    if (this.MotorConfigForm.valid){
      let data = {
        oil_system: this.MotorConfigForm.value.coolingsystem?.oil_system,
        cooling_system: this.MotorConfigForm.value.coolingsystem?.cooling_system,

        fuel_system : this.MotorConfigForm.value.fluidsystem?.fuel_system,
        exhaust_system : this.MotorConfigForm.value.fluidsystem?.exhaust_system,

        resilient_mounts : this.MotorConfigForm.value.powertransmissionsystem?.resilient_mounts,
        bluevision : this.MotorConfigForm.value.powertransmissionsystem?.bluevision,
        torsionally_resilient_coupling : this.MotorConfigForm.value.powertransmissionsystem?.torsionally_resilient_coupling,
        gearbox_options : this.MotorConfigForm.value.powertransmissionsystem?.gearbox_options,

        air_starter : this.MotorConfigForm.value.startingsystem?.air_starter,
        auxiliary_PTO : this.MotorConfigForm.value.startingsystem?.auxiliary_PTO,
        engine_management_system : this.MotorConfigForm.value.startingsystem?.engine_management_system
      };
      this.bffcommunicator.send_Motorconfig(data).subscribe();
    }
  }

  switchState() {
    this.saveForm();
    this.signalDone.emit(1);
  }
}
