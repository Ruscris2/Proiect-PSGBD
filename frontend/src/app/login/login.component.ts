import { Component } from '@angular/core';
import { BackendService } from "../backend.service";
import { Cookie } from 'ng2-cookies/ng2-cookies';
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

	username: string = '';
	password: string = '';

	constructor(private backendService: BackendService, private router: Router) {
		if(Cookie.get('sessionId') != null){
			this.router.navigateByUrl('');
		}
	}

	onLoginClick(){
		if(this.username == '' || this.password == ''){
			alert('Ambele campuri trebuie sa fie completate!');
			return;
		}

		this.backendService.login(this.username, this.password).subscribe(
			data => {let jsonParsed = JSON.parse(JSON.stringify(data));
						if(jsonParsed.authorized) {
							Cookie.set('sessionId', this.username);
							this.router.navigateByUrl('');
						}
						else
							alert('Username sau parola invalida!')},
			error => console.log('Login failed!'),
			() => console.log('Logged in!')
		);
	}

	onInputChange(event: any){
		if(event.target.id == 'userTextBox'){
			this.username = event.target.value;
		}
		else if(event.target.id == 'passTextBox'){
			this.password = event.target.value;
		}
	}
}
