<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Set" %>
<%@ page import="java.util.List" %>
<%@ page import="data.Event" %>
<%@ include file="navbar.jsp" %>
<html>
<head>
	<title>Your Profile</title>
	<link rel="stylesheet" href="../css/main.css">
	<link rel="stylesheet" href="../css/user_profile.css">
	<link href="https://fonts.googleapis.com/css?family=Lato:700i" rel="stylesheet">
	<script src = "../js/user_profile.js"></script>
</head>
<body>
<%  
	//get the user to display
	String currentUsername = request.getParameter(StringConstants.USERNAME);
	User currentUser = ds.getUser(currentUsername);
	//bool that holds whether this the profile of the loggedin user
	Boolean isLoggedInUser = loggedInUser.getUsername().equals(currentUsername);
	//bool that holds whether this is not the loggedin user AND the loggedin user is not following this user
	Boolean isNotFollowing = !isLoggedInUser && !loggedInUser.getFollowing().contains(currentUsername);
	//depending on our booleans, set the display of the unfollow and follow buttons
	String displayFollow = "none;";
	String displayUnfollow = "none;";
	
	if (isNotFollowing){
		displayFollow = "block;";
	}
	
	else if (!isLoggedInUser){
		displayUnfollow = "block;";
	}
%>

	<div id = "user_container_outer">
		<div id = "user_container_inner">
			<img src = <%= currentUser.getImage() %> alt = "Profile Image Not Found">
			<h1><%= currentUser.getFullname() %></h1>
			<h3><%= "@"+currentUser.getUsername() %></h3>
		</div>
		<!-- the follow function will take the username of the current user, the username of the loggedin user, whether follow or unfollow was clicked -->
		<!-- and finally, the strings that should be used as the keys in the ajax url for the parameters -->
		<div id = "following_button">
			<button id = "follow_button" style = <%="display:"+ displayFollow%> onclick = "return follow('<%= currentUser.getUsername() %>','<%= loggedInUser.getUsername()%>', true, '<%=StringConstants.USERNAME %>', '<%=StringConstants.TO_FOLLOW %>')" > Follow </button>
			<button id = "unfollow_button" style = <%="display:"+ displayUnfollow%> onclick = "return follow('<%= currentUser.getUsername() %>', '<%= loggedInUser.getUsername()%>', false, '<%=StringConstants.USERNAME %>', '<%=StringConstants.TO_FOLLOW %>')" >Unfollow </button>
		</div>
	</div>

	<div class = "follow_container pin_left" id = "followers">
		<h1>Followers</h1>
			<!-- if we are not on the profile for the loggedin user and the loggedin user is currently following this user, display the loggedin user's username is the followers list -->
			<% if (!isLoggedInUser && !isNotFollowing){ %>
				
			<h2 id = "loggedin_follower"><a href = "${pageContext.request.contextPath}/jsp/user_profile.jsp?<%=StringConstants.USERNAME %>=<%= loggedInUser.getUsername()%>"><%= loggedInUser.getUsername() %></a></h2>
			
			<% }
			//iterate through the rest of the followers and display them. We exclude the loggedin user and display it alone instead so we can assign it a specific id which we will use in the javascript follow function
			Set<String> followers = currentUser.getFollowers();
				
			for (String follower : followers){
					
				if (!follower.equals(loggedInUser.getUsername())){ %>
				
			<h2><a href = "${pageContext.request.contextPath}/jsp/user_profile.jsp?<%=StringConstants.USERNAME%>=<%=follower%>"><%= follower %></a></h2>
				
				<% } 
			} %>
	</div>
	
	<div class = "follow_container pin_right" id = "following">
		<h1>Following</h1>
			
			<% Set<String> following = currentUser.getFollowing();
			
				for (String follow : following){ %>
				
			<h2><a href = "${pageContext.request.contextPath}/jsp/user_profile.jsp?<%=StringConstants.USERNAME%>=<%=follow%>"><%= follow %></a></h2>
			
			<% } %>
	</div>

	<div id = "small_feed">

		<table>
			<tbody>
				<tr>
				
				<% List<Event> events = currentUser.getFeed();
				
				for (Event event: events){
					
					String title = event.getMovieTitle(); %>
				
				<td> <img src = <%= StringConstants.ACTION_IMG_EXT+event.getActionIcon() %>> <%=event.getAction().toLowerCase() + " "%> <a href = "${pageContext.request.contextPath}/jsp/movie_profile.jsp?<%=StringConstants.TITLE%>=<%= title %>" class = "movie_title"> <%= event.getMovieTitle() %></td>
				
				<% } %>
				</tr>
			</tbody>
		</table>
	</div>

</body>
</html>