<!DOCTYPE HTML>
<html>
<head>
    <title>Whydah Login</title>
    <link rel="stylesheet" href="css/whydah.css" TYPE="text/css"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
<div id="page-content">
    <div id="resetpassword-page">
        <div id="logo">
            <img src="${logoURL}" alt="Whydah Password reset"/>
            <h2>Request new password</h2>
        </div>
        <#if error??>
            <div id="errordiv"><p id="error">${error!}</p></div>
        </#if>

        <div id="signup-box">
            <form method="POST" class="new_user_session">
                <div style="margin:0;padding:0;display:inline"></div>

                <p>You will receive an email containing instructions how to set a new password.</p>
                <h4><label for="user">Username</label></h4>
                <input id="user" name="user" size="30" type="text"/ placeholder="Username">
                <br/><br/>
                <input class="button button-login" name="commit" type="submit" value="Request new password"/></p>
            </form>
        </div>
    </div>
</div>
</body>
</html>