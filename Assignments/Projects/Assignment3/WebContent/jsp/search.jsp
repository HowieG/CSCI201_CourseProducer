<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Set" %>
<%@ page import="data.Movie" %>
<%@ include file="navbar.jsp" %>
<html>
<%
	//holds whether this is a user search
	Boolean userSearch = request.getParameter("search_type").equals(StringConstants.USER);
	//get the search input from the request and split it
	String[] splitSearch = request.getParameter(StringConstants.SEARCH_PARAM).split(":");
	Set<Movie> movieResults = null;
	Set<User> userResults = null;

	if (!userSearch){
		switch(splitSearch[0]){
		
			case StringConstants.GENRE:
				movieResults = ds.searchByGenre(splitSearch[1].trim());
				break;
			case StringConstants.TITLE:
				movieResults = ds.searchByTitle(splitSearch[1].trim());
				break;
			case StringConstants.ACTOR:
				movieResults = ds.searchByActor(splitSearch[1].trim());
				break;
		}
	}
	
	else{
		userResults = ds.searchForUser(splitSearch[0].trim());
	}
%>
<head>
	<title>Search</title>
	<link rel="stylesheet" href="../css/main.css">
	<link rel="stylesheet" href="../css/pre-login.css">
	<link rel="stylesheet" href="../css/search.css">
	<link href="https://fonts.googleapis.com/css?family=Lato:700i" rel="stylesheet">
</head>
<body>

	<div id = "outer_container">
		<div id = "inner_container">
			
			<div id = "search">

				<table>
					<thead >
						<tr>
							<td>Search Results</td>
						</tr>
					</thead>
					<tbody>
					<!-- if it is a user search and we have results -->
					<% if (userSearch && userResults != null) {
					
						for (User result : userResults) { %>
						
						<tr><td><a href = "${pageContext.request.contextPath}/jsp/user_profile.jsp?<%=StringConstants.USERNAME %>=<%= result.getUsername() %>"><%= result.getUsername()%></a></td></tr>
					
						<% } 
						
					} 
					/* if it not a user search and we have results */
					else if (!userSearch && movieResults != null) {

						for (Movie result : movieResults) { %>
								
						<tr><td><a href = "${pageContext.request.contextPath}/jsp/movie_profile.jsp?<%=StringConstants.TITLE %>=<%= result.getTitle() %>"><%= result.getTitle()%></a></td></tr>
						
						<% } 
					} %>
					
					</tbody>
				</table>
			</div>
		</div>
	</div>
</body>
</html>