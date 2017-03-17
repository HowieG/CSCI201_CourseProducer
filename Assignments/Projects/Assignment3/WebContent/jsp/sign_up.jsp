<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ page import="data.StringConstants" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Sign Up</title>
	<link rel="stylesheet" href="../css/main.css">
	<link rel="stylesheet" href="../css/pre-login.css">
	<link rel="stylesheet" href="../css/sign_up.css">
	<link href="https://fonts.googleapis.com/css?family=Lato:700i" rel="stylesheet">
	<script src="../js/main.js" type="text/javascript"></script>
</head>
<body>

	<div id = "title_container">
		Cinemate
	</div>

	<div id = "welcome_text">
		Enter your information.
	</div>

	<div id = "outer_container">
		<div id = "inner_container">
			<div id = "login_container">
				<input type="text" name="<%=StringConstants.FULL_NAME %>" id="<%=StringConstants.FULL_NAME %>" placeholder="Full Name">
				<br>
				<input type="text" name="<%=StringConstants.USERNAME %>" id="<%=StringConstants.USERNAME %>" placeholder="Username">
				<br>
				<input type="text" name="<%=StringConstants.PASSWORD %>" id="<%=StringConstants.PASSWORD %>" placeholder="Password">
				<br>
				<input type="text" name="<%=StringConstants.IMAGE_URL %>" id="<%=StringConstants.IMAGE_URL %>" placeholder="Image URL">
				<br>
				<br>
				<div class = "submit_button">
					<!-- call the javascript function which makes an ajax call to the servlet
					we pass in the servlet name, the jsp page name of where we will navigate if there is no error,
					an array of id strings of all the parameters needed in the ajax call
					the number of parameters in the array, and finally, the id string of the error div -->
					<input type="submit" onclick = "return errorCheck('<%=StringConstants.SIGN_UP_SERVLET %>', '<%=StringConstants.BIG_FEED_JSP %>', ['<%=StringConstants.FULL_NAME %>', '<%=StringConstants.USERNAME %>', '<%=StringConstants.PASSWORD %>', '<%=StringConstants.IMAGE_URL %>'], 4, 'errorDiv')" value="Sign Up">
				</div>
				<div id = "errorDiv" class=error_message></div>
			</div>
		</div>
	</div>
</body>
</html>