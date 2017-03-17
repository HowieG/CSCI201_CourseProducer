window.addEventListener('load', loadSetupForm, false)

function loadSetupForm(event) {
	var setupForm = document.forms['setupform'];
	setupForm.addEventListener('submit', handleFormSubmit, false)
}

function handleFormSubmit(event) {
	event.preventDefault(); // prevents default submission behavior
	var setupForm = document.forms['setupform'];
	
	var url = "SetupServlet";
	var params = "?";

	// setup the params
	var formData = new FormData(setupForm);
	var entries = [];
	for (var [key, value] of formData.entries()) {
		key = encodeURIComponent(key)
		value = encodeURIComponent(value)
		entries.push(key + "=" + value);
	}
	params += entries.join("&");

	// create AJAX request
	var req = new XMLHttpRequest();
	req.open("GET", url + params, true);
	req.onreadystatechange = function () {
		if(req.readyState == 4 && req.status == 200) {
			processForm(JSON.parse(req.responseText));
		}
	}
	req.send(null);
}

function processForm(formData) {
	setStateToFactory();
	
	var managerName = document.getElementById('manager-name');
	managerName.innerHTML = "Manager: " + formData.name
	
	var colorables = document.getElementsByClassName('colorable');
	for (var colorable of colorables) {
		colorable.style.backgroundColor = formData.color;
	}
}

function setStateToFactory() {
	var setupFormBlock = document.getElementById('setup-form');
	var factoryContainerBlock = document.getElementById('factory-container');
	setupFormBlock.style.display = 'none';
	factoryContainerBlock.style.display = 'table';
}

function setStateToSetupForm() {
	var setupFormBlock = document.getElementById('setup-form');
	var factoryContainerBlock = document.getElementById('factory-container');
	setupFormBlock.style.display = 'block';
	factoryContainerBlock.style.display = 'none';
}