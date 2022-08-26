import { Component, Input, OnInit } from '@angular/core';
import {WFCommunicationService} from "../../Services/wf-communication.service";
import { FormBuilder } from '@angular/forms';
import {Motor_config, Coolingsystem_config, Fluidsystem_config, Powertransmissionsystem_config, Startingsystem_config} from "../../Interfaces"


@Component({
  selector: 'app-motor-config',
  templateUrl: './motor-config.component.html',
  styleUrls: ['./motor-config.component.css']
})
export class MotorConfigComponent implements OnInit {
  @Input() jsonFormData: any;

  Coolingsystem_config : Coolingsystem_config = {
    oil_system: ['basic', 'performance', 'supreme'],
    cooling_system: ['cool', 'cooler', 'icecold']
  }

  Fluidsystem_config: Fluidsystem_config = {
    fuel_system: ['duplex fuel pre-filter', 'divert valve', 'monitoring fuel leaks'],
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
  auxiliary_PTO: ['140A', '190A', '250A'],
  in_compliance: true
}

  MotorConfigForm = this.fb.group({
    coolingsystemform: this.fb.group({
        oil_system: [''],
        cooling_system: ['']

    }
    ),
    fluidsystemform: this.fb.group({
      exhaust_system: [''],
      fuel_system: ['']
    }),
    powertransmissionsystemform: this.fb.group({
      resilient_mounts: [''],
      bluevision: [''],
      torsionally_resilient_coupling: [''],
      gearbox_options: ['']
    }),
    startingsystemform: this.fb.group({
      air_starter: [''],
      auxiliary_PTO:[''],
      engine_management_system: ['']
    })
  })

  constructor(private WFCommunicationService: WFCommunicationService, private fb: FormBuilder){
  }

  ngOnInit() {}

  saveForm(){
    console.log('Form data is ', this.MotorConfigForm.value);
  }

}
