function handleWorkerSelect(factoryWorker) {
	
	var url = "WorkerNotesServlet?workerIndex=" + factoryWorker.worker.number + "&socketSessionId=" + factory.sessionId;

	// create AJAX request
	var req = new XMLHttpRequest();
	req.open("GET", url, true);
	req.onreadystatechange = function () {
		if(req.readyState == 4 && req.status == 200) {
			createWorkerNotesPopup(req.responseText);
		}
	}
	req.send(null);
}

function createWorkerNotesPopup(notes) {
	var body = document.body;
	var workerNotesPopup = document.createElement("div"),
		popupHeading = document.createElement("div"),
		popupClose = document.createElement("div"),
		workerNotesContainer = document.createElement("div");
	
	workerNotesPopup.className = "popup";
	popupHeading.className = "popup-heading";
	popupHeading.innerHTML = "Worker Notes";
	popupClose.className = "popup-close";
	popupClose.innerHTML = "x";
	workerNotesContainer.innerHTML = notes;
	
	workerNotesPopup.appendChild(popupHeading);
	workerNotesPopup.appendChild(popupClose);
	workerNotesPopup.appendChild(workerNotesContainer);
	workerNotesPopup.style.display = 'block';
	body.appendChild(workerNotesPopup);
	
	popupClose.addEventListener('click', function (){
		workerNotesPopup.parentNode.removeChild(workerNotesPopup);
	}, false);
}