<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="data.DataStorage" %>
<%@ page import="data.User" %>
<%@ page import="data.StringConstants" %>
<html>
<head>
	<link rel="stylesheet" href="../css/main.css">
	<link rel="stylesheet" href="../css/navbar.css">
	<link href="https://fonts.googleapis.com/css?family=Lato:700i" rel="stylesheet">
	<script src = "../js/navbar.js"></script>
</head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Cinemate</title>
</head>
<body>
<% 
	DataStorage ds = (DataStorage)session.getAttribute(StringConstants.DATA);
	User loggedInUser = ds.getLoggedInUser();
%>
	<div id = "navBar">
		<a href = "${pageContext.request.contextPath}/jsp/big_feed.jsp">
			<img src = "../img/navbar/feed_icon.png" id = "feed_icon" class = "clickable" alt = "feed" title = "View feed">
		</a>
		<a href = "${pageContext.request.contextPath}/jsp/user_profile.jsp?<%=StringConstants.USERNAME%>=<%=loggedInUser.getUsername()%>">
			<img src = <%= loggedInUser.getImage() %> id = "logged_in_user_icon" class = "clickable" alt = "user_icon" title = "View profile">
		</a>
		<!-- container for our search bar -->
		<div id = "search_bar">
			<!-- Actual search input which will be dynamically updated (search type image and placeholder text)  -->
			<input type = "search" id = "search_input" placeholder = "Search movies" size = "3" width = "40px">
			<input id = "search_type" style = "display:none;" value = "<%= StringConstants.MOVIE %>"></input>
			<!-- the search type image -->
			<img src="../img/navbar/clapperboard_icon.png" id = "toggleable_search_type" class = "clickable" onclick = "toggleSearch()" title = "Toggle search type">
		</div>
		<!-- pass in the string used for the parameter key in the ajax request -->
		<img src = "../img/navbar/search_icon.png" id = "search_icon" class = "clickable" alt = "search" title = "Search" onclick = "search('<%=StringConstants.SEARCH_PARAM%>', 'search_type')">
		<a href = "${pageContext.request.contextPath}/jsp/file_chooser.jsp">
			<img src = "../img/navbar/exit_icon.jpg" id = "exit_icon" class = "clickable" alt = "exit" title = "Exit">
		</a>
		<a href = "${pageContext.request.contextPath}/jsp/login.jsp">
			<img src = "../img/navbar/logout_icon.png" id = "logout_icon" class = "clickable" alt = "logout" title = "Log out">
		</a>
		<div id = "cinemate_title">
			Cinemate
		</div>
	</div>
</body>
</html>