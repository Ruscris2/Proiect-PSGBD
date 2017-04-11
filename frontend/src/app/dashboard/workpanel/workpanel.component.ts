import { Component } from '@angular/core';

@Component({
  selector: 'app-workpanel',
  templateUrl: './workpanel.component.html',
  styleUrls: ['./workpanel.component.css']
})
export class WorkpanelComponent {
	sidebarItems = [
		{ name: 'First item here', selected: true },
		{ name: 'Second item here', selected: false },
		{ name: 'Third item here', selected: false },
		{ name: 'Fourth item here', selected: false }
	];

	selectedItem = 0;

	onHeadingClick(index){
		this.sidebarItems[this.selectedItem].selected = false;
		this.selectedItem = index;
		this.sidebarItems[this.selectedItem].selected = true;
	}
}
