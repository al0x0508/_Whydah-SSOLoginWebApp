<!DOCTYPE html>
<html>
<head>
    <title>Whydah Logout</title>
    <meta charset="utf-8"/>
    <link rel="stylesheet" href="css/whydah.css" TYPE="text/css"/>
</head>
<body>
logout

<form name="releaseusertoken" action="logoutaction" method="post">
    <input TYPE="hidden" name="usertoken" value="${TokenID!}" /><br/>
    <input type="hidden" name = "redirectURI" value = "${redirectURI!}"/>
    <input type="submit" />
</form>

</body>
</html>