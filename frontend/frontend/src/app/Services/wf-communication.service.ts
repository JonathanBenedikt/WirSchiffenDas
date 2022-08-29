// noinspection LanguageDetectionInspection

import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Motor_config} from "../Interfaces"
import {Observable} from "rxjs";
import {webSocket, WebSocketSubject} from "rxjs/webSocket";
const subject = webSocket("ws://localhost:8081");


//Im Tutorial senden die das sofort an die Adresse und holen die daten per GET call an die selbe
//Wir wollen aber, dass die Daten vom Backend an den Client Gepushed werden (brauchen wir dafür vielleicht den Websocket kram
//um mit Kafka zu reden. Denke wir müssen Von Frontend an BFF(serverside) der wiederrum mit anderen über Kafka kommuniziert (geht nicht direkt, macht auch sinn, da das ja Interne Kommunikation ist und Angular irgendwo aufm Client ausgeführt wird)
//Websocket ist ein Protokol für duplexe Kommunikation (kommunikation in beide Richtungen, server kann angular ebenfalls was sagen, ermöglicht)
  //https://javascript-conference.com/blog/real-time-in-angular-a-journey-into-websocket-and-rxjs/
  //https://medium.com/swlh/angular-spring-boot-kafka-how-to-stream-realtime-data-the-reactive-way-510a0f1e5881
  //angular kafka communication

  //Alles über Websocket an BFF senden (wie? google, hat was mit RxJS und Websocket zutun)
  //Senden von Configdata
  //receiven von Simulationdatas
  //Stop/Starten von Simulation
  //restarten der Simulationen
  //Muss dann über BFF weitergeleitet werden

  //Aufteilung von Traffic auf WF oder Analyser geschieht dann wahrscheinlich im BFF


import { environment } from '../../environments/environment';
export const WS_ENDPOINT = environment.wsEndpoint;

@Injectable({
  providedIn: 'root'
})
export class WFCommunicationService {

}
