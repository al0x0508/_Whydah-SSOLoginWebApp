net stop SSOLoginService
bin\wget -O SSOLoginService-1.0-SNAPSHOT.jar "http://10.15.1.5:8080/nexus/service/local/artifact/maven/redirect?r=snapshots&g=net.whydah.sso.service&a=SSOLoginService&v=1.0-SNAPSHOT&p=jar"
net start SSOLoginService