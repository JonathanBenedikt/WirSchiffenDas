import { Component } from '@angular/core';

enum states {
  specifing_motorconfig,
  waiting_for_analyserresults,
  viewing_results
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'frontend';
  currentState: string = states[0];
  states = states;

  changeState(state: number){
    this.currentState = this.states[state];
  }

}


