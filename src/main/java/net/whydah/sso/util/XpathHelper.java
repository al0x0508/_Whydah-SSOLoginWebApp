package net.whydah.sso.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class XpathHelper {

    private static final Logger logger = LoggerFactory.getLogger(XpathHelper.class);

    public static String getUserTokenId(String userTokenXml) {
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

    public static String getLifespan(String userTokenXml) {
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

    public static String getTimestamp(String userTokenXml) {
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

    public static String getRealName(String userTokenXml){
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

    // TODO  rewrite this as XPATH
    private String delete_getAppTokenIdFromAppToken(String appTokenXML) {
        String stag="<applicationtokenID>";
        String etag="</applicationtokenID>";
        return appTokenXML.substring(appTokenXML.indexOf(stag) + stag.length(), appTokenXML.indexOf(etag));
    }

    public static  String getAppTokenIdFromAppToken(String appTokenXML) {
        logger.trace("appTokenXML: {}", appTokenXML);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(appTokenXML)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/applicationtoken/params/applicationtokenID[1]";
            XPathExpression xPathExpression = xPath.compile(expression);
            String appId = xPathExpression.evaluate(doc);
            logger.trace("XML parse: applicationtokenID = {}", appId);
            return appId;
        } catch (Exception e) {
            logger.error("getAppTokenIdFromAppToken - Could not get applicationID from XML: " + appTokenXML, e);
        }
        return "";
    }


}
