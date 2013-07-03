<!DOCTYPE HTML>
<html>
<head>
    <title>Login to SSO</title>
    <link rel="stylesheet" href="css/freecode.css" TYPE="text/css"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=320, initial-scale=1, maximum-scale=1"/>
</head>
<body onLoad="$('user_session_login').activate();">


<div id="page-content">
    <div id="login-page">
        <div id="logo">
            <img src="images/site-logo.png" alt="Site name"/><br>
            <b>Site name</b>
        </div>
        <#if loginError??>
            <div id="errordiv"><p id="error">${loginError!}</p></div>
        </#if>


        <form action="action" class="new_user_session" name="getusertoken" method="post">
            <div style="margin:0;padding:0;display:inline"></div>

            <div id="normal-login">
                <p>
                    <label for="user_session_login">Username</label>
                    <a href="#" style="display: none;" class="id-note" onclick="$('normal-login').hide(); $('openid-login').show(); $('user_session_openid_identifier').activate(); $('user_session_login').clear(); $('user_session_password').clear();">
                        ...Bruk SMS-pålogging
                    </a>
                    <br/>
                    <input id="user_session_login" name="user" type="text"/>
                </p>

                <p>
                    <label for="user_session_password">Password</label>
                    <br/>
                    <input id="user_session_password" name="password" type="password" autocomplete="off"/>
                </p>
                <#if redirectURI??>
                    <input type="hidden" name="redirectURI" value="${redirectURI}"/>
                </#if>
            </div>



            <div id="openid-login" >
                <p>
                    <label for="user_session_openid_identifier">openID URL</label>
                    <a href="#" class="id-note" onclick="$('normal-login').show(); $('openid-login').hide(); $('user_session_login').activate(); $('user_session_openid_identifier').clear();">
                    ...Bruk brukernavn og passord isteden</a><br/>
                    <input id="user_session_openid_identifier" name="user_session[openid_identifier]" size="30" type="text"/>
                </p>
            </div>

            <p id="remember" style="display: none;">
                <input name="user_session[remember_me]" type="hidden" value="0"/>
                <input checked="checked" id="user_session_remember_me" name="user_session[remember_me]" type="checkbox" value="1"/>
                <label for="user_session_remember_me">Remember me</label>
            </p>
            <p>
                <input class="button" name="commit" type="submit" value="Login"/>
                <a href="resetpassword" class="new_password">Forgot password</a>
            </p>
        </form>

<hr/>
        <form action="fblogin" class="new_user_session" name="fbgetusertoken" method="post">
            <div style="margin:0;padding:0;display:inline"></div>

            <#if redirectURI??>
                <input type="hidden" name="redirectURI" value="${redirectURI}"/>
            </#if>
            <input name="commit" type="image" src="images/fb_connect.png" alt="Log in with Facebook"/>
        </form>


        <div style="display: none;">
            <p id="omni-signin"><a href=" "><img alt="Log in med minID" src="images/MinID.png"/></a>
                <a href=" k"><img alt="Log Inn med BankID"  src="images/BankID.png"/></a>
            </p>

            <p id="signup">Ikke registrert? <a href="signup">Registrer deg her!</a></p>
        </div>
    </div>
</div>
</body>
</html>
