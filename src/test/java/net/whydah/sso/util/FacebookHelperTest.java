package net.whydah.sso.util;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.User;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author <a href="mailto:erik.drolshammer@altran.com">Erik Drolshammer</a>
 * @since 10/15/12
 */
public class FacebookHelperTest {


    /**
     * Manual test.
     * Update access token and run.
     * https://graph.facebook.com/me?access_token=
     */
    @Ignore
    @Test
    public void testCreateUserFromFacebookAttributes() {
        String faceBookAccessToken = "fbAccessTokenHere";
        FacebookClient facebookClient = new DefaultFacebookClient(faceBookAccessToken);
        User fbUser = facebookClient.fetchObject("me", User.class);

        assertEquals("745925301", fbUser.getId());
        assertEquals("Erik", fbUser.getFirstName());
        assertEquals("Drolshammer", fbUser.getLastName());
        assertEquals("male", fbUser.getGender());
        assertEquals("Moss, Norway", fbUser.getHometown().getName());
        assertEquals("Oslo, Norway", fbUser.getLocation().getName());

        //Requires more permissions
        assertEquals("08/05/1982", fbUser.getBirthday());
        assertEquals("erik.drolshammer@gmail.com", fbUser.getEmail());
    }


    /**
     * Manual test.
     */
    @Ignore
    @Test
    public void testGetFacebookFriends() {
        String faceBookAccessToken = "fbAccessTokenHere";
        FacebookClient facebookClient = new DefaultFacebookClient(faceBookAccessToken);

        Connection<User> friendsConnection = facebookClient.fetchConnection("me/friends", User.class, Parameter.with("fields", "id, name, birthday"));
        @SuppressWarnings("unused")
		List<User> users = friendsConnection.getData();
    }

}
