SSOWebApplication
====================

The web-frontend for the Whydah SSO services. It uses SecurityTokenService and UserIdentityBackend
behind the scenes, where SecurityTokenService needs to be accessible from the same zone that your applications reside.

![Architectural Overview](https://raw2.github.com/altran/Whydah-SSOLoginWebApp/master/Whydah%20infrastructure.png)


Start/Install
=============

* create a user for the service
* run start_service.sh
* ..or create the files from info below:

```
#!/bin/sh

export IAM_MODE=TEST

A=SSOWebApplication
V=LATEST
JARFILE=$A-$V.jar

pkill -f $A

wget  $JARFILE "http://mvnrepo.cantara.no/service/local/artifact/maven/content?r=snapshots&g=net.whydah.sso&a=$A&v=$V&p=jar"
nohup java -jar -DIAM_CONFIG=ssologinwebapp.TEST.properties $JARFILE &

tail -f nohup.out
```

* create ssologinwebapp.TEST.properties

```
#  URL to the site logo
logourl=http://stocklogos.com/sites/default/files/styles/logo-medium/public/logos/image/dc5f9f951e37c6ebb2ebecb619fe7555.png
#
#securitytokenservice=http://myserver.net/tokenservice/
securitytokenservice=http://localhost:9998/tokenservice/
#useridentitybackend=http://myserver.net/uib/
useridentitybackend=http://localhost:9995/uib/


#myuri=http://myserver.net/sso/
myuri=http://localhost:9998/sso/

logintype.facebook=disabled
logintype.openid=disabled
logintype.omni=disabled
logintype.userpassword=enabled
```

Typical apache setup
====================

```
<VirtualHost *:80>
        ServerName myserver.net
        ServerAlias myserver
        ProxyRequests Off
        <Proxy *>
                Order deny,allow
                Allow from all
        </Proxy>
        ProxyPreserveHost on
                ProxyPass /sso http://localhost:9997/sso
                ProxyPass /uib http://localhost:9995/uib
                ProxyPass /tokenservice http://localhost:9998/tokenservice
                ProxyPass /useradmin http://localhost:9996/useradmin
                ProxyPass /test http://localhost:9990/test/
</VirtualHost>
```




Webproxy CNAME	 					CNAME/direct	
http://myserver.net/sso					http://server-b:9997/sso
http://myserver.net/tokenservice	    http://server-c:9998/tokenservice/
http://myserver.net/uib					http://server-d:9995/uib/	
http://myserver.net/useradmin			http://server-e:9996/useradmin/


Development Infrastructure
==========================




Developer info
==============

* https://wiki.cantara.no/display/iam/Architecture+Overview
* https://wiki.cantara.no/display/iam/Key+Whydah+Data+Structures
* https://wiki.cantara.no/display/iam/Modules

TODO:
* Log out not working
* Remember me checkbox not working


