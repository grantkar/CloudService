package domain;

import java.io.Serializable;

public class RegistrationMessage implements Serializable {
    private String login;
    private String password;

    public RegistrationMessage(String login, String password) {
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
