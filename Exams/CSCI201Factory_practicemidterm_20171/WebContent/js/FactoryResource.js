function FactoryResource(cell, resource) {
	// create and place image
	var name = resource['name'];
	var img = document.createElement('img');
	img.id = name;
	img.src = 'img/' + resource.image;
	cell.appendChild(img);

	// place quantityText
	var quantity = resource['quantity'];
	this.quantityText = document.createElement('span');
	this.quantityText.className = 'resource-quantity';
	this.quantityText.innerHTML = quantity;
	cell.appendChild(this.quantityText);

	this.createLabel(cell, name);
}

/* ***** SETUP ***** */

FactoryResource.prototype.update = function (cell, resource) {
	// update quantityText
	var quantity = resource['quantity'];
	this.quantityText.innerHTML = quantity;
}

FactoryResource.prototype.createLabel = function (cell, name) {
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
