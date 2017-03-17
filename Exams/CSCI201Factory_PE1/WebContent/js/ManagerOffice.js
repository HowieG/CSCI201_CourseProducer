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
}