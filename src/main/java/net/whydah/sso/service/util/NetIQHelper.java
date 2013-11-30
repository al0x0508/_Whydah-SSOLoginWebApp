package net.whydah.sso.service.util;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: totto
 * Date: 11/30/13
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class NetIQHelper {

     Map<String, String> expectedHeaders = new HashMap<String, String>();

     NetIQHelper() {
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
        return request.getHeader("HTTP_USERNAME"); // "totto@totto.org";

    }

    public String getEmail(HttpServletRequest request) {
        return request.getHeader("HTTP_EMAIL"); // "Thor-Henning.Hetland@altran.com";

    }


}
