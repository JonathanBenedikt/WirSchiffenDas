import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {HTTPBackendCommunicationService} from "../../Services/httpbackend-communication.service";
import {Simulationresults} from "../../Interfaces";
import {Observable} from "rxjs";
import {getMatIconFailedToSanitizeLiteralError} from "@angular/material/icon";

@Component({
  selector: 'app-motor-finished',
  templateUrl: './motor-finished.component.html',
  styleUrls: ['./motor-finished.component.css']
})
export class MotorFinishedComponent implements OnInit {

  @Output() signalDone = new EventEmitter<number>();

  columns = ["Property", "Configuration", "Result"]
  datasource : Simulationresults[] = [
    {name : "", choosenOption : "", simulationresult : 0.0}
  ];

  constructor(private backendcommunicator : HTTPBackendCommunicationService) { }

  ngOnInit(): void {
    this.get_simulationdata()
  }

  get_simulationdata(){
    this.backendcommunicator.get_Simulationresults().subscribe((simdata) => {
      this.datasource = simdata;
    });
  }

  buy() {

  }

  restart_process() {
    this.signalDone.emit(0);
  }

}
