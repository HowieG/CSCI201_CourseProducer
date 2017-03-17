/*
 * Factory.js:
	•	It’s confusing, but objects in JavaScript can be defined as functions. So when we do “factory = new Factory(msg['factory']),” we are calling “function Factory(factoryData),” where factoryData is the value of msg[‘factory’] – which if you recall is a Factory.java object! You may think of “function Factory(factoryData)” as a constructor if it helps, just know that is not accurate terminology.
	•	Now any time we write something like factoryData.resources, we are accessing the data member named “resources” from this Factory.java object (private Vector<FactoryResource> resources). Note: the factoryData.resources received by the client is already stored as an array (not a Vector) in the JSON. This was likely done by the Gson library.
	•	If we take “this.drawResources(factoryData.resources)” as an example, the function loops through the array of FactoryResources. FactoryResource is also Serializable because it extends FactoryNode which implements FactoryObject which implements Serializable. Therefore we can access its data members just like we can access Factory.java’s. The function simply generates some HTML, but notice how it creates a “new FactoryResource(cell, resource).” Again, this is a new FactoryResource.js object, NOT a FactoryResource.java object. The FactoryResource.js object then does its own thing to generate HTML for the resources.
 */

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
	//PE1
	this.drawManagerOffice(factoryData.managerOffice);
	//PE1
	this.drawWorkerButtons(factoryData.workers);
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

//PE1
//see Lab2: Factory.js -> Factory.prototype.drawWalls for reference
Factory.prototype.drawManagerOffice = function (managerOffice) {
		// get coordinates
		//for the exam, we know this will be the bottom right because we hardcoded x=14, y=14
		var x = managerOffice['x'],
		y = managerOffice['y'];
		var cell = this.simulation.rows[y].cells[x];

		//create a new ManagerOffice.js object
		new ManagerOffice(cell, managerOffice);
}

//PE1
//see Lab 5: Factory.js -> Factory.prototype.drawWorkerRating for reference
Factory.prototype.drawWorkerButtons = function (workers) {
	
	//create a div to hold the button (optional, could have just made a button)
	var buttonDiv = document.createElement('div');
	
	//the div we want to add to, located in index.jsp
	var workerButtonArea = document.getElementById("workerButtons");
	
	//we keep track of the worker indices for simplicity
	var i = 0;
	//dynamically add all the worker buttons
	for (var worker of workers) {
		var name = worker.name;
		//add a button which calls sendManagerAction(i) when clicked, button text should be worker name
		buttonDiv.innerHTML += "<button type='button' onclick = 'Factory.prototype.sendManagerAction(" + i + ")' name='worker'>" + name + "</button><br>";
		//increment worker index
		i = i+1;
	}
	
	//add the whole div to our containing div
	workerButtonArea.appendChild(buttonDiv);
}

//PE1
//sends the worker index as JSON data over the socket with the action "MustSeeManager"
//we could have done this without prototyping, but did for consistency
//see: FactoryWorker.js -> FactoryWorker.prototype.move -> tick() for reference
Factory.prototype.sendManagerAction = function (workerNumber) {
	socket.send(JSON.stringify({
		action: 'MustSeeManager',
		workerNumber: workerNumber
	}));
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
