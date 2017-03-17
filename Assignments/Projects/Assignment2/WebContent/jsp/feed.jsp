<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="data.DataStorage" %>
<%@ page import="data.User" %>
<%@ page import="data.Event" %>
<%@ page import="data.StringConstants" %>
<html>
<head>
	<title>User Profile</title>
	<link rel="stylesheet" href= "../css/main.css">
	<link rel="stylesheet" href= "../css/feed.css">
	<link rel="stylesheet" href= "../css/profile.css">
	<link href="https://fonts.googleapis.com/css?family=Lato:700i" rel="stylesheet">
</head>
<body>
<!-- get the data storage object and logged in user info -->
<%  DataStorage ds = (DataStorage) session.getAttribute(StringConstants.DATA); 
	User user = ds.getLoggedInUser();
	String name = user.getFName() +" "+user.getLName();
	String un = "@"+user.getUsername();
%>
	<div id = "title_container">
		Cinemate
	</div>
	<div id = "user_container_outer">
		<div id = "user_container_inner">
			<img src = <%= user.getImage()%> alt = "Profile Image Not Found">
			<h1><%= name %></h1>
			<h3><%= un %></h3>
		</div>
	</div>
	<br>
		<table id = "feed">
			<thead >
				<tr>
					<td><h2>Feed</h2></td>
				</tr>
			</thead>
			<tbody>
<!-- 			Get the users list of following and add their own username. Then iterate through this new set and get the user object
			and iterate through each of their feeds -->
				<% 
				Set<String> following = new HashSet<>(user.getFollowing());
				following.add(user.getUsername());
				
				for (String username : following){
					
					User current = ds.getUser(username);
							
						for (Event event : current.getFeed()){
							String toShow = event.getUsername() +" " + event.getAction().toLowerCase() +" "+event.getMovie().getTitle(); %>
									
							<tr><td> <%= toShow %></td></tr>
					<% }
				} %>
			</tbody>
		</table>
</body>
</html>