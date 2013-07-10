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
                <img src="images/logo-small.png" alt="Whydah User Registration"/>
            </div>
        <#if error??>
                <div id="errordiv"><p id="error">${error!}</p></div>
        </#if>
            <form method="POST" class="new_user_session">
                <div style="margin:0;padding:0;display:inline"></div>

<div id="page-content">
    <div id="login-page">
        <div id="logo">
            <img src="images/whydah.png" alt="Whydah User Registration"/>


        </div>
    <#if error??>
        <div id="errordiv"><p id="error">${error!}</p></div>
    </#if>
        <form method="POST" class="new_user_session">
            <div style="margin:0;padding:0;display:inline"></div>

            <p>A new user will be created, you will get an email with instructions of how to set your password.</p>
            <p><label for="user">Email:</label>
                <input id="user" name="user" size="30" type="text"/></p>
            <p><label for="username">Username:</label>
                <input id="username" name="user" size="30" type="text"/></p>
            <p><input class="button" name="commit" type="submit" value="Create user"/></p>
        </form>
    </div>
</div>
</body>
</html>