import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {Motorconfig, Simulationresults} from '../Interfaces'
import {FormGroup} from "@angular/forms";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
    Authorization: 'my-auth-token'
  })
};

@Injectable({
  providedIn: 'root'
})
export class HTTPBackendCommunicationService {
  baseurl : string = 'http://localhost:3000';

  constructor(private http : HttpClient) {
  }

  send_Motorconfig(motorconfig: Object){
    return this.http.post<Object>(this.baseurl+'/motorconfig', motorconfig, httpOptions)
      .pipe(
        catchError(this.handleError)
      );
  }

  get_Simulationresults(){
    return this.http.get<Simulationresults>(this.baseurl+'/simulation_results')
  }

  start_Analyser(analysername : String){
    return this.http.post(this.baseurl+'/start_Analyser', {name : analysername})
  }

  stop_Analyser(analysername : String){
    return this.http.post(this.baseurl+'/stop_Analyser', {name : analysername})
  }

  restart_Coolingsystemanalyser(){
    return this.http.get(this.baseurl+'/restart_Coolingsystemanalyser');
  }

  restart_Fluidsystemanalyser(){
    return this.http.get(this.baseurl+'/restart_Fluidsystemanalyser');
  }

  restart_Startingsystemanalyser(){
    return this.http.get(this.baseurl+'/restart_Startingsystemanalyser');
  }

  restart_Powertransmissionsystemanalyser(){
    return this.http.get(this.baseurl+'/restart_Powertransmissionsystemanalyser');
  }

  private handleError(error: HttpErrorResponse) {
    if (error.status === 0) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong.
      console.error(
        `Backend returned code ${error.status}, body was: `, error.error);
    }
    // Return an observable with a user-facing error message.
    return throwError(() => new Error('Something bad happened; please try again later.'));
  }

}
