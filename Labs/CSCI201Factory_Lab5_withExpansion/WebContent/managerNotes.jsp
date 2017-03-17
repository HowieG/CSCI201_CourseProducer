<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>


<%
	String[] ratings = (String[]) request.getAttribute("ratings");
	String[] workerNames = (String[]) request.getAttribute("workerNames");
	String notes = (String) request.getAttribute("notes");
	String color = (String) session.getAttribute("color");
%>


<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Manager Notes</title>

<style type="text/css">

/*Styling sourced from: http://www.w3schools.com/html/tryit.asp?filename=tryhtml_table_intro */
table {
    font-family: arial, sans-serif;
    border-collapse: collapse;
    width: 100%;
}

td, th {
    border: 1px solid #dddddd;
    text-align: left;
    padding: 8px;
}

tr:nth-child(even) {
    background-color: <%= color %>;
}
</style>

</head>
<body>

	<table>
		<tr>
			<th>Worker</th>
			<th>Rating</th>
		</tr>
		<%for(int i = 0; i < ratings.length; i++) {%>
		<tr>
			<td><%= workerNames[i]%></td>
			<td><%= ratings[i] %></td>
		</tr>
		<%}%>
	</table>
	
	<br>
	
	<strong>Notes:</strong>
	<%= notes %>

</body>
</html>