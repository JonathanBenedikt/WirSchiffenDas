import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {HTTPBackendCommunicationService} from '../../Services/httpbackend-communication.service';
import {interval, Subscription} from "rxjs";

@Component({
  selector: 'app-analysers-overview',
  templateUrl: './analysers-overview.component.html',
  styleUrls: ['./analysers-overview.component.css']
})
export class AnalysersOverviewComponent {
  @Output() signalDone = new EventEmitter<number>();

  period$ = interval(30000).subscribe();


  displayedColumns: string[] = ['Analyser', 'Status', 'RefreshStatus', 'StopStart'];
  AnalyserData = [
    {Analyser : "Coolingsystem", Status : 'Running', Checked : true},
    {Analyser : "Fluidsystem", Status : "Running", Checked : true},
    {Analyser : "Powertransmissionsystem", Status : "Running", Checked : false},
    {Analyser : "Startingsystem", Status : "Running", Checked : true},
  ];

  constructor(private backendcommunication : HTTPBackendCommunicationService) {}


   refresh_status(analysername : string){
     let spezRow = this.AnalyserData.find(obj => obj.Analyser === analysername)
     if(spezRow === undefined){
       console.log("No fitting rowname");
       return;
     } else {
       spezRow.Status = "Pending";
       this.backendcommunication.get_Analyserstatus(analysername).subscribe(
         (currentStatus) => {
           spezRow !== undefined ? spezRow.Status = currentStatus : undefined;
         }
       )
     }
   }


  toggle_analyser(analysername : string){
    let row = this.AnalyserData.find(row => row.Analyser === analysername);
    if(row === undefined){
      throw new Error("No fitting Rowname")
    } else {
      if(row.Checked){
        this.backendcommunication.start_Analyser(analysername).subscribe();
      } else {
        this.backendcommunication.stop_Analyser(analysername).subscribe();
      }
    }
  }

  get_simulationresults() {
    if(this.AnalyserData.every((row) => {row.Status === "finsihed"})){
      this.signalDone.emit(2);
    } else
      this.signalDone.emit(2); //Todo loeschen wenn getestet
    //todo popup das noch nicht ready
    }
}

