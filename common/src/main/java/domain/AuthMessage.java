package domain;

import java.io.Serializable;

public class AuthMessage implements Serializable {
    private String login;
    private String password;

    public AuthMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
