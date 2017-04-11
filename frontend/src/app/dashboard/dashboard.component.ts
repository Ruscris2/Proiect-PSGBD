import { Component } from '@angular/core';
import { Cookie } from 'ng2-cookies/ng2-cookies';
import {Router} from "@angular/router";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {

	constructor(private router: Router) {
		let user = Cookie.get('sessionId');
		if(user == null){
			router.navigateByUrl('/login');
		}
	}

	onLogoutClick(){
		Cookie.delete('sessionId');
		this.router.navigateByUrl('/login');
	}
}
