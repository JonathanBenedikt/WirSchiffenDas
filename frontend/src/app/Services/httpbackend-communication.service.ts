import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {Simulationresults} from '../Interfaces';

//     'Access-Control-Allow-Origin': "*", didn't work

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class HTTPBackendCommunicationService {
  //baseurl: string = 'http://localhost:8582';

  constructor(private http: HttpClient) {
  }

  send_Motorconfig(motorconfig: Object) {
    return this.http.post<Object>('/startAnalysis', motorconfig, httpOptions).pipe(
        catchError(this.handleError)
      );
  }

  get_Analyserstatus(analysername: string): Observable<string> {
    let path = "";
    if (analysername == "Coolingsystem") {
      path = "getCoolingsystemStatus"
    } else if (analysername == "Fluidsystem") {
      path = "getFluidsystemStatus"
    } else if (analysername == "Powertransmissionsystem") {
      path = "getPowertransmissionsystemStatus"
    } else if (analysername == "Startingsystem") {
      path = "getStartingsystemStatus"
    } else {
      console.log(analysername + " is not known")
    }
    return this.http.get<string>("/"+path);
  }

  retry(analysername: string) {
    let path = "";
    if (analysername == "Coolingsystem") {
      path = "retryCoolingsystem"
    } else if (analysername == "Fluidsystem") {
      path = "retryFluidsystem"
    } else if (analysername == "Powertransmissionsystem") {
      path = "retryPowersystem"
    } else if (analysername == "Startingsystem") {
      path = "retryStartingsystem"
    } else {
      console.log(analysername + " is not known")
    }
    return this.http.get<null>("/"+path);
  }

  get_all_Analyserstati() {
    return this.http.get<string[]>('/getAllStati').pipe(
        catchError(this.handleError)
    );
  }

  get_Simulationresults() {
    return this.http.get<Simulationresults[]>('/getSimulationresults').pipe(
      catchError(this.handleError)
    );
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
