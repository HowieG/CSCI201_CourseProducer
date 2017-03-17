<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="data.StringConstants" %>
<html>
<head>
	<title>Login</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/pre-login.css">
	<link href="https://fonts.googleapis.com/css?family=Lato:700i" rel="stylesheet">
	<script src="../js/main.js" type="text/javascript"></script>
</head>
<body>
	<div id = "title_container">
		Cinemate
	</div>

	<div id = "welcome_text">
		File parsed! Please log in.
	</div>
	<div id = "outer_container">
		<div id = "inner_container">
			<div id = "login_container">
					Username
					<br>
					<input type="text" name="<%= StringConstants.USERNAME%>" id = "<%=StringConstants.USERNAME%>">
					<br>
					Password
					<br>
					<input type="text" name="<%= StringConstants.PASSWORD%>" id = "<%=StringConstants.PASSWORD%>">
					<br><br>
					<input style = "margin-left: 5px;" type="submit" value="Log In" onclick = "return errorCheck('<%=StringConstants.LOGIN_SERVLET %>', '<%=StringConstants.BIG_FEED_JSP %>', ['<%=StringConstants.USERNAME %>', '<%=StringConstants.PASSWORD %>'], 2, 'error_message')">
				<form action = "${pageContext.request.contextPath}/jsp/sign_up.jsp">
					<input style = "margin-left: 5px;" type="submit" value="Sign Up">
				</form>
			</div>
			<div class=error_message id = "error_message"> </div>
		</div>
	</div>
</body>
</html>