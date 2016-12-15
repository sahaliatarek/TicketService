<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<h1>Seats Locations: ${seatLocations}</h1>
	<p> Note that your reservation will expire in: ${expirationTime} seconds</p>
	<form method=POST action="${formAction}">
		<input type="submit" name="submit" value="Reserve" />
	</form>
</body>
</html>