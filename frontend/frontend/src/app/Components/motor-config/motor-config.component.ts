import { Component, Input, OnInit } from '@angular/core';
import {WFCommunicationService} from "../../Services/wf-communication.service";

@Component({
  selector: 'app-motor-config',
  templateUrl: './motor-config.component.html',
  styleUrls: ['./motor-config.component.css']
})
export class MotorConfigComponent implements OnInit {
  @Input() jsonFormData: any;

  constructor(private WFCommunicationService: WFCommunicationService) {

  }

  ngOnInit() {}

}
