<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String[] ratings = (String[]) request.getAttribute("ratings");
	String[] workerNames = (String[]) request.getAttribute("workerNames");
	String notes = (String) request.getAttribute("notes");
	String color = (String) session.getAttribute("color");
%>

<style type="text/css">
	tr:nth-child(even) {
	    background-color: <%= color %>;
	}
</style>

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