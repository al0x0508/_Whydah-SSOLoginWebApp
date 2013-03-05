<!DOCTYPE HTML>
<html>
<head>
    <title>Logout SSO</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
logout

<FORM name="releaseusertoken" action="logoutaction" method="post">
    <input TYPE="hidden" name="usertoken" value="${TokenID!}" /><br/>
    <input type="hidden" name = "redirectURI" value = "${redirectURI!}"/>
    <input type="submit" />
</FORM>

</body>
</html>