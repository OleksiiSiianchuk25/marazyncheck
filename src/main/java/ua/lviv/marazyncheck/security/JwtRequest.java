package ua.lviv.marazyncheck.security;

public class JwtRequest {
    private String username; // or email if you use email as the username
    private String password;

    // Default constructor for JSON Parsing
    public JwtRequest() {}

    public JwtRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
