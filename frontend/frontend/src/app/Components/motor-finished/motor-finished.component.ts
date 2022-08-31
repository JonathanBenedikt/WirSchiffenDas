import { Component, OnInit } from '@angular/core';
import {HTTPBackendCommunicationService} from "../../Services/httpbackend-communication.service";
import {Simulationresults} from "../../Interfaces";

@Component({
  selector: 'app-motor-finished',
  templateUrl: './motor-finished.component.html',
  styleUrls: ['./motor-finished.component.css']
})
export class MotorFinishedComponent implements OnInit {

  columns = ["Property", "Configuration", "Result"]
  datasource : Simulationresults[] = [
    {Property : "", Configuration : "", Result : 0}
  ];

  constructor(private backendcommunicator : HTTPBackendCommunicationService) { }

  ngOnInit(): void {
  }

  get_simulationdata(){
    this.backendcommunicator.get_Simulationresults().subscribe((simdata) => {
      this.datasource = simdata;
    });
  }

  restart_process() {
    //todo wieder von Anfang starten (an app übergeben)
  }

  buy() {
    //Pop-Ups that feature is not implemented yet
  }
}
