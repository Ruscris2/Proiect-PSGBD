import 'rxjs/Rx';
import { Injectable } from '@angular/core';
import { Http, Headers } from '@angular/http';

@Injectable()
export class BackendService {
	constructor(private http: Http) {}

	login(username: string, password: string){
		var headers = new Headers();
		var json = JSON.stringify({ username: username, password: password});

		headers.append('Content-Type', 'application/json');
		return this.http.post('http://localhost:6969/login', json, { headers: headers }).map(res => res.json());
	}
}