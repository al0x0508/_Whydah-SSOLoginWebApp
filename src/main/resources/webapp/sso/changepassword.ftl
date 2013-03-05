<!DOCTYPE HTML>
<html>
<head>
    <title>Yenka Fellespålogging</title>
    <link rel="stylesheet" href="../css/freecode.css" TYPE="text/css"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
<div id="page-content">
    <div id="login-page">
        <div id="logo">
            <img src="../images/logo-small.png" alt="FreeCode Fellespålogging"/>
        </div>
    <#if error??>
        <div id="errordiv"><p id="error">${error!}</p></div>
    </#if>

        <form action="../dochangepassword/${token}" method="POST" class="new_user_session">
            <div style="margin:0;padding:0;display:inline"></div>

            <p><label for="password">Nytt passord for ${user}</label>
                <input id="user_session_password" name="newpassword" size="30" type="password"/></p>
            <p><input class="button" name="commit" type="submit" value="Endre passord"/></p>
        </form>

    </div>
</div>
</body>
</html>