/*
 * FactoryWorker.js:
	•	function Factory(factoryData) calls “this.drawWorkers(factoryData.workers),” which calls “Factory.prototype.drawWorkers,” which pushes a new FactoryWorker.js object into the Factory.js’s array of workers.
	•	You can look through FactoryWorker.js on your own, but let’s take a look at FactoryWorker.prototype.move.

	FactoryWorker.prototype.move:
	•	defines function tick(), which is the callback for window.requestAnimationFrame(tick). Side note: on the first pass of FactoryWorker.prototype.move, just because we define function tick() does not mean we call it, it is only called at the bottom by the second window.requestAnimationFrame(tick). Then once inside the function tick() callback, it calls window.requestAnimationFrame(tick) again which starts a recursive-like back-and-forth between the two.
	•	window.requestAnimationFrame(tick): requests your browser to call a specified animation function before it performs the next repaint, usually at a rate of 60 callbacks per second, but this depends on the browser.
	
	tick():
	•	if (self.moveTowards(self.nextNode, delta)): moveTowards returns true if the worker is close enough to the next node in its path within a certain tolerance.
	•	self.currentNode = self.nextNode: if the worker has arrived, the current node is the node it was moving to.
	•	if (self.pathStack.length > 0): if there are more nodes in the path, i.e. the worker is not at its destination node, the worker will update its nextNode
	•	else: we are at our destination node.
	•	socket.send(JSON.stringify(): sends a string containing an action, the worker, and its currentNode over the socket to be received by WebSocketEndpoint.java’s onMessage() method. As it stands now, the factory code only supports one action being sent from the front-end client to the back-end, which is “WorkerArrivedAtDestination”.

	
 */

function FactoryWorker(worker, startingNode, dimensions, factory) {
	this.factory = factory;
	this.worker = worker;

	// paint worker
	var nodePosition = this.factory.getNodePosition(startingNode.y, startingNode.x);
	this.container = this.placeContainer(dimensions, factory);
	this.setXPosition(nodePosition.x);
	this.setYPosition(nodePosition.y);
	this.placeImage(worker, this.container);
	this.createLabel(this.container, worker.name);

	// movement
	this.pathStack = [];
	this.nextNode = null;
	this.x = startingNode.x;
	this.y = startingNode.y;
}

/* ***** SETUP ***** */

FactoryWorker.prototype.placeContainer = function (dimensions, factory) {
	var container = document.createElement('div');
	container.className = 'factory-worker-container';
	container.style.width = dimensions.width + 'px';
	container.style.height = dimensions.height + 'px';
	factory.simulation.appendChild(container);
	return container;
}

FactoryWorker.prototype.placeImage = function (worker, container) {
	var name = worker['name'];
	var img = document.createElement('img');
	img.id = name;
	img.src = 'img/' + worker.image;
	container.appendChild(img);
}

FactoryWorker.prototype.createLabel = function (cell, name) {
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

/* ***** ANIMATION LOGIC ***** */

/**
 * starts animation for FactoryWorker
 * along the path defined by the shortestPathStack
 */
FactoryWorker.prototype.move = function (shortestPathStack) {
	this.pathStack = shortestPathStack;
	this.nextNode = this.pathStack.pop();

	var prevTick = null;
	var self = this;
	function tick(timestamp) {
		var delta = (!prevTick) ? 0 : timestamp - prevTick;
		prevTick = timestamp;

		if (delta == 0) {
			window.requestAnimationFrame(tick);
			return;
		}

		if (self.moveTowards(self.nextNode, delta)) {
			// if arrived, save current node:
			self.currentNode = self.nextNode;
			// arrived at location but still have somewhere to go:
			if (self.pathStack.length > 0) {
				self.nextNode = self.pathStack.pop();
				window.requestAnimationFrame(tick);
			} else { // arrived at destination
				socket.send(JSON.stringify({
					action: 'WorkerArrivedAtDestination',
					worker: self.worker,
					currentNode: self.currentNode
				}));
			}
			return;
		}

		window.requestAnimationFrame(tick);
	}
	// instead of using an interval, this function ticks at the fastest framerate
	// permitted by your browser.
	window.requestAnimationFrame(tick);
}

/* NOTE:
 * We are calculating the movement using relative position (percentage of the board).
 * Assume deltaTime = 1000 miliseconds (1s), then
 * velocity = (1000 * 0.001 * 0.5) = 0.5
 * Meaning, the worker travels half the board in 1 second, at half speed.
 */
FactoryWorker.prototype.moveTowards = function (factoryNode, deltaTime) {
	var velocity = deltaTime * 0.001 * (parseInt(this.factory.slider.value) / 100);
	var nodePosition = this.factory.getNodePosition(factoryNode.y, factoryNode.x);
	
	if (factoryNode.x > this.x) {
		return this.moveRight(factoryNode.x, nodePosition, velocity);
	} else if (factoryNode.x < this.x) {
		return this.moveLeft(factoryNode.x, nodePosition, velocity);
	} else if (factoryNode.y > this.y) {
		return this.moveDown(factoryNode.y, nodePosition, velocity);
	} else if (factoryNode.y < this.y) {
		return this.moveUp(factoryNode.y, nodePosition, velocity);
	} else { // at node
		return true;
	}
}

FactoryWorker.prototype.moveRight = function (targetX, nodePosition, velocity) {
	var workerX = this.getPosition().relativeX;
	var destinationX = nodePosition.relativeX;
	if (workerX + velocity >= destinationX) {
		// we've reached or surpassed destinationX
		// set workerX to destinationX
		this.setRelativeXPosition(destinationX);
		this.x = targetX;
		return true;
	} else {
		// still animating
		this.setRelativeXPosition(workerX + velocity);
		return false;
	}
}

FactoryWorker.prototype.moveLeft = function (targetX, nodePosition, velocity) {
	var workerX = this.getPosition().relativeX;
	var destinationX = nodePosition.relativeX;
	if (workerX - velocity <= destinationX) {
		// we've reached or surpassed destinationX
		// set workerX to destinationX
		this.setRelativeXPosition(destinationX);
		this.x = targetX;
		return true;
	} else {
		// still animating
		this.setRelativeXPosition(workerX - velocity);
		return false;
	}
}

FactoryWorker.prototype.moveUp = function (targetY, nodePosition, velocity) {
	var workerY = this.getPosition().relativeY;
	var destinationY = nodePosition.relativeY;
	if (workerY - velocity <= destinationY) {
		// we've reached or surpassed destinationX
		// set workerY to destinationY
		this.setRelativeYPosition(destinationY);
		this.y = targetY;
		return true;
	} else {
		// still animating
		this.setRelativeYPosition(workerY - velocity);
		return false;
	}
}

FactoryWorker.prototype.moveDown = function (targetY, nodePosition, velocity) {
	var workerY = this.getPosition().relativeY;
	var destinationY = nodePosition.relativeY;
	if (workerY + velocity >= destinationY) {
		// we've reached or surpassed destinationY
		// set workerY to destinationY
		this.setRelativeYPosition(destinationY);
		this.y = targetY;
		return true;
	} else {
		// still animating
		this.setRelativeYPosition(workerY + velocity);
		return false;
	}
}

/* ***** POSITIONING LOGIC ***** */

FactoryWorker.prototype.getPosition = function () {
	var simulationRect = this.factory.simulation.getBoundingClientRect();
	var workerRect = this.container.getBoundingClientRect();

	var x = workerRect.left - simulationRect.left + (workerRect.width / 2);
	var y = workerRect.top - simulationRect.top + (workerRect.height / 2);
	var relativeX = x / simulationRect.width; // percentage
	var relativeY = y / simulationRect.height; // percentage

	return { x, y, relativeX, relativeY };
}
FactoryWorker.prototype.setXPosition = function (x) {
	var workerXOffset = this.container.getBoundingClientRect().width / 2;
	this.container.style.left = (x - workerXOffset) + 'px';
}
FactoryWorker.prototype.setYPosition = function (y) {
	var workerYOffset = this.container.getBoundingClientRect().height / 2;
	this.container.style.top = (y - workerYOffset) + 'px';
}
FactoryWorker.prototype.setRelativeXPosition = function (relativeX) {
	var simulationRect = this.factory.simulation.getBoundingClientRect();
	this.setXPosition(relativeX * simulationRect.width);
}
FactoryWorker.prototype.setRelativeYPosition = function (relativeY) {
	var simulationRect = this.factory.simulation.getBoundingClientRect();
	this.setYPosition(relativeY * simulationRect.height)
}
