import { Component } from '@angular/core';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

	username: string = '';
	password: string = '';

	onLoginClick(){
		alert(this.username + ' ' + this.password);
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
