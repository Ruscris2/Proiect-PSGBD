import { ModuleWithProviders } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {LoginComponent} from "./login/login.component";

export const router: Routes = [
	{ path: '', component: DashboardComponent },
	{ path: 'login', component: LoginComponent }
];

export const routes : ModuleWithProviders = RouterModule.forRoot(router);