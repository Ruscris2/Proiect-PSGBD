import { Component } from '@angular/core';
import { Cookie } from 'ng2-cookies/ng2-cookies';
import {Router} from "@angular/router";
import {BackendService} from "../backend.service";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {

	firstname: string;
	lastname: string;

	constructor(private router: Router, private backendService: BackendService) {
		let user = Cookie.get('sessionId');
		if(user == null){
			router.navigateByUrl('/login');
		}

		backendService.getUserInfo(user).subscribe(
			data => { let jsonParsed = JSON.parse(JSON.stringify(data));
						this.firstname = jsonParsed.nume;
						this.lastname = jsonParsed.prenume},
			error => console.log('getUserInfo() failed!'));
	}

	onLogoutClick(){
		Cookie.delete('sessionId');
		this.router.navigateByUrl('/login');
	}
}
