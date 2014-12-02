package net.whydah.sso.notinuse.commands;

import net.whydah.sso.config.AppConfig;
import net.whydah.sso.config.ApplicationMode;
import org.junit.BeforeClass;
import org.junit.Test;
import rx.Observable;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;


public class TestCircuitBreaker {

    @BeforeClass
    public static void setup() {
        System.setProperty(ApplicationMode.IAM_MODE_KEY, ApplicationMode.TEST);
    }


    @Test
    public void testDummyCommand() throws Exception {
        String s = new CommandLogonUser("Bob").execute();
        assertEquals("Hello Bob!",s);
        Future<String> s2 = new CommandLogonUser("Bob").queue();
        assertEquals("Hello Bob!",s2.get());
        Observable<String> s3 = new CommandLogonUser("Bob").observe();
        assertEquals("Hello Bob!",s3.toBlocking().single());
    }


}
