import { Component } from '@angular/core';
import {BackendService} from "../../backend.service";
import { Cookie } from 'ng2-cookies/ng2-cookies';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent {

	firstname: string;
	lastname: string;
	emailAddress: string;
	cnp: string;

	constructor(private backendService: BackendService){
		let user = Cookie.get('sessionId');
		backendService.getUserInfo(user).subscribe(
			data => { let jsonParsed = JSON.parse(JSON.stringify(data));
				this.firstname = jsonParsed.nume;
				this.lastname = jsonParsed.prenume;
				this.emailAddress = jsonParsed.email;
				this.cnp = jsonParsed.cnp},
			error => console.log('getUserInfo() failed!'));
	}
}
