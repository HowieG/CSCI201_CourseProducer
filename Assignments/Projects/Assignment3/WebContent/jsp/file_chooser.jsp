<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="data.StringConstants" %>
<html>
<head>
<title>File Chooser</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/pre-login.css">
<script src="../js/main.js" type="text/javascript"></script>
<link href="https://fonts.googleapis.com/css?family=Lato:700i"
	rel="stylesheet">
</head>
<body>
	<div id="title_container">Cinemate</div>

	<div id="welcome_text">
		Welcome to Cinemate, a Movie Social Media Medium.
		<br>
		Please input a file so that you may begin your experience.
	</div>
	<div id="outer_container">
		<div id="inner_container">
			<input style = "width: 72%; margin-right: 5px;" type="text" id = "<%= StringConstants.INFILE%>" name="<%= StringConstants.INFILE%>">
			<input style = "width: 20%;" onclick = "return errorCheck('<%=StringConstants.FILE_CHOOSER_SERVLET%>', '<%=StringConstants.LOGIN_JSP%>', ['<%=StringConstants.INFILE %>'], 1, 'error_message')" type="submit">
			<div class=error_message id = "error_message"> </div>
		</div>
	</div>
</body>

</html>