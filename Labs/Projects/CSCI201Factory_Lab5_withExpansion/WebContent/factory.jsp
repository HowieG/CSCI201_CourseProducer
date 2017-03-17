<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ page import="utilities.ColorTools"%>
<%
	ColorTools colorTools = new ColorTools();

	int r = colorTools.getRed();
	int g = colorTools.getGreen();
	int b = colorTools.getBlue();
	
	String name = (String) session.getAttribute("name");
	String color = (String) session.getAttribute("color");
%>

<head>
<meta charset="UTF-8">
<title>CSCI201 Factory Simulator</title>
<link rel="stylesheet" href="css/main.css">
<script src="js/WebSocketEndpoint.js" type="text/javascript"></script>
<script src="js/Factory.js" type="text/javascript"></script>
<script src="js/FactoryWorker.js" type="text/javascript"></script>
<script src="js/FactoryResource.js" type="text/javascript"></script>
<script src="js/FactoryWall.js" type="text/javascript"></script>
<script src="js/FactoryTaskBoard.js" type="text/javascript"></script>

<style type="text/css">
.colorable {
	background-color: <%=color%>;
}

#factory-controller-container {
	background-color: rgb(<%=r%>, <%=g%>, <%=b%>);
}
</style>


</head>
<body>

	<table id="factory-container">
		<tr id="factory-container-top">
			<td id="factory-simulation-container"><span
				id="factory-simulation-heading"></span>
				<div id="factory-simulation-container2">
					<table id="factory-simulation"></table>
				</div></td>
			<td class = "colorable" id="factory-product-table-container">

				<h3 style="text-align: center;"> Manager: <%=name%></h3>

				<table id="factory-product-table"></table>

				<form name="managerNotes"
					action="${pageContext.request.contextPath}/NotesServlet"
					method="GET"></form>

			</td>
		</tr>
		<tr id="factory-container-bottom">
			<td class = "colorable" id="factory-messages-container">
				<ul id="factory-messages">
					<li>Waiting for factory configuration from server...</li>
				</ul>
			</td>
			<td class = "colorable">
				<button id="open-factory-controller">Controller</button>
			</td>
		</tr>
	</table>
	<div id="factory-controller-container" class="popup">
		<div class="popup-heading">Factory Controller</div>
		<div class="popup-close">close</div>
		<div id="factory-controller-buttons">

			<button id="factory-controller-pause">Pause</button>
			<button id="factory-controller-continue">Continue</button>
			<button id="factory-controller-reset">Reset</button>
		</div>
		<div id="factory-slider-container">
			<b>Speed Controller:</b> <input type="range" id="factory-slider"
				value=50 />
		</div>
	</div>
</body>
</html>