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

	getUserInfo(username: string){
		var headers = new Headers();
		var json = JSON.stringify({ username: username});

		headers.append('Content-Type', 'application/json');
		return this.http.post('http://localhost:6969/userinfo', json, { headers: headers }).map(res => res.json());
	}

	addClient(firstname: string, lastname: string, cnp: string, email: string){
		var headers = new Headers();
		var json = JSON.stringify({ firstname: firstname, lastname: lastname, cnp: cnp, email: email });

		headers.append('Content-Type', 'application/json');
		return this.http.post('http://localhost:6969/addclient', json, { headers: headers }).map(res => res.json());
	}

	getClientListPage(pagenumber: number, pagesize: number, filter: string){
		var headers = new Headers();
		var json = JSON.stringify({ pagenumber: pagenumber, pagesize: pagesize, filter: filter });

		headers.append('Content-Type', 'application/json');
		return this.http.post('http://localhost:6969/clientlist', json, { headers: headers }).map(res => res.json());
	}

	getTPoints(){
		var headers = new Headers();

		headers.append('Content-Type', 'application/json');
		return this.http.get('http://localhost:6969/gettpoints', { headers: headers}).map(res => res.json());
	}

	getSubTypes(){
		var headers = new Headers();

		headers.append('Content-Type', 'application/json');
		return this.http.get('http://localhost:6969/getsubtypes', { headers: headers}).map(res => res.json());
	}

	getClient(cnp: string){
		var headers = new Headers();
		var json = JSON.stringify({ msg: cnp });

		headers.append('Content-Type', 'application/json');
		return this.http.post('http://localhost:6969/client', json, { headers: headers });
	}

	removeClient(cnp: string){
		var headers = new Headers();
		var json = JSON.stringify({ msg: cnp });

		headers.append('Content-Type', 'application/json');
		return this.http.post('http://localhost:6969/removeclient', json, { headers: headers }).map(res => res.json());
	}

	updateClient(firstname: string, lastname: string, cnp: string, email: string){
		var headers = new Headers();
		var json = JSON.stringify({ firstname: firstname, lastname: lastname, cnp: cnp, email: email });

		headers.append('Content-Type', 'application/json');
		return this.http.post('http://localhost:6969/updateclient', json, { headers: headers }).map(res => res.json());
	}

	makeTransaction(cnp: string, iduser: number, idsub: number){
		var headers = new Headers();
		var json = JSON.stringify({ cnp: cnp, iduser: iduser, idsub: idsub });

		headers.append('Content-Type', 'application/json');
		return this.http.post('http://localhost:6969/maketransaction', json, { headers: headers }).map(res => res.json());
	}
}