var protocol = 'ws://',
	hostname = window.location.hostname,
	port     = ':8080',
	pathname = '/' + window.location.pathname.split('/')[1];
var socket = new WebSocket(protocol + hostname + port + pathname + '/ws');
var factory; // GLOBAL INSTANCE OF FACTORY!

socket.onopen = function (event) {
	readTextFile('resources/factory.txt', function (text) {
		socket.send(text);
	});
}

socket.onmessage = function (event) {
	var msg = JSON.parse(event.data);
	console.log(msg); // debug by uncommenting this
	var action = msg.action;

	if (action == 'Factory') {
		if (factory) factory.stop();
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
	} else if (action == 'Report') {
		factory.report(msg['report']);
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
