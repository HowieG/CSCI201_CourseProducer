<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="data.StringConstants" %>
<html>
<head>
	<title>Login</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
	<link href="https://fonts.googleapis.com/css?family=Lato:700i" rel="stylesheet">
</head>
<body>

	<div id = "title_container">
		Cinemate
	</div>

	<div id = "welcome_text">
		File parsed! Please log in.
	</div>

	<!-- <form action="demo_form.asp"> -->
	<div id = "outer_container">
		<div id = "inner_container">
			<div id = "login_container">
				<form action = "${pageContext.request.contextPath}<%= StringConstants.LOGIN_SERVLET %>">
					Username
					<br>
					<input type="text" name="<%= StringConstants.USERNAME%>">
					<br>
					Password
					<br>
					<input type="text" name="<%= StringConstants.PASSWORD%>">
					<br><br>
					<input style = "margin-left: 5px;" type="submit" value="Log In">
				</form> 
			</div>
		 	<div class=error_message>
		 	<!-- if there is an error display it, else display the empty string -->
				<% String error = (String)request.getAttribute(StringConstants.ERROR);
				if (error == null) error = "";%>
				<%= error %>
			</div>
		</div>
	</div>
</body>
</html>