<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
	String[] productsNames = (String[]) request.getAttribute("productsNames");
	String[] productsStarted = (String[]) request.getAttribute("productsStarted");
	String[] productsCompleted = (String[]) request.getAttribute("productsCompleted");
	
	String workerName = (String) request.getAttribute("workerName");
	String completedTotal = (String) request.getAttribute("completedTotal");
%>

<h2><%= workerName %></h2>

<table>
	<tr>
		<th>Product</th>
		<th>Started</th>
		<th>Completed</th>
	</tr>
	<%for(int i = 0; i < productsNames.length; i++) {%>
	<tr>
		<td><%= productsNames[i] %></td>
		<td><%= productsStarted[i] %></td>
		<td><%= productsCompleted[i] %></td>
	</tr>
	<%}%>
</table>

<p>Total Completed: <%= completedTotal %></p>