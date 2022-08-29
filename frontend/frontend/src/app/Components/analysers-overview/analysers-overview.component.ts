import { Component, OnInit } from '@angular/core';
import {WFCommunicationService} from "../../Services/wf-communication.service";

@Component({
  selector: 'app-analysers-overview',
  templateUrl: './analysers-overview.component.html',
  styleUrls: ['./analysers-overview.component.css']
})
export class AnalysersOverviewComponent implements OnInit {

  //Todo Drehende Kreise beim Warten auf den Service
  //Eine Tabelle Service Name, Status + Kreis der dreht "laden", Start/Stop Button,
  constructor(private bffcommunication : WFCommunicationService) { }

  ngOnInit(): void {
  }

}
