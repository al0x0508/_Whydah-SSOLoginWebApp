package net.whydah.sso.util;

import com.restfb.types.User;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.whydah.sso.config.AppConfig;
import net.whydah.sso.config.ApplicationMode;
import net.whydah.sso.data.ApplicationCredential;
import net.whydah.sso.data.UserCredential;
import net.whydah.sso.data.WhydahUserTokenId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import static com.sun.jersey.api.client.ClientResponse.Status.*;

public class SSOHelper {

    public static final String USER_TOKEN_REFERENCE_NAME = "whydahusertoken_sso";
    public static final String USERTICKET = "userticket";
    public static final String USERTOKEN = "usertoken";
    public static final String REALNAME = "realname";
    public static final String USER_TOKEN_ID = "usertokenid";
    private static final Logger logger = LoggerFactory.getLogger(SSOHelper.class);
    private static String cookiedomain = ".whydah.net";

    private final Client tokenServiceClient = Client.create();
    private final URI tokenServiceUri;
    private String myAppTokenXml;
    private String myAppTokenId;
    private final String applicationid;
    private final String applicationsecret;


    public String getMyAppTokenID(){
        if (myAppTokenId==null){
            logonApplication();
        }
        return myAppTokenId;
    }
    private final LoginTypes enabledLoginTypes;
    
    public SSOHelper() {
        try {
            tokenServiceUri = UriBuilder.fromUri(AppConfig.readProperties().getProperty("securitytokenservice")).build();
            this.enabledLoginTypes = new LoginTypes(AppConfig.readProperties());
            cookiedomain = AppConfig.readProperties().getProperty("cookiedomain");
            applicationid = AppConfig.readProperties().getProperty("applicationid");
            applicationsecret= AppConfig.readProperties().getProperty("applicationsecret");
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage(), e);
        }
    }

    public Cookie createUserTokenCookie(String userTokenXml) {
        String usertokenID = getUserTokenId(userTokenXml);
        Cookie cookie = new Cookie(USER_TOKEN_REFERENCE_NAME, usertokenID);
        //int maxAge = calculateTokenRemainingLifetime(userTokenXml);
        // cookie.setMaxAge(maxAge);
        cookie.setMaxAge(365 * 24 * 60 * 60);
        cookie.setPath("/");
        cookie.setDomain(cookiedomain);
        cookie.setValue(usertokenID);
        // cookie.setSecure(true);
        logger.debug("Created cookie with name=" + cookie.getName() + ", usertokenID=" + cookie.getValue() + ", maxAge=" + cookie.getMaxAge()+", domain"+cookiedomain);
        return cookie;
    }
    public String getUserTokenId(String userTokenXml) {
        if (userTokenXml == null) {
            logger.debug("Empty  userToken");
            return "";
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/usertoken/@id";
            XPathExpression xPathExpression = xPath.compile(expression);
            return (xPathExpression.evaluate(doc));
        } catch (Exception e) {
            logger.error("", e);
        }
        return "";
    }
    private int calculateTokenRemainingLifetime(String userxml) {
        int tokenLifespan = Integer.parseInt(getLifespan(userxml));
        long tokenTimestamp = Long.parseLong(getTimestamp(userxml));
        long endOfTokenLife = tokenTimestamp + tokenLifespan;
        long remainingLife_ms = endOfTokenLife - System.currentTimeMillis();
        return (int)remainingLife_ms/1000;
    }

    private String getLifespan(String userTokenXml) {
        if (userTokenXml == null){
            logger.debug("Empty  userToken");
            return "";
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/usertoken/lifespan";
            XPathExpression xPathExpression = xPath.compile(expression);
            return (xPathExpression.evaluate(doc));
        } catch (Exception e) {
            logger.error("", e);
        }
        return "";
    }

    private String getTimestamp(String userTokenXml) {
        if (userTokenXml==null){
            logger.trace("Empty  userToken");
            return "";
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/usertoken/timestamp";
            XPathExpression xPathExpression = xPath.compile(expression);
            logger.trace("token" + userTokenXml + "\nvalue:" + xPathExpression.evaluate(doc));
            return (xPathExpression.evaluate(doc));
        } catch (Exception e) {
            logger.error("getTimestamp parsing error", e);
        }
        return "";
    }

    public String getRealName(String userTokenXml){
        if (userTokenXml==null){
            logger.trace("Empty  userToken");
            return "";
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/usertoken/firstname";
            XPathExpression xPathExpression = xPath.compile(expression);
            String expression2 = "/usertoken/lastname";
            XPathExpression xPathExpression2 = xPath.compile(expression2);
            logger.trace("getRealName - usertoken" + userTokenXml + "\nvalue:" + xPathExpression.evaluate(doc) + " " + xPathExpression2.evaluate(doc));
            return (xPathExpression.evaluate(doc)+" "+xPathExpression2.evaluate(doc));
        } catch (Exception e) {
            logger.error("getRealName - getTimestamp parsing error", e);
        }
        return "";
    }



    public String appendTicketToRedirectURI(String redirectURI, String userticket) {
        char paramSep = redirectURI.contains("?") ? '&' : '?';
        redirectURI += paramSep + SSOHelper.USERTICKET + '=' + userticket;
        return redirectURI;
    }

    public WhydahUserTokenId getUserTokenIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        boolean found = false;
        WhydahUserTokenId foundTokenId = WhydahUserTokenId.fromTokenId("");
        if (cookies != null) {
            logger.trace("getUserTokenIdFromCookie - Found {} cookie(s)", cookies.length);
            for (Cookie cookie : cookies) {
                logger.trace("getUserTokenIdFromCookie - Checking cookie:"+cookie.getName());
                if (!SSOHelper.USER_TOKEN_REFERENCE_NAME.equals(cookie.getName())) {
                    continue;
                }

                String usertokenId = cookie.getValue();
                logger.trace("getUserTokenIdFromCookie - Found whydahusertoken cookie, usertokenid={}", usertokenId);
                if ("logout".equalsIgnoreCase(usertokenId)) {
                    return WhydahUserTokenId.invalidTokenId();
                }
                if (verifyUserTokenId(usertokenId)) {
                    logger.trace("getUserTokenIdFromCookie - usertokenid ok");
                    foundTokenId = WhydahUserTokenId.fromTokenId(usertokenId);
                    found = true;
                }
            }
        }
        if (found) {
            return foundTokenId;
        }
        logger.debug("getUserTokenIdFromCookie - Found no cookies with usertokenid");
        return WhydahUserTokenId.invalidTokenId();
    }


    private void logonApplication() {
        //todo sjekke om myAppTokenXml er gyldig før reauth
        WebResource logonResource = tokenServiceClient.resource(tokenServiceUri).path("logon");
        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        ApplicationCredential appCredential = new ApplicationCredential();
        appCredential.setApplicationID(applicationid);
        appCredential.setApplicationPassord(applicationsecret);


        formData.add("applicationcredential", appCredential.toXML());
        ClientResponse response;
        try {
            response = logonResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        }catch (RuntimeException e) {
            logger.error("logonApplication - Problem connecting to {}", logonResource.toString());
            throw(e);
        }
        //todo håndtere feil i statuskode + feil ved app-pålogging (retry etc)
        if (response.getStatus() != 200) {
            logger.error("Application authentication failed with statuscode {}", response.getStatus());
            throw new RuntimeException("Application authentication failed");
        }
        myAppTokenXml = response.getEntity(String.class);
        myAppTokenId = getAppTokenIdFromAppToken(myAppTokenXml);
        logger.debug("Applogon ok: apptokenxml: {}", myAppTokenXml);
        logger.debug("myAppTokenId: {}", myAppTokenId);
    }


    // TODO  rewrite this as XPATH
    private String getAppTokenIdFromAppToken(String appTokenXML) {
        String stag="<applicationtokenID>";
        String etag="</applicationtokenID>";
        return appTokenXML.substring(appTokenXML.indexOf(stag) + stag.length(), appTokenXML.indexOf(etag));
    }

    public String getUserToken(UserCredential user, String userticket) {
        if (ApplicationMode.DEV.equals(ApplicationMode.getApplicationMode())){
            return getDummyToken();
        }
        logonApplication();
        logger.debug("getUserToken - apptokenid: {}", myAppTokenId);

        logger.debug("getUserToken - Log on with user credentials {}", user.toString());
        WebResource getUserToken = tokenServiceClient.resource(tokenServiceUri).path("user/" + myAppTokenId + "/" + userticket + "/usertoken");
        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        formData.add("apptoken", myAppTokenXml);
        formData.add("usercredential", user.toXML());
        ClientResponse response = getUserToken.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            logger.info("getUserToken - User authentication failed with status code " + response.getStatus());
            return null;
            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("getUserToken - Log on OK with response {}", responseXML);
            return responseXML;
        }

        //retry once for other statuses
        response = getUserToken.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("getUserToken - Log on OK with response {}", responseXML);
            return responseXML;
        }else if (response.getStatus() == NOT_FOUND.getStatusCode()) {
            logger.error(printableUrlErrorMessage("getUserToken - Auth failed - Problems connecting with TokenService", getUserToken, response));
        }else {
            logger.info(printableUrlErrorMessage("getUserToken - User authentication failed", getUserToken, response));
        }
        return null;
        //throw new RuntimeException("User authentication failed with status code " + response.getStatus());
    }

    public boolean createTicketForUserTokenID(String userticket, String userTokenID){
        logonApplication();
        logger.debug("createTicketForUserTokenID - apptokenid: {}", myAppTokenId);

        WebResource getUserToken = tokenServiceClient.resource(tokenServiceUri).path("user/" + myAppTokenId  + "/create_userticket_by_usertokenid");
        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        formData.add("apptoken", myAppTokenXml);
        formData.add("userticket", userticket);
        formData.add("userTokenID", userTokenID);
        ClientResponse response = getUserToken.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            logger.info("createTicketForUserTokenID - failed with status code " + response.getStatus());
            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
            return false;
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("createTicketForUserTokenID - OK with response {}", responseXML);
            return true;
        }
        return false;

    }
    public String getUserTokenByUserTicket(String userticket) {
        if (ApplicationMode.DEV.equals(ApplicationMode.getApplicationMode())){
            return getDummyToken();
        }
        logonApplication();

        WebResource userTokenResource = tokenServiceClient.resource(tokenServiceUri).path("user/" + myAppTokenId + "/get_usertoken_by_userticket");
        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        logger.trace("getUserTokenByUserTicket - ticket: {} apptoken: {}",userticket,myAppTokenXml);
        formData.add("apptoken", myAppTokenXml);
        formData.add("userticket", userticket);
        ClientResponse response = userTokenResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            logger.warn("getUserTokenByUserTicket failed");
            throw new IllegalArgumentException("getUserTokenByUserTicket failed.");
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("Response OK with XML: {}", responseXML);
            return responseXML;
        }
        //retry
        response = userTokenResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("Response OK with XML: {}", responseXML);
            return responseXML;
        }
        String authenticationFailedMessage = printableUrlErrorMessage("User authentication failed", userTokenResource, response);
        logger.warn(authenticationFailedMessage);
        throw new RuntimeException(authenticationFailedMessage);
    }

    public String getUserTokenByUserTokenID(String usertokenId) {
        if (ApplicationMode.DEV.equals(ApplicationMode.getApplicationMode())) {
            return getDummyToken();
        }
        logonApplication();

        WebResource userTokenResource = tokenServiceClient.resource(tokenServiceUri).path("user/" + myAppTokenId + "/get_usertoken_by_usertokenid");
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("apptoken", myAppTokenXml);
        formData.add("usertokenid", usertokenId);
        ClientResponse response = userTokenResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            throw new IllegalArgumentException("Login failed.");
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("Response OK with XML: {}", responseXML);
            return responseXML;
        }
        //retry
        response = userTokenResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("Response OK with XML: {}", responseXML);
            return responseXML;
        }
        String authenticationFailedMessage = printableUrlErrorMessage("User authentication failed", userTokenResource, response);
        logger.warn(authenticationFailedMessage);
        throw new RuntimeException(authenticationFailedMessage);
    }

    public void releaseUserToken(String userTokenId) {
        logonApplication();
        WebResource releaseResource = tokenServiceClient.resource(tokenServiceUri).path("user/" + myAppTokenId + "/release_usertoken");
        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        formData.add(USER_TOKEN_ID, userTokenId);
        ClientResponse response = releaseResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if(response.getStatus() != OK.getStatusCode()) {
            logger.warn("releaseUserToken failed: {}", response);
        }
    }

    public boolean verifyUserTokenId(String usertokenid) {

        // If we get strange values...  return false
        if (usertokenid == null || usertokenid.length() < 4) {
            logger.trace("verifyUserTokenId - Called with bogus usertokenid {} return false",usertokenid);
            return false;
        }
        logonApplication();
        WebResource verifyResource = tokenServiceClient.resource(tokenServiceUri).path("user/" + myAppTokenId + "/validate_usertokenid/" + usertokenid);
        ClientResponse response = verifyResource.get(ClientResponse.class);
        if(response.getStatus() == OK.getStatusCode()) {
            logger.debug("verifyUserTokenId - usertokenid validated OK");
            return true;
        }
        if(response.getStatus() == CONFLICT.getStatusCode()) {
            logger.debug("verifyUserTokenId - usertokenid not ok: {}" + response);
            return false;
        }
        //retry
        logger.info("verifyUserTokenId - retrying usertokenid ");
        logonApplication();
        response = verifyResource.get(ClientResponse.class);
        boolean bolRes = response.getStatus() == OK.getStatusCode();
        logger.debug("verifyUserTokenId - validate_usertokenid {}  result {}","user/" + myAppTokenId + "/validate_usertokenid/" + usertokenid, response);
        return bolRes;
    }

    public String createAndLogonUser(User fbUser, String fbAccessToken, UserCredential userCredential, String userticket) {
        logonApplication();
        logger.debug("apptokenid: {}", myAppTokenId);


        WebResource createUserResource = tokenServiceClient.resource(tokenServiceUri).path("user/" + myAppTokenId +"/"+ userticket + "/create_user");
        logger.trace("createUserResource:"+createUserResource.toString());

        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        formData.add("apptoken", myAppTokenXml);
        formData.add("usercredential", userCredential.toXML());
        String facebookUserAsXml = FacebookHelper.getFacebookUserAsXml(fbUser, fbAccessToken);
        formData.add("fbuser", facebookUserAsXml);
        logger.trace("createAndLogonUser with fbuser XML: " + facebookUserAsXml+"\nformData:\n"+formData);
        logger.info("createAndLogonUser username=" + fbUser.getUsername());
        ClientResponse response = createUserResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);

        //No need to retry if we know it is forbidden.
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            //throw new IllegalArgumentException("createAndLogonUser failed. username=" + fbUser.getUsername() + ", id=" + fbUser.getId());
            logger.warn("createAndLogonUser failed. username=" + fbUser.getUsername() + ", id=" + fbUser.getId());
            return null;
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("createAndLogonUser OK with response {}", responseXML);
            return responseXML;
        }

        //retry once for other statuses
        response = createUserResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("createAndLogonUser OK with response {}", responseXML);
            return responseXML;
        }

        logger.warn(printableUrlErrorMessage("createAndLogonUser failed after retrying once.", createUserResource, response));
        return null;
        //throw new RuntimeException("createAndLogonUser failed with status code " + response.getStatus());
    }

    public String createAndLogonUser(String netiqUserName, String netiqAccessToken, UserCredential userCredential, String userticket,HttpServletRequest request) {
        logonApplication();
        logger.debug("createAndLogonUser - apptokenid: {}", myAppTokenId);

        WebResource createUserResource = tokenServiceClient.resource(tokenServiceUri).path("user/" + myAppTokenId +"/"+ userticket + "/create_user");
        logger.debug("createUserResource:"+createUserResource.toString());


        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        formData.add("apptoken", myAppTokenXml);
        formData.add("usercredential", userCredential.toXML());
        NetIQHelper helper = new NetIQHelper();
        String netIQUserAsXml = helper.getNetIQUserAsXml(request);
        formData.add("fbuser", netIQUserAsXml);
        logger.trace("createAndLogonUser with netiquser XML: " + netIQUserAsXml+"\nformData:\n"+formData);
        logger.info("createAndLogonUser username=" + helper.getUserName(request));
        ClientResponse response = createUserResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);

        //No need to retry if we know it is forbidden.
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            //throw new IllegalArgumentException("createAndLogonUser failed. username=" + fbUser.getUsername() + ", id=" + fbUser.getId());
            logger.warn("createAndLogonUser failed. username=" + helper.getUserName(request) + ", id=" + helper.getEmail(request));
            return null;
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("createAndLogonUser OK with response {}", responseXML);
            return responseXML;
        }

        //retry once for other statuses
        response = createUserResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("createAndLogonUser OK with response {}", responseXML);
            return responseXML;
        }

        logger.warn("createAndLogonUser failed after retrying once.");
        return null;
        //throw new RuntimeException("createAndLogonUser failed with status code " + response.getStatus());
    }
	public LoginTypes getEnabledLoginTypes() {
		return enabledLoginTypes;
	}

    public static  String getDummyToken(){
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<usertoken xmlns:ns2=\"http://www.w3.org/1999/xhtml\" id=\"759799fe-2e2f-4c8e-b096-d5796733d4d2\">\n" +
                "    <uid>7583278592730985723</uid>\n" +
                "    <securitylevel>0</securitylevel>\n" +
                "    <personRef></personRef>\n" +
                "    <firstname>Olav</firstname>\n" +
                "    <lastname>Nordmann</lastname>\n" +
                "    <email></email>\n" +
                "    <timestamp>7982374982374</timestamp>\n" +
                "    <lifespan>3600000</lifespan>\n" +
                "    <issuer>/iam/issuer/tokenverifier</issuer>\n" +
                "    <application ID=\"2349785543\">\n" +
                "        <applicationName>MyApp</applicationName>\n" +
                "        <organization ID=\"2349785543\">\n" +
                "            <organizationName>myCompany</organizationName>\n" +
                "            <role name=\"janitor\" value=\"Employed\"/>\n" +
                "            <role name=\"board\" value=\"President\"/>\n" +
                "        </organization>\n" +
                "        <organization ID=\"0078\">\n" +
                "            <organizationName>myDayJobCompany</organizationName>\n" +
                "            <role name=\"board\" value=\"\"/>\n" +
                "        </organization>\n" +
                "    </application>\n" +
                "    <application ID=\"appa\">\n" +
                "        <applicationName>App A</applicationName>\n" +
                "        <organization ID=\"1078\">\n" +
                "            <organizationName>myFotballClub</organizationName>\n" +
                "            <role name=\"janitor\" value=\"Employed\"/>\n" +
                "        </organization>\n" +
                "    </application>\n" +
                "\n" +
                "    <ns2:link type=\"application/xml\" href=\"/\" rel=\"self\"/>\n" +
                "    <hash type=\"MD5\">7671ec2d5bac82d1e70b33c59b5c96a3</hash>\n" +
                "</usertoken>";

    }

    public static String getCookieDomain() {

        logger.info("CookieDomain: " + cookiedomain);
        return cookiedomain;
    }

    private String printableUrlErrorMessage(String errorMessage, WebResource request, ClientResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append(errorMessage);
        sb.append(" Code: ");
        if(response != null) {
            sb.append(response.getStatus());
            sb.append(" URL: ");
        }
        if(request != null) {
            sb.append(request.toString());
        }
        return sb.toString();
    }
}

