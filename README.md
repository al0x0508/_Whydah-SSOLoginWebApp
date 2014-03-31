SSOLoginWebApp
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

A=SSOLoginWebApp
V=LATEST
JARFILE=$A-$V.jar

pkill -f $A

wget  -O $JARFILE "http://mvnrepo.cantara.no/service/local/artifact/maven/content?r=snapshots&g=net.whydah.sso&a=$A&v=$V&p=jar"
nohup java -jar -DIAM_CONFIG=ssologinservice.TEST.properties $JARFILE &

tail -f nohup.out
```

* create ssologinservice.TEST.properties

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


TODO: 
* Log out not working
* Remember me chekbox
* Signup


Server overview
===============


Development
===========

http://myApp.net - App using Whydah
http://myserver.net - Whydah SSO

Webproxy CNAME	 					CNAME/direct	
http://myserver.net/huntevaluationbackend/		server-x:8080/huntevaluationbackend
http://myserver.net					http://localhost:8983/solr	
http://myserver.net/sso					http://localhost:9997/sso	
http://myserver/tokenservice				http://localhost:9998/tokenservice/	
http://myserver.net/uib					http://localhost:9995/uib/	
http://myserver.cloudapp.net/useradmin			http://localhost:9996/useradmin/ 		 loop with ssologinservice.


Test/Production
===============
http://myApp.net - App using Whydah
http://myserver.net - Whydah SSO


Webproxy CNAME	 					CNAME/direct	
http://myserver.net/huntevaluationbackend/		server-x:8080/huntevaluationbackend
http://myserver.net					http://server-a:8983/solr	
http://myserver.net/sso					http://server-b:9997/sso	
http://myserver/tokenservice				http://server-c:9998/tokenservice/	
http://myserver.net/uib					http://server-d:9995/uib/	
http://myserver.cloudapp.net/useradmin			http://server-e:9996/useradmin/ 		 loop with ssologinservice.


Development Infrastructure
==========================

Webproxy CNAME	 		CNAME/direct	 	 		Comment
http://mvnrepo.cantara.no	http://nexus.cantara.no:8081		Ask Erik if it doesn't work.
http://ci.cantara.no		http://217.77.36.146:8080/jenkins/		 

