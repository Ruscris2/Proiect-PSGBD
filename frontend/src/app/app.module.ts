import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { BackendService } from "./backend.service";
import { DashboardComponent } from './dashboard/dashboard.component';
import { routes } from "./app.router";

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent
  ],
	imports: [
		BrowserModule,
		FormsModule,
		HttpModule,
		routes
  ],
  providers: [ BackendService ],
  bootstrap: [AppComponent]
})
export class AppModule { }
