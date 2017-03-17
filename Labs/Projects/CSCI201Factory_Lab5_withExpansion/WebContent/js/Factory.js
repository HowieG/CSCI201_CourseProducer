function Factory(factoryData) {
	// set reference to HTML
	this.simulationHeading = document.getElementById('factory-simulation-heading');
	this.simulation = document.getElementById('factory-simulation');
	this.productTable = document.getElementById('factory-product-table');
	this.messages = document.getElementById('factory-messages');
	this.slider = document.getElementById('factory-slider');

	// clear current canvas
	this.resetCanvas();

	// log action
	this.reportFactoryData(factoryData);

	// data
	this.boundingBox = {};
	this.workers = [];
	this.resources = {};

	// dynamically update the factory UI
	this.simulationHeading.innerHTML = factoryData.name;
	this.drawGrid(factoryData.width, factoryData.height);
	this.drawResources(factoryData.resources);
	this.drawTaskBoard(factoryData.taskBoard);
	this.drawWorkers(factoryData.workers);
	this.drawWalls(factoryData.walls);
	this.drawWalls(factoryData.walls2);
	this.drawWorkerRating(factoryData.workers);
	this.attachEventListeners();
	this.onresize();
}

Factory.prototype.moveWorker = function (worker, shortestPathStack) {
	this.workers[worker['number']].move(shortestPathStack);
}


/* ***** Factory Helper Methods ***** */

/**
 * On window resize, resize the FactorySimulation table contents
 */
Factory.prototype.onresize = function () {
	var oldBoundingBox = this.boundingBox;
	this.boundingBox = document.getElementById('factory-simulation-container2').getBoundingClientRect();
	var factoryHeight = this.simulation.rows.length,
		factoryWidth = this.simulation.rows[0].cells.length; // may throw error if not generated
	var cellWidth = this.boundingBox.width / factoryWidth,
		cellHeight = this.boundingBox.height / factoryHeight;

	// resize each cell
	for (var y = 0; y < factoryHeight; y++) {
		for (var x = 0; x < factoryWidth; x++) {
			var cell = this.simulation.rows[y].cells[x];
			cell.style.width = cellWidth + 'px';
			cell.style.height = cellHeight + 'px';
		}
	}
	
	// resize worker containers
	for (var worker of this.workers) {
		worker.container.style.width = cellWidth + 'px';
		worker.container.style.height = cellHeight + 'px';
		var cellPosition = this.getNodePosition(worker.y, worker.x);
		worker.setRelativeXPosition(cellPosition.relativeX);
		worker.setRelativeYPosition(cellPosition.relativeY);
	}
}

/**
 * Draws grid on factorySim by creating a Table
 */
Factory.prototype.drawGrid = function (factoryWidth, factoryHeight) {
	this.boundingBox = document.getElementById('factory-simulation-container2').getBoundingClientRect();
	var cellWidth = this.boundingBox.width / factoryWidth,
		cellHeight = this.boundingBox.height / factoryHeight;

	// create factory tiles (cells)
	for (var i = 0; i < factoryHeight; i++) {
		var row = document.createElement('tr');
		for (var j = 0; j < factoryWidth; j++) {
			var cell = document.createElement('td');
			cell.className = 'factory-node';
			cell.style.width = cellWidth + 'px';
			cell.style.height = cellHeight + 'px';
			row.appendChild(cell);
		}
		this.simulation.appendChild(row);
	}
}

Factory.prototype.getNodePosition = function (row, col) {
	var cell = this.simulation.rows[row].cells[col];

	var simulationRect = this.simulation.getBoundingClientRect();
	var cellRect = cell.getBoundingClientRect();

	var x = cellRect.left - simulationRect.left + (cellRect.width / 2);
	var y = cellRect.top - simulationRect.top + (cellRect.height / 2);
	var relativeX = x / simulationRect.width;
	var relativeY = y / simulationRect.height;

	return { x, y, relativeX, relativeY };
}

/**
 * Draws resources onto factorySimulation according to their coordinates
 */
Factory.prototype.drawResources = function (resources) {
	for (var resource of resources) {
		// get coordinates
		var x = resource['x'],
		y = resource['y'];
		var cell = this.simulation.rows[y].cells[x];

		if (this.resources[resource['name']]) {
			this.resources[resource['name']].update(cell, resource);
		} else {
			this.resources[resource['name']] = new FactoryResource(cell, resource);
		}
	}
}

/**
 * Draws taskBoard onto factorySimulation according to its coordinates
 * Fills in factoryProductTable with products (data)
 */
Factory.prototype.drawTaskBoard = function (taskBoard) {
	// get coordinates
	var x = taskBoard['x'],
	y = taskBoard['y'];
	var cell = this.simulation.rows[y].cells[x];

	this.taskBoard = new FactoryTaskBoard(cell, taskBoard, this);
}

/**
 * Draws each (animatable) worker
 */
Factory.prototype.drawWorkers = function (workers) {
	var dimensions = this.simulation.rows[0].cells[0].getBoundingClientRect();
	for (var worker of workers)
		this.workers.push(new FactoryWorker(worker, worker.currentNode, dimensions, this));
}

/**
 * Draws walls onto factorySimulation according to their coordinates
 */
Factory.prototype.drawWalls = function (walls) {
	for (var wall of walls) {
		// get coordinates
		var x = wall['x'],
		y = wall['y'];
		// the grid cell we wish to place the wall on
		var cell = this.simulation.rows[y].cells[x];

		new FactoryWall(cell, wall);
	}
}

/**
 * Adds worker names and rate fields
 */
Factory.prototype.drawWorkerRating = function (workers) {
	
	//create a div container to hold our ratings area
	var ratingDiv = document.createElement('div');
	//assign it an id for styling
	ratingDiv.id = "rating-container";

	//the form we want to add to
	var notesArea = document.forms['managerNotes'];
	
	//dynamically add all the worker names and a corresponding number input
	for (var worker of workers) {
		var name = worker.name;
		//add the number input
		ratingDiv.innerHTML += "<label for='rating'>" + name + "</label>";
		ratingDiv.innerHTML += "<input type='hidden' name='workerName' value='" + name + "'>";
		ratingDiv.innerHTML += "<input type='number' name='rating' min='1' max='10'><br>";
	}
	
	//add a textarea
	ratingDiv.innerHTML += "<br><textarea rows='5' cols='40' name='notes'>Enter notes here...</textarea>";

	//add a submit button
	ratingDiv.innerHTML += "<br><input type='submit' value='Submit Notes'>";
	//add the whole div to our form
	notesArea.appendChild(ratingDiv);
}

/**
 * Prints message to factoryMessages describing factory's contents
 */
Factory.prototype.reportFactoryData = function (factoryData) {
	var resourceString = '';
	var productString = '';

	// parse resource string
	for (var resource of factoryData.resources) {
		resourceString += '<li>Resource: ' + resource['name'] + ' has quantity ' + resource['quantity'] + '</li>\n';
	}

	// parse product string
	for (var product of factoryData.products) {
		productString += '<li>Product: ' + product['name'] + ' needs quantity ' + product['quantity'] + '\n<ul>';
		for (var resourceNeeded of product['resourcesNeeded']) {
			productString += '<li>Resource: ' + resourceNeeded['name'] + ' has quantity ' + resourceNeeded['quantity'] + '</li>\n';
		}
		productString += '</ul></li>'
	}

	// combine
	var configText = '<li>Factory Received.</li>\n' +
	'<li>Factory Name: ' + factoryData.name + '</li>\n' +
	resourceString +
	productString;

	// print
	this.messages.innerHTML += configText;
}

Factory.prototype.report = function (preparedReport) {
	this.messages.innerHTML += '<li>' + preparedReport + '</li>';
}

/**
 * Clears factorySimulationHeading, factorySimulation, and factoryProductTable
 */
Factory.prototype.resetCanvas = function () {
	// factory simulation heading
	this.simulationHeading.innerHTML = '';

	// factory simulation
	while (this.simulation.hasChildNodes()) {
		this.simulation.removeChild(this.simulation.lastChild);
	}

	// factory product table
	while (this.productTable.hasChildNodes()) {
		this.productTable.removeChild(this.productTable.lastChild);
	}
}

Factory.prototype.attachEventListeners = function () {
	// reference: https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener
	var factoryControllerPopup = document.getElementById('factory-controller-container');
	
	// button to show factory controller popup
	var openFactoryControllerButton = document.getElementById('open-factory-controller');
	openFactoryControllerButton.addEventListener('click', function openFactoryController() {
		factoryControllerPopup.style.display = 'block';
	}, false);
	
	// button to close factory controller popup
	var closeFactoryControllerButton = factoryControllerPopup.getElementsByClassName('popup-close')[0];
	closeFactoryControllerButton.addEventListener('click', function closeFactoryController() {
		factoryControllerPopup.style.display = 'none';
	}, false);
	
	// pause functionality (shifting speed to 0%)
	var factoryControllerPauseButton = document.getElementById('factory-controller-pause');
	factoryControllerPauseButton.addEventListener('click', function pauseFactory() {
		this.slider.value = 0;
	}.bind(this), false);
	
	// continue functionality (shifting speed to 50%)
	var factoryControllerContinueButton = document.getElementById('factory-controller-continue');
	factoryControllerContinueButton.addEventListener('click', function continueFactory() {
		this.slider.value = 50;
	}.bind(this), false);
	
	// reset functionality
	var factoryControllerResetButton = document.getElementById('factory-controller-reset');
	factoryControllerResetButton.addEventListener('click', function resetFactory() {
		this.resetFactory();
	}.bind(this), false);
}

Factory.prototype.resetFactory = function () {
	socket.send(JSON.stringify({
		action: 'ResetFactory'
	}));
}

Factory.prototype.stop = function () {
	for (var worker of this.workers) {
		worker.stop();
	}
}