/*
 * WebSocketEndpoint.js
	•	Notice how index.html did not explicitly invoke any methods in this file to execute, but it “instantiated” it when it included it. The top of this file creates a new WebSocket, and the file’s socket.onopen() function is called when the socket is open, which then triggers further functions. This is the answer to “how does everything get started?”
	•	socket.onopen() calls readTextFile(), which makes a GET request for the factory.txt file stored on the server. It then calls the callback that was passed into it to go back to socket.onopen(). This callback is an anonymous function that just calls socket.send(text).
 	
 	socket.onmessage():
	• 	receives the JSON data
	•	var msg = JSON.parse(event.data): calls JavaScript’s built-in JSON parser
	•	var action = msg.action: reads in the message’s action attribute (Message’s String action)
	•	if (action == 'Factory'): If the message was of type FactoryMessage (with String action = “Factory”)
	•	factory = new Factory(msg['factory']): creates a new Factory object - Factory.js object NOT Factory.java object - with the message’s “factory” attribute (FactoryMessage has a data member “private Factory factory”).
	•	The other else if statements call methods from Factory.js depending on the value of the action in the received message.

 */

var protocol = 'ws://',
	hostname = window.location.hostname,
	port     = ':8080',
	pathname = '/' + window.location.pathname.split('/')[1];
var socket = new WebSocket(protocol + hostname + port + pathname + '/ws');
var factory; // GLOBAL INSTANCE OF FACTORY!

socket.onopen = function (event) {
	readTextFile('resources/factory2.txt', function (text) {
		socket.send(text);
	});
}

socket.onmessage = function (event) {
	var msg = JSON.parse(event.data);
	// console.log(msg); // debug by uncommenting this
	var action = msg.action;

	if (action == 'Factory') {
		factory = new Factory(msg['factory']);
	} else if (action == 'WorkerMoveToPath') {
		factory.moveWorker(msg['worker'], msg['shortestPathStack']);
	} else if (action == 'UpdateTaskBoard') {
		factory.taskBoard.printProductTable(
				msg['taskBoard'].workerTableColumnNames, 
				msg['taskBoard'].workerTableDataVector, 
				factory);
	} else if (action == 'UpdateResources') {
		factory.drawResources(msg['resources']);
	}
}

window.onresize = function () {
	if (factory) factory.onresize();
}

function readTextFile(file, callback)
{
	var xhr = new XMLHttpRequest();
	xhr.open("GET", file, true);
	xhr.onreadystatechange = function () {
		if(xhr.readyState === 4 && (xhr.status === 200 || xhr.status == 0)) {
			var allText = xhr.responseText;
			callback(allText);
		}
	}
	xhr.send(null);
}
