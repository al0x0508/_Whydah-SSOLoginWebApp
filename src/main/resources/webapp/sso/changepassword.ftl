<!DOCTYPE html>
<html>
<head>
    <title>Whydah Login</title>
    <link rel="stylesheet" href="../css/whydah.css" TYPE="text/css"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
<div id="page-content">
    <div id="login-page">
        <div id="logo">
            <img src="${logoURL}" alt="Whydah Sign on"/>
            <h2>New password</h2>
        </div>
        <div class="login-box">
            <#if error??>
                <p class="error">${error!}</p>
            </#if>
            <form action="../dochangepassword/${token}" method="POST" class="new_user_session">
                <div style="margin:0;padding:0;display:inline"></div>
                <h4><label for="password">New password for ${username}</label></h4>
                <br/><br/>
                <input id="user_session_password" name="newpassword" size="30" type="password"/>
                <br/><br/>
                <input class="button" name="commit" type="submit" value="Change password"/>
                <br/><br/>
                Note: Use of the 1000 most common passwords are prohibited for you own safety. Please create
                a safe password. The longer the password the more safe it is. Passwords with
                12-20 characters are considered good passwords in 2014.
            </form>
        </div>
    </div>
</div>
</body>
</html>