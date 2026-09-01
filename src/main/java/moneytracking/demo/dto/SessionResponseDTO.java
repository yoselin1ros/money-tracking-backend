package moneytracking.demo.dto;

public class SessionResponseDTO {
    private Long id;
    private String deviceId;
    private String deviceName;
    private Long platformId;
    private String platformName;
    private String expiresAt;
    private String createdAt;
    private Boolean revoked;
    private String lastActivityAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }

    public String getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(String lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    @Override
    public String toString() {
        return "SessionResponseDTO [id=" + id + ", deviceId=" + deviceId + ", deviceName=" + deviceName
                + ", platformId=" + platformId + ", platformName=" + platformName + ", expiresAt=" + expiresAt
                + ", createdAt=" + createdAt + ", revoked=" + revoked + ", lastActivityAt=" + lastActivityAt + "]";
    }

}
