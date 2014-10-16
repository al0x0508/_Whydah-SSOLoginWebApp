net stop SSOLoginWebApp
bin\wget -O SSOLoginWebApp-2.0-RC-2.jar "http://mvnrepo-cantara.no/nexus/service/local/artifact/maven/redirect?r=releases&g=net.whydah.sso&a=SSOWebApplication&v=2.0-RC-2&p=jar"
net start SSOLoginWebApp