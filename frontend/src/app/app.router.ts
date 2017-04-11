import { ModuleWithProviders } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {LoginComponent} from "./login/login.component";
import {WorkpanelComponent} from "./dashboard/workpanel/workpanel.component";
import {ProfileComponent} from "./dashboard/profile/profile.component";

export const router: Routes = [
	{ path: '', component: DashboardComponent, children: [
		{ path: '', component: WorkpanelComponent },
		{ path: 'profile', component: ProfileComponent }
	] },
	{ path: 'login', component: LoginComponent }
];

export const routes : ModuleWithProviders = RouterModule.forRoot(router);