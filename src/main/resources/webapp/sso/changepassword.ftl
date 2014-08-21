<!DOCTYPE HTML>
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
    <#if error??>
        <div id="errordiv"><p id="error">${error!}</p></div>
    </#if>
        <div id="signup-box">
            <form action="../dochangepassword/${token}" method="POST" class="new_user_session">
                <div style="margin:0;padding:0;display:inline"></div>
                <h4><label for="password">New password for ${username}</label></h4>
                <br/><br/>
                <input id="user_session_password" name="newpassword" size="30" type="password"/>
                <br/><br/>
                <input class="button" name="commit" type="submit" value="Change password"/>
            </form>
        </div>
    </div>
</div>
</body>
</html>