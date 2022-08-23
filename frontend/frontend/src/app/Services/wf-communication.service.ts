import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class WFCommunicationService {

  constructor(private http: HttpClient) {
    //Todo Use Kafka
  }
}
