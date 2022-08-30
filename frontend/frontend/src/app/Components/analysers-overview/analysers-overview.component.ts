import {Component, Input, OnInit} from '@angular/core';
import {HTTPBackendCommunicationService} from '../../Services/httpbackend-communication.service'

export interface PeriodicElement {
  Analyser: string;
  Status: string;
}

const ELEMENT_DATA: PeriodicElement[] = [
  {Analyser : "Coolingsystem", Status : "Running"},
  {Analyser : "Fluidsystem", Status : "Running"},
  {Analyser : "Powertransmissionsystem", Status : "Running"},
  {Analyser : "Startingsystem", Status : "Running"},
];


@Component({
  selector: 'app-analysers-overview',
  templateUrl: './analysers-overview.component.html',
  styleUrls: ['./analysers-overview.component.css']
})
export class AnalysersOverviewComponent implements OnInit {


  displayedColumns: string[] = ['Analyser', 'Status', 'StopStart', 'Restart'];
  AnalyserData = ELEMENT_DATA
  //Todo Drehende Kreise beim Warten auf den Service
  //Eine Tabelle Service Name, Status + Kreis der dreht "laden", Start/Stop Button,
  constructor(private backendcommunication : HTTPBackendCommunicationService) { }

  ngOnInit(): void {
  }

  restart_analyser(analysername : string){
    if(analysername === 'Fluidsystem'){
      this.backendcommunication.restart_Fluidsystemanalyser().subscribe();
    }
    else if (analysername === 'Coolingsystem') {
      this.backendcommunication.restart_Coolingsystemanalyser().subscribe();
    }
    else if (analysername === 'Powertransmissionsystem') {
      this.backendcommunication.restart_Powertransmissionsystemanalyser().subscribe();

    }
    else if (analysername === 'Startingsystem') {
      this.backendcommunication.restart_Startingsystemanalyser().subscribe();
    }
  }
  @Input()
  toggle_analyser(analysername : string){
    if(analysername === 'Fluidsystem'){
    }
    else if (analysername === 'Coolingsystem') {
    }
    else if (analysername === 'Powertransmissionsystem') {
    }
    else if (analysername === 'Startingsystem') {
    }
  }



}
