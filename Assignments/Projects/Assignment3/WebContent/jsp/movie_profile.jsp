<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.List" %>
<%@ page import="data.Actor" %>
<%@ page import="data.Movie" %>
<%@ include file="navbar.jsp" %>
<html>
<% 
   Movie movie = ds.getMovie(request.getParameter(StringConstants.TITLE));
%>
<head>
	<title>Movie Profile</title>
	<link rel="stylesheet" href="../css/main.css">
	<link rel="stylesheet" href="../css/movie_profile.css">
	<link href="https://fonts.googleapis.com/css?family=Lato:700i" rel="stylesheet">
	<script src = "../js/movie_profile.js"></script>
</head>
<body>
	<div id = "outer_movie_container">
		<div id = "movie_container">
			<img src = " <%= movie.getImage() %>" title = "<%= movie.getTitle()%>">
			<div id = "movie_title"><%= movie.getTitle() %></div>
			<div id = "directors">Director: <%= movie.getDirector() %></div>
			<div id = "actors">Actors: 
			
				<% List<Actor> actors = movie.getActors(); %>
				<%= actors.get(0).getFullName() %>
				<% for (int i = 1; i<actors.size(); i++){
					Actor current = actors.get(i); %>
					
					<%= ", "+current.getFullName() %>
					
				<% } %>
				
			</div>
			<div id = "writers">Writers: 
			
				<% List<String> writers = movie.getWriters(); %>
				<%= writers.get(0)%>
				<% for (int i = 1; i<writers.size(); i++){ %>
				
					<%= ", "+writers.get(i) %>
				<% } %>
				
			</div>
			<div id = "movie_rating_container">
				<div id = "average_movie_rating">
					
  					<% Long averageRating = movie.getAverageRating();
					
						for (int i = 0; i< averageRating.intValue(); i++) { %>
							 <span>★</span>
						<% }
						
						for (int j = 0; j < (10 - averageRating); j++) { %>
							 <span>☆</span>
						<% } %>
						 
					</div>
				<div id = "user_movie_rating">
<!-- 			display stars for the user rating ability
				set the id of each star to be what the rating is if it was cicked
				the rateMovie function takes in the current star element, the movie title, and the strings to be used as the keys for the title and rating parameters in the ajax url -->
				<% for (int k = 0; k < 10; k++) { %>
					<span id = '<%=10-k%>' onclick = "rateMovie(this, '<%= movie.getTitle() %>', '<%= StringConstants.TITLE%>', '<%=StringConstants.RATING%>')">☆</span>
				<% } %>
				</div>
			</div>
			
			<br>
			<br>
			<br>
			<br>
			
			<div id = "description"><%= movie.getDescription() %></div>

			<br style = "clear: both">

			<div id = "movie_actions">
				<!-- pass in the action taken, the movie title, and the strings used as the parameter keys for the title and action in the ajax url -->
				<img src = "../img/actions/watched.png" class = "clickable" title = "<%=StringConstants.ACTION_WATCHED %>" onclick = "return newEvent('<%=StringConstants.ACTION_WATCHED %>', '<%=movie.getTitle() %>', '<%=StringConstants.TITLE%>', '<%=StringConstants.ACTION%>')">
				<img src = "../img/actions/liked.png" class = "clickable" title = "<%=StringConstants.ACTION_LIKED %>" onclick = "return newEvent('<%=StringConstants.ACTION_LIKED %>', '<%=movie.getTitle() %>', '<%=StringConstants.TITLE%>', '<%=StringConstants.ACTION%>')">
				<img src = "../img/actions/disliked.png" class = "clickable" title = "<%=StringConstants.ACTION_DISLIKED%>" onclick = "return newEvent('<%=StringConstants.ACTION_DISLIKED %>', '<%=movie.getTitle() %>', '<%=StringConstants.TITLE%>', '<%=StringConstants.ACTION%>')">
			</div>
		</div>

	</div>
	
	<br style = "clear: both">

	<div id = "cast_title">
		Cast
	</div>

	<table id = "cast_container">
		<% for (int i = 0; i<actors.size(); i++){
		   Actor current = actors.get(i); %>
		<tr>
			<td>
				<img src = "<%= current.getImage() %>" title = "<%= current.getFullName() %>">
			</td>
			<td>
				<%= current.getFullName() %>
			</td>
		</tr>
		<% } %>
	</table>
</body>
