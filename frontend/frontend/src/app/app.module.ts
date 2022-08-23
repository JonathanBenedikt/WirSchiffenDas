import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { MotorConfigComponent } from './Components/motor-config/motor-config.component';
import { AnalysersOverviewComponent } from './Components/analysers-overview/analysers-overview.component';
import { MotorFinishedComponent } from './Components/motor-finished/motor-finished.component';
import {HttpClientModule} from "@angular/common/http";

@NgModule({
  declarations: [
    AppComponent,
    MotorConfigComponent,
    AnalysersOverviewComponent,
    MotorFinishedComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
