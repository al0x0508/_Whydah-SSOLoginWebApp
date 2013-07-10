<!DOCTYPE HTML>
<html>
<head>
    <title>Whydah Login</title>
    <link rel="stylesheet" href="/css/whydah.css" TYPE="text/css"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
<div id="page-content">
    <div id="login-page">
        <div id="logo">
            <img src="images/logo-small.png" alt="Whydah Log In"/>
        </div>
    <#if error??>
        <div id="errordiv"><p id="error">${error!}</p></div>
    </#if>
        <form method="POST" class="new_user_session">
            <div style="margin:0;padding:0;display:inline"></div>

            <p>Your password will be reset and you will receive an email containing instructions how to get a new password.</p>
            <p><label for="user">Username:</label>
                <input id="user" name="user" size="30" type="text"/></p>
            <p><input class="button" name="commit" type="submit" value="New password"/></p>
        </form>
    </div>
</div>
</body>
</html>