package ru.linachan.fenrir;

public class FenrirSession {

    private FenrirCore core;

    private FenrirUser user;
    private String token;
    private String clientIP;

    public FenrirSession(FenrirUser user, String clientIP, String token) {
        this.user = user;
        this.clientIP = clientIP;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public boolean checkIP(String clientIP) {
        return this.clientIP.equals(clientIP);
    }

    public FenrirUser getUser() {
        return user;
    }
}
