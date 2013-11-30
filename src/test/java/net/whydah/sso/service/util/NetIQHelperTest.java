package net.whydah.sso.service.util;

import static org.mockito.Mockito.*;
import net.whydah.sso.service.data.UserCredential;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

import static junit.framework.Assert.assertEquals;

public class NetIQHelperTest {

    /**
     * Manual test.
     * Update access token and run.
     * https://graph.facebook.com/me?access_token=
     */
    @Ignore
    @Test
    public void testCreateUserFromNetIQRedirect() {

        NetIQHelper netIQ = new NetIQHelper();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeaderNames())
                .thenReturn(netIQ.getExpectedHeaders());

        when(request.getHeader(anyString())).thenAnswer(new Answer() {
            public String answer(InvocationOnMock invocation) {
                NetIQHelper netIQ = new NetIQHelper();
                Object[] args = invocation.getArguments();
                Object mock = invocation.getMock();
                return netIQ.getExpectedHeader((String)args[0]);
            }
        });


        assertEquals("Thor Henning", netIQ.getFirstName(request));
        assertEquals("Hetland", netIQ.getLastName(request));
        assertEquals("Thor-Henning.Hetland@altran.com", netIQ.getEmail(request));

        UserCredential userCredential= new UserCredential() {
            @Override
            public String toXML() {
                return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +
                        "<usercredential>\n" +
                        "    <params>\n" +
                        "        <username>" + "user" + "</username>\n" +
                        "    </params> \n" +
                        "</usercredential>\n";
            }
        };

    }

    @Test
    public void testHTTPHeaders() {

        NetIQHelper netIQ = new NetIQHelper();
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeaderNames())
                .thenReturn(netIQ.getExpectedHeaders());

        when(request.getHeader(anyString())).thenAnswer(new Answer() {
            public String answer(InvocationOnMock invocation) {
                NetIQHelper netIQ = new NetIQHelper();
                Object[] args = invocation.getArguments();
                Object mock = invocation.getMock();
                return netIQ.getExpectedHeader((String)args[0]);
            }
        });


        Enumeration headerNames = request.getHeaderNames();
        assert(headerNames.hasMoreElements());

        while(headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            System.out.println("HeaderName:" + headerName);
            System.out.println("Value:" + request.getHeader(headerName));
        }
    }


}
