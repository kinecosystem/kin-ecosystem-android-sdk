package com.kin.ecosystem.data.model;

public class WhiteListData {

    private String userID;
    private String appID;
    private String apiKey;

    public WhiteListData(String userID, String appID, String apiKey) {
        this.userID = userID;
        this.appID = appID;
        this.apiKey = apiKey;
    }

    public String getUserID() {
        return userID;
    }

    public String getAppID() {
        return appID;
    }

    public String getApiKey() {
        return apiKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WhiteListData that = (WhiteListData) o;

        if (userID != null ? !userID.equals(that.userID) : that.userID != null) {
            return false;
        }
        if (appID != null ? !appID.equals(that.appID) : that.appID != null) {
            return false;
        }
        return apiKey != null ? apiKey.equals(that.apiKey) : that.apiKey == null;
    }

    @Override
    public int hashCode() {
        int result = userID != null ? userID.hashCode() : 0;
        result += (appID != null ? appID.hashCode() : 0);
        result += (apiKey != null ? apiKey.hashCode() : 0);
        return result;
    }
}
