package model;

public class AccessLog {
    private String accessType;
    private String accessTime;  // Time will now be a string

    public AccessLog(String accessType, String accessTime) {
        this.accessType = accessType;
        this.accessTime = accessTime;
    }

    public String getAccessType() {
        return accessType;
    }

    public String getAccessTime() {
        return accessTime;
    }
}
