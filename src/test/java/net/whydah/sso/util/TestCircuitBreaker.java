package net.whydah.sso.util;

import org.junit.Test;
import rx.Observable;

import java.util.concurrent.Future;

public class TestCircuitBreaker {


    @Test
    public void testDummyCommand(){
        String s = new CommandLogonUser("Bob").execute();
        Future<String> s2 = new CommandLogonUser("Bob").queue();
        Observable<String> s3 = new CommandLogonUser("Bob").observe();
    }
}
