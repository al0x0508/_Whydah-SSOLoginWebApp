package net.whydah.sso;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

public class ServerRunner {
    public static final int PORT_NO = 9997;
    public static final String ROOT_URL = "http://localhost:"+PORT_NO+"/sso/";
    public static final String TESTURL = ROOT_URL + "action";
    public static final String CONTEXT = "/sso";

    private final static Logger logger = LoggerFactory.getLogger(ServerRunner.class);

    private Server server;
    private ServletContextHandler context;

    public ServerRunner()  {
        server = new Server(PORT_NO);
        context = new ServletContextHandler(server, CONTEXT);

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath:webapp/sso/mvc-config.xml");
        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        context.addServlet(servletHolder, "/*");
  
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public static void main(String[] arguments) throws Exception {
        ServerRunner serverRunner = new ServerRunner();
        serverRunner.start();

       logger.info("Jetty server started - " + serverRunner.server.getConnectors()[0].getHost() + " : " +serverRunner.server.getConnectors()[0].getLocalPort());

        logger.info("Jetty server started - " + serverRunner.server.getConnectors()[0].getHost() + " : " +
            serverRunner.server.getConnectors()[0].getLocalPort());

        serverRunner.join();
    }
}
