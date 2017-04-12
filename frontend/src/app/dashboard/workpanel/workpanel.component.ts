import { Component } from '@angular/core';
import {BackendService} from "../../backend.service";

@Component({
  selector: 'app-workpanel',
  templateUrl: './workpanel.component.html',
  styleUrls: ['./workpanel.component.css']
})
export class WorkpanelComponent {
	sidebarItems = [
		{ name: 'Clients', selected: true },
		{ name: 'Subscription Manager', selected: false },
		{ name: 'Third item here', selected: false },
		{ name: 'Fourth item here', selected: false }
	];

	clientListPage: any;

	clientpagenumber = 1;
	selectedItem = 0;
	addClientSelectedButton = 2;

	// Add client text boxes
	addClientFirstName: string;
	addClientLastName: string;
	addClientCNP: string;
	addClientEmail: string;

	constructor(private backendService: BackendService) {
		this.getClientPage();
	}

	onHeadingClick(index){
		if(index != this.selectedItem)
			this.resetAddClientStrings();

		this.sidebarItems[this.selectedItem].selected = false;
		this.selectedItem = index;
		this.sidebarItems[this.selectedItem].selected = true;
	}

	onAddClientPageClick(){
		this.addClientSelectedButton = 0;
	}

	onRemoveClientPageClick(){
		this.addClientSelectedButton = 1;
		this.resetAddClientStrings();
	}

	onClientListPageClick(){
		this.addClientSelectedButton = 2;
		this.resetAddClientStrings();
	}

	onInputChange(event: any){
		if(event.target.id == 'addClientFNameBox'){
			this.addClientFirstName = event.target.value;
		}
		if(event.target.id == 'addClientLNameBox'){
			this.addClientLastName = event.target.value;
		}
		if(event.target.id == 'addClientCNPBox'){
			this.addClientCNP = event.target.value;
		}
		if(event.target.id == 'addClientEmailBox'){
			this.addClientEmail = event.target.value;
		}
	}

	onRegisterClick(){
		if(this.addClientFirstName == '' || this.addClientLastName == '' || this.addClientCNP == '' || this.addClientEmail == ''){
			alert('One or more fields are empty!');
			return;
		}

		this.backendService.addClient(this.addClientFirstName, this.addClientLastName, this.addClientCNP, this.addClientEmail).subscribe(
			data => {let jsonParsed = JSON.parse(JSON.stringify(data));
						alert(jsonParsed.msg)},
			error => alert('Register user failed!')
		);
	}

	onPrevClientPageClick(){
		if(this.clientpagenumber == 1)
			return;

		this.clientpagenumber--;
		this.getClientPage();
	}

	onNextClientPageClick(){
		this.clientpagenumber++;
		this.getClientPage();
	}

	getClientPage(){
		this.backendService.getClientListPage(this.clientpagenumber, 10).subscribe(
			data => { this.clientListPage = data },
			error => console.log('Error getting client list page!')
		);
	}

	resetAddClientStrings(){
		this.addClientFirstName = '';
		this.addClientLastName = '';
		this.addClientCNP = '';
		this.addClientEmail = '';
	}
}
