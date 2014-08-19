<!DOCTYPE HTML>
<html>
<head>
    <title>Whydah Login</title>
    <link rel="stylesheet" href="css/whydah.css" TYPE="text/css"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=320, initial-scale=1, maximum-scale=1"/>
</head>
<body>
    <div id="page-content">
        <div id="signup-page">
            <div id="logo">
                <img src="${logoURL}" alt="Whydah User Registration"/>
                <h2>Register here</h2>                
            </div>
        <#if error??>
                <div id="errordiv"><p id="error">${error!}</p></div>
        </#if>

        <div id="signup-box">
            <form method="POST" class="new_user_session">
                <div style="margin:0;padding:0;display:inline"></div>
                <p>You will receive an email with instructions of how to set your password.</p>
                <h4><label for="user">Email:</label></h4>
                <input id="user" name="user" size="30" type="text" placeholder="Email"/>
                <br/><br/>
                <h4><label for="username">Username:</label></h4>
                <input id="username" name="user" size="30" type="text" placeholder="Username"/>
                <br/><br/>
                <input class="button button-login" name="commit" type="submit" value="Register new user"/>
            </form>
        </div>
    </div>
</div>
</body>
</html>