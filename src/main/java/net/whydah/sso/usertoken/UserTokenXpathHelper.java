package net.whydah.sso.usertoken;

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

public class UserTokenXpathHelper {
    private static final Logger logger = LoggerFactory.getLogger(UserTokenXpathHelper.class);

    public static String getUserTokenId(String userTokenXml) {
        if (userTokenXml == null) {
            logger.debug("userTokenXml was empty, so returning empty userTokenId.");
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
            logger.error("getUserTokenId parsing error", e);
        }
        return "";
    }


    public static  String getAppTokenIdFromAppToken(String appTokenXML) {
        //logger.trace("appTokenXML: {}", appTokenXML);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(appTokenXML)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/applicationtoken/params/applicationtokenID[1]";
            XPathExpression xPathExpression = xPath.compile(expression);
            String appId = xPathExpression.evaluate(doc);
            logger.debug("getAppTokenIdFromAppToken: applicationTokenId={}, appTokenXML={}", appId, appTokenXML);
            return appId;
        } catch (Exception e) {
            logger.error("getAppTokenIdFromAppToken - appTokenXML - Could not get applicationID from XML: " + appTokenXML, e);
        }
        return "";
    }

    public static String getRealName(String userTokenXml){
        if (userTokenXml==null){
            logger.debug("userTokenXml was empty, so returning empty realName.");
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
            logger.debug("getRealName - usertoken" + userTokenXml + "\nvalue:" + xPathExpression.evaluate(doc) + " " + xPathExpression2.evaluate(doc));
            return (xPathExpression.evaluate(doc)+" "+xPathExpression2.evaluate(doc));
        } catch (Exception e) {
            logger.error("getRealName - userTokenXml - getTimestamp parsing error", e);
        }
        return "";
    }


    public static String getLifespan(String userTokenXml) {
        if (userTokenXml == null){
            logger.debug("userTokenXml was empty, so returning empty lifespan.");
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
            logger.error("getLifespan - userTokenXml lifespan parsing error", e);
        }
        return "";
    }

    public static String getTimestamp(String userTokenXml) {
        if (userTokenXml==null){
            logger.debug("userTokenXml was empty, so returning empty timestamp.");
            return "";
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/usertoken/timestamp";
            XPathExpression xPathExpression = xPath.compile(expression);
            logger.debug("token" + userTokenXml + "\nvalue:" + xPathExpression.evaluate(doc));
            return (xPathExpression.evaluate(doc));
        } catch (Exception e) {
            logger.error("getTimestamp - userTokenXml timestamp parsing error", e);
        }
        return "";
    }
}
