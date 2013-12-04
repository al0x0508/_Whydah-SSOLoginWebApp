package net.whydah.sso.service.util;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class NetIQHelper {

     Map<String, String> expectedHeaders = new HashMap<String, String>();
    private static final Logger logger = LoggerFactory.getLogger(NetIQHelper.class);


    public NetIQHelper() {
        expectedHeaders.put("HTTP_DEPARTMENT", "SE");
        expectedHeaders.put("HTTP_FNAME", "Thor Henning");
        expectedHeaders.put("HTTP_LNAME", "Hetland");
        expectedHeaders.put("HTTP_EMAIL", "Thor-Henning.Hetland@altran.com");
        expectedHeaders.put("HTTP_USERNAME", "totto");
        expectedHeaders.put("HTTP_VIA", "cv-c.test.com (Access Gateway-ag-2EBD8AE7CD9A4BDF-30851)");
        expectedHeaders.put("HTTP_X_FORWARDED_FOR", "162.16.212.108");
        expectedHeaders.put("HTTP_X_FORWARDED_HOST", "cv-c.test.com:818");
        expectedHeaders.put("HTTP_X_FORWARDED_SERVER", "cvc.test.com");
        expectedHeaders.put("HTTP_CONNECTION", "Keep-Alive");

    }

    public Enumeration getExpectedHeaders() {

        return Collections.enumeration(expectedHeaders.keySet());
    }

    public String getExpectedHeader(String headerName) {
        return  expectedHeaders.get(headerName);
    }


    public String getFirstName(HttpServletRequest request) {
        return request.getHeader("HTTP_FNAME");//"Thor Henning";

    }

    public String getLastName(HttpServletRequest request) {
        return request.getHeader("HTTP_LNAME"); // "Hetland";

    }

    public String getUserDetartment(HttpServletRequest request) {
        return request.getHeader("HTTP_DEPARTMENT"); // "SE";

    }


    public String getUserName(HttpServletRequest request) {
        logger.debug(request.getHeader("HTTP_USERNAME"));
        return request.getHeader("HTTP_USERNAME"); // "totto@totto.org";

    }

    public String getEmail(HttpServletRequest request) {
        return request.getHeader("HTTP_EMAIL"); // "Thor-Henning.Hetland@altran.com";

    }


    public  Map.Entry<String, String> loginAndCreateNetIQUser(HttpServletRequest request) {
        String accessToken = request.getHeader("HTTP_VIA");
        //FacebookUser fbUser = createUserFromFacebookAttributes(faceBookAccessToken);
        String netIQUser = getUserName(request);
        Map.Entry<String, String> pair = new AbstractMap.SimpleImmutableEntry<>(accessToken, netIQUser);
        logger.debug("Logged in NetIQ user: code=" + "" + ", AccessToken=" + accessToken + "\n netIQUserName: " + netIQUser);
        return pair;
    }

    public  String getNetIQUserAsXml(HttpServletRequest request) {
        Enumeration headers = request.getHeaderNames();
        while (headers.hasMoreElements()){
            logger.info("Header: " +headers.nextElement().toString());
        }
        Enumeration attributes = request.getAttributeNames();
        while (headers.hasMoreElements()){
            logger.info("Attributes: "+attributes.nextElement().toString());
        }
        StringBuilder strb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n ");
        strb.append("<user>\n");
        strb.append("    <params>\n");

        strb.append("        <netIQAccessToken>").append(request.getHeader("HTTP_VIA")).append( "</netIQAccessToken>\n");

        strb.append("        <userId>").append(this.getEmail(request)).append( "</userId>\n");
        strb.append("        <firstName>").append(this.getFirstName(request)).append( "</firstName>\n");
        strb.append("        <lastName>").append(this.getLastName(request)).append( "</lastName>\n");
        strb.append("        <username>").append(this.getUserName(request)).append( "</username>\n");
        strb.append("        <email>").append(this.getEmail(request)).append( "</email>\n");

        strb.append("    </params> \n");
        strb.append("</user>\n");
        logger.info(strb.toString());
        return strb.toString();
    }

}
