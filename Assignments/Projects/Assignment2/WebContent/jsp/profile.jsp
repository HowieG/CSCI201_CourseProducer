<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="data.DataStorage" %>
<%@ page import="data.User" %>
<%@ page import="data.StringConstants" %>
<html>
<!-- get the data storage object and user info -->
<%  DataStorage ds = (DataStorage) session.getAttribute(StringConstants.DATA); 
	User user = ds.getLoggedInUser();
	String name = user.getFName() +" "+user.getLName();
	String un = "@"+user.getUsername();
%>
<head>
	<title>User Profile</title>
	<link rel="stylesheet" href="../css/main.css">
	<link rel="stylesheet" href="../css/profile.css">
	<link href="https://fonts.googleapis.com/css?family=Lato:700i" rel="stylesheet">
</head>
<body>

	<div id = "title_container">
		Cinemate
	</div>

	<div id = "user_container_outer">
		<div id = "user_container_inner">
			<img src = <%=user.getImage()%> alt = "Profile Image Not Found">
			<h1><%=name %></h1>
			<h3><%=un %></h3>
		</div>
	</div>
<!-- display followers and following if there are any -->
	<div class = "follow_container">
		<h1>Followers</h1>
		<% for (String username : user.getFollowers()) { %>
			<h2><a href = ""><%=username %></a></h2>
		<% } %>
	</div>
	<div class = "follow_container">
		<h1>Following</h1>

		<% for (String username : user.getFollowing()) { %>
			<h2><a href = ""><%=username %></a></h2>
		<% } %>
	</div>

</body>
</html>