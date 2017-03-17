function FactoryWall(cell, wall) {
	// create and place image
	var name = wall['name'];
	var img = document.createElement('img');
	img.id = name;
	
	if(wall.image.startsWith("http")) {
		img.src = wall.image;
	}
	else {
		img.src = 'img/' + wall.image;
	}
	
	cell.appendChild(img);
}