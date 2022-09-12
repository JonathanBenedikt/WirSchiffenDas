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

  interval: any;

  ngOnInit() {
    this.interval = setInterval(() => {
      this.refreshStati();
    }, 30000);
  }

  refreshStati(){
    this.backendcommunication.get_all_Analyserstati().subscribe((stati) => {
        for (let i = 0; i < this.AnalyserData.length; i++) {
          this.AnalyserData[i].Status = stati[i];
        }
      }
    );
  }

  displayedColumns: string[] = ['Analyser', 'Status', 'RefreshStatus', 'Retry'];
  AnalyserData = [
    {Analyser : "Coolingsystem", Status : 'Running'},
    {Analyser : "Fluidsystem", Status : "Running"},
    {Analyser : "Powertransmissionsystem", Status : "Running"},
    {Analyser : "Startingsystem", Status : "Running"},
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


   retry(analysername : string){
     let spezRow = this.AnalyserData.find(obj => obj.Analyser === analysername)
     if(spezRow === undefined){
       console.log("No fitting rowname");
       return;
     } else {
       spezRow.Status = "Pending";
       this.backendcommunication.retry(analysername).subscribe(
         (currentStatus) => {

         }
       )
     }
   }



  get_simulationresults() {
    if(this.AnalyserData.every((row) => {row.Status === "finsihed"})){
      this.signalDone.emit(2);
    } else {
    }
  }
}

