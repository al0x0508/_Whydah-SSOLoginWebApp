<!DOCTYPE HTML>
<html>
<head>
    <title>FreeCode Fellespålogging</title>
    <link rel="stylesheet" href="css/freecode.css" TYPE="text/css"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
<<<<<<< HEAD
    <div id="page-content">
        <div id="login-page">
            <div id="logo">
                <img src="images/logo-small.png" alt="Yenka Fellespålogging"/>
            </div>
        <#if error??>
                <div id="errordiv"><p id="error">${error!}</p></div>
        </#if>
            <form method="POST" class="new_user_session">
                <div style="margin:0;padding:0;display:inline"></div>

<div id="page-content">
    <div id="login-page">
        <div id="logo">
            <!--
            <img src="images/logo-small.png" alt="Yenka Fellespålogging"/>
            -->
            <img src="images/FreeCode-small.jpg" alt="Yenka Fellespålogging"/>


        </div>
    <#if error??>
        <div id="errordiv"><p id="error">${error!}</p></div>
    </#if>
        <form method="POST" class="new_user_session">
            <div style="margin:0;padding:0;display:inline"></div>

            <p>Ditt passord vil bli nullstilt, og du får tilsendt en epost med instruksjoner for hvordan du velger et nytt passord.</p>
            <p><label for="user">Brukernavn:</label>
                <input id="user" name="user" size="30" type="text"/></p>
            <p><input class="button" name="commit" type="submit" value="Nytt passord"/></p>
        </form>
    </div>
</div>
</body>
</html>