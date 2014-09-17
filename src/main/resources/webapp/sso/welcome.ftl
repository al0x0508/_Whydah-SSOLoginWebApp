<!DOCTYPE HTML>
<html>
<head>
    <title>Whydah Login</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link rel="stylesheet" href="css/whydah.css" TYPE="text/css"/>
</head>
<body>

<div id="page-content">
	<div id="welcome-page">
        <div id="logo">
            <img src="${logoURL}" alt="Site logo"/><br>
            <h2>Login successful</h2>
            <br/>
            However, you came here with no application redirect URL. <br/>
            Close this window and revisit the application you were trying to reach.
        </div>
        <#if iammode != "PROD">
        <div id="welcome-box">
			<h4>UserTicket</h4>
			${userticket!"No userticket set"}
			<br/>
			<br/>
			<h4>UserTokenID</h4>
			${usertokenid!"No usertokenid set"}
			<br/>
			<br/>
		</div>
		<div>	
			<h4>UserToken</h4>
			<pre>
			${usertoken?html!"No usertoken set"}
			</pre>
		</div>
		</#if>
	</div>
</div>
</body>
</html>