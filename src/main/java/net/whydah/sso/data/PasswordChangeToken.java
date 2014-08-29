package net.whydah.sso.data;

import com.sun.jersey.core.util.Base64;

public class PasswordChangeToken {
    private final String user;
    private final long timeout;
    private final String token;

    public PasswordChangeToken(String token) {
        this.token = token;
        String decoded = Base64.base64Decode(token);
        String[] elements = decoded.split(":");
        user = elements[0];
        timeout = Long.parseLong(elements[1]);
    }

    public boolean isValid() {
        return timeout > System.currentTimeMillis();
    }

    public String getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "PasswordChangeToken{" +
                "user='" + user + '\'' +
                ", timeout=" + timeout +
                '}';
    }
}
