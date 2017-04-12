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

	clientListPage: any = [];

	clientpagenumber = 1;
	selectedItem = 0;
	addClientSelectedButton = 2;

	// Add client text boxes
	addClientFirstName: string;
	addClientLastName: string;
	addClientCNP: string;
	addClientEmail: string;

	// Client list filter
	currentFilter: string;

	// Remove client
	removeClientCNP: string = '';
	display_client: boolean = false;
	removeClientId: number;
	removeClientFirstName: string;
	removeClientLastName: string;
	removeClientAbonament: string;
	removeClientStart: string;
	removeClientEnd: string;
	removeClientEmail: string;

	// Update client text boxes
	updateClientCNP: string;
	updateClientFirstName: string;
	updateClientLastName: string;
	updateClientEmail: string;

	constructor(private backendService: BackendService) {
		this.currentFilter = '';
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

	onUpdateClientPageClick(){
		this.addClientSelectedButton = 3;
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
		if(event.target.id == 'removeClientTextBox'){
			this.removeClientCNP = event.target.value;
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

	onFilterClick(){
		this.clientpagenumber = 1;
		this.getClientPage();
	}

	onRemoveBtnClick(){
		if(this.removeClientCNP == ''){
			alert('Fill the CNP textbox first!');
			return;
		}

		this.backendService.getClient(this.removeClientCNP).subscribe(
			res => { if(res.status == 204){
						alert('There is no client with that CNP!');
					} else{
						this.display_client = true;
						let jsonParsed = JSON.parse(JSON.stringify(res.json()))
						this.removeClientId = jsonParsed.id;
						this.removeClientFirstName = jsonParsed.firstname;
						this.removeClientLastName = jsonParsed.lastname;
						this.removeClientAbonament = jsonParsed.abonament;
						this.removeClientStart = jsonParsed.start;
						this.removeClientEnd = jsonParsed.end;
						this.removeClientEmail = jsonParsed.email;
					}
			},
			error => console.log('Error getting client by cnp!')
		);
	}

	onConfirmRemoveClick(){
		this.backendService.removeClient(this.removeClientCNP).subscribe(
			error => console.log('Error removing client!')
		);
		alert('Client removed from database!');
		this.display_client = false;
	}

	onCancelRemoveClick(){
		this.display_client = false;
	}

	onUpdateClick(){
		if(this.updateClientFirstName == '' || this.updateClientLastName == '' || this.updateClientEmail == ''){
			alert('All inputs must be completed!');
			return;
		}

		this.backendService.updateClient(this.updateClientFirstName, this.updateClientLastName, this.updateClientCNP,
			this.updateClientEmail).subscribe(
				error => console.log('Error updating client!')
		);
		alert('Client was updated sucessfully!');
	}

	onUpdateCNPChange(){
		this.updateClientFirstName = '';
		this.updateClientLastName = '';
		this.updateClientEmail = '';
	}

	getDataBtn(){
		this.backendService.getClient(this.updateClientCNP).subscribe(
			res => { if(res.status == 204){
				alert('There is no client with that CNP!');
			} else{
				let jsonParsed = JSON.parse(JSON.stringify(res.json()))
				this.updateClientFirstName = jsonParsed.firstname;
				this.updateClientLastName = jsonParsed.lastname;
				this.updateClientEmail = jsonParsed.email;
			}
			},
			error => console.log('Error getting client by cnp!')
		);
	}

	getClientPage(){
		this.backendService.getClientListPage(this.clientpagenumber, 10, this.currentFilter).subscribe(
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
