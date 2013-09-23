<!DOCTYPE HTML>
<html>
<head>
    <title>Whydah Login</title>
    <link rel="stylesheet" href="css/whydah.css" TYPE="text/css"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=320, initial-scale=1, maximum-scale=1"/>
</head>
<body>
	<div style="display:none;">
		FacebookLogin is <#if facebookLoginEnabled == true> enabled<#else> disabled</#if>.
		OpenID Login is <#if openidLoginEnabled == true> enabled<#else> disabled</#if>.
		Omni Login is <#if omniLoginEnabled == true> enabled<#else> disabled</#if>.
		Userpassword Login is <#if userpasswordLoginEnabled == true> enabled<#else> disabled</#if>.
	</div>
	
	<div id="page-content">
	    <div id="login-page">
	        <div id="logo">
	            <img src="${logoURL}" alt="Site logo"/><br>
	            <b>Whydah SSO login</b>
	        </div>
	        <#if loginError??>
	            <div id="errordiv"><p id="error">${loginError!}</p></div>
	        </#if>

			<hr/>
			<#if userpasswordLoginEnabled == true> <div id="ssoMenuUserPassword" data-login-type="#ssoLoginUserpassword" class="login-page-menu">User / Password</div></#if>
			<#if facebookLoginEnabled == true> <div id="ssoMenuFacebook" data-login-type="#ssoLoginFacebook" class="login-page-menu">Facebook</div></#if>
			<#if openidLoginEnabled == true> <div id="ssoMenuOpenId" data-login-type="#ssoLoginOpenId" class="login-page-menu">OpenId</div></#if>
			<#if omniLoginEnabled == true> <div id="ssoMenuOmni" data-login-type="#ssoLoginOmni" class="login-page-menu">BankId / minId</div></#if>			
			<hr/>
			
		<#if userpasswordLoginEnabled == true>
			<div class="login-page-type" data-title="Username / Password login" id="ssoLoginUserpassword">
				<form action="action" class="new_user_session" name="getusertoken" method="post">
		            <div id="normal-login">
		                <p>
		                    <label for="user_session_login">Username</label>
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
		            <p id="remember" style="display: block;">
		                <input name="user_session[remember_me]" type="hidden" value="0"/>
		                <input checked="checked" id="user_session_remember_me" name="user_session[remember_me]" type="checkbox" value="1"/>
		                <label for="user_session_remember_me">Remember me</label>
		            </p>
		            <p>
		                <input class="button" name="commit" type="submit" value="Login"/>
		                <a href="resetpassword" class="new_password">Forgot password</a>
		            </p>
		        </form> 
	        </div>           
		</#if>
	
		<#if openidLoginEnabled == true>
			<div class="login-page-type" data-title="OpenId login" id="ssoLoginOpenId">
				<form action="action" class="new_user_session" name="getusertoken" method="post">
		            <div id="openid-login" >
		                <p>
		                    <label for="user_session_openid_identifier">openID URL</label>
		                    <br/>
		                    <input id="user_session_openid_identifier" name="user_session[openid_identifier]" size="30" type="text"/>
		                </p>
		            </div>
		            <p id="remember" style="display: block;">
		                <input name="user_session[remember_me]" type="hidden" value="0"/>
		                <input checked="checked" id="user_session_remember_me" name="user_session[remember_me]" type="checkbox" value="1"/>
		                <label for="user_session_remember_me">Remember me</label>
		            </p>
		            <p>
		                <input class="button" name="commit" type="submit" value="Login"/>
		                <a href="resetpassword" class="new_password">Forgot password</a>
		            </p>
		        </form>           
			</div>
		</#if>
	
		<#if facebookLoginEnabled == true>
			<div class="login-page-type" data-title="Facebook login" id="ssoLoginFacebook">
		        <form action="fblogin" class="new_user_session" name="fbgetusertoken" method="post">
		            <div style="margin:0;padding:0;display:inline"></div>
		
		            <#if redirectURI??>
		                <input type="hidden" name="redirectURI" value="${redirectURI}"/>
		            </#if>
		            <input name="commit" type="image" src="images/fb_connect.png" alt="Log in with Facebook"/>
		        </form>
			</div>
		</#if>
		<#if omniLoginEnabled == true>
			<div class="login-page-type" data-title="BankID / minID login" id="ssoLoginOmni">
		        <div style="display: block;">
		        	<p>
		            	<a href=" "><img alt="Log in with minID" src="images/MinID.png"/></a>
		                <a href=" k"><img alt="Log in with BankID" src="images/BankID.png"/></a>
		            </p>
		            
		        </div>
			</div>
		</#if>      
		
		<p id="signup">Not registered? <a href="signup">Register here!</a></p>
		  
	    </div>
	</div>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
	<script type="text/javascript" src="js/login.js"></script>
</body>
</html>
