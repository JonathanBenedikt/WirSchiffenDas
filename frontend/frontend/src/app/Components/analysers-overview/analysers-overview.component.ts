import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {HTTPBackendCommunicationService} from '../../Services/httpbackend-communication.service';

@Component({
  selector: 'app-analysers-overview',
  templateUrl: './analysers-overview.component.html',
  styleUrls: ['./analysers-overview.component.css']
})
export class AnalysersOverviewComponent implements OnInit {

  @Output() signalDone = new EventEmitter<number>();

  displayedColumns: string[] = ['Analyser', 'Status', 'RefreshStatus', 'StopStart', 'Restart'];
  AnalyserData = [
    {Analyser : "Coolingsystem", Status : 'Running', Checked : true},
    {Analyser : "Fluidsystem", Status : "Running", Checked : true},
    {Analyser : "Powertransmissionsystem", Status : "Running", Checked : false},
    {Analyser : "Startingsystem", Status : "Running", Checked : true},
  ];

  constructor(private backendcommunication : HTTPBackendCommunicationService) { }

  ngOnInit(): void {
    //todo kann hier dann die Analysedaten ziehen!
  }

  refresh_status(analysername : string){
    this.backendcommunication.get_Analyserstatus(analysername).subscribe(
      (currentStatus) => {
        let spezRow = this.AnalyserData.find((obj) => {obj.Analyser === analysername})
        if(spezRow === undefined){
          throw new Error("No fitting rowname");
        } else {
          spezRow.Status = currentStatus;
        }
      }
    )
  }

  restart_analyser(analysername : string){
    this.backendcommunication.restart(analysername).subscribe();
  }

  toggle_analyser(analysername : string){
    let row = this.AnalyserData.find((row) => {
      row.Analyser === analysername
    });
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

