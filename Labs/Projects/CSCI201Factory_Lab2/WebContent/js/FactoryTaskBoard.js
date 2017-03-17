function FactoryTaskBoard(cell, taskBoard, factory) {
	this.placeImage(cell, taskBoard, factory);
	this.createLabel(cell, taskBoard['name']);
	this.printProductTable(taskBoard.workerTableColumnNames, taskBoard.workerTableDataVector, factory);
}

/* ***** SETUP ***** */

FactoryTaskBoard.prototype.placeImage = function (cell, taskBoard, factory) {
	// create and place image
	var name = taskBoard['name'];
	var img = document.createElement('img');
	img.id = name;
	img.src = 'img/' + taskBoard.image;
	cell.appendChild(img);
}

FactoryTaskBoard.prototype.createLabel = function (cell, name) {
	// create label
	var label = document.createElement('span');
	label.className = 'factory-label';
	label.innerHTML = name;
	label.style.display = 'none';
	cell.appendChild(label);

	// event listeners
	cell.addEventListener('mouseover', function () {
		label.style.display = 'block';
	});
	cell.addEventListener('mouseout', function () {
		label.style.display = 'none';
	});
}

FactoryTaskBoard.prototype.printProductTable = function (workerTableColumnNames, workerTableDataVector, factory) {
	this.clearProductTable(factory);
	this.printHeading(workerTableColumnNames, factory);

	for (var rowData of workerTableDataVector) {
		var productRow = document.createElement('tr');
		for (var colData of rowData) {
			var colCell = document.createElement('td');
			colCell.innerHTML = colData;
			productRow.appendChild(colCell);
		}
		factory.productTable.appendChild(productRow);
	}
}

FactoryTaskBoard.prototype.clearProductTable = function (factory) {
	while (factory.productTable.hasChildNodes()) {
		factory.productTable.removeChild(factory.productTable.lastChild);
	}
}

FactoryTaskBoard.prototype.printHeading = function (workerTableColumnNames, factory) {
	var productTableHeading = document.createElement('tr');
	for (var columnName of workerTableColumnNames) {
		var columnHeading = document.createElement('th');
		columnHeading.innerHTML = columnName;
		productTableHeading.appendChild(columnHeading);
	}
	factory.productTable.appendChild(productTableHeading);
}
