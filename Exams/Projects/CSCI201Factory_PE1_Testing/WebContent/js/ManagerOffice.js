//PE1
// creates a ManagerOffice with the name and image data from Factory's managerOffice data member
//See Lab 2 -> FactoryWall.js for reference
function ManagerOffice(cell, managerOffice) {
	// create and place image
	var name = managerOffice['name'];
	var img = document.createElement('img');
	img.id = name;
	img.src = 'img/' + managerOffice.image;
	//append it to the bottom right grid cell
	cell.appendChild(img);
	
	this.createLabel(cell, name);
}

ManagerOffice.prototype.createLabel = function (cell, name) {
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