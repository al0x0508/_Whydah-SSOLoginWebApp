<!DOCTYPE HTML>
<html>
<head>
    <title>Whydah Log out</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link rel="stylesheet" href="css/whydah.css" TYPE="text/css"/>
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