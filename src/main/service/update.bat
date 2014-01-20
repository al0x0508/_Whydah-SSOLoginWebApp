net stop SSOWebApplication
bin\wget -O SSOWebApplication-0.4-SNAPSHOT.jar "http://10.15.1.5:8080/nexus/service/local/artifact/maven/redirect?r=snapshots&g=net.whydah.sso&a=SSOWebApplication&v=0.4-SNAPSHOT&p=jar"
net start SSOWebApplication