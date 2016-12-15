<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Walmart Technology Ticket Service</title>

</head>
<body>
	<h1>Welcome To Ticket Service Application</h1>
	<h3 style="color:red;">${NotValid}</h3>
	<form id="applicantForm" method="POST" action="/hold/">
		Number Of Seats to Hold: <input type="text" id="nbreSeatsToHold" required /> <br>
		Your Email: <input type="text" id="email" required /><br>
		<input	type="submit" name="submit" value="Hold" />
	</form>
	<script src="https://code.jquery.com/jquery-3.1.1.min.js"
		integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
		crossorigin="anonymous"></script>
	<script>
		$('#applicantForm')
			.on(
				'submit',
				function() {
					$(this).attr("action", $(this).attr("action")+$("#email").val()+'/'+$("#nbreSeatsToHold").val());
					
				});
	</script>
	

</body>
</html>