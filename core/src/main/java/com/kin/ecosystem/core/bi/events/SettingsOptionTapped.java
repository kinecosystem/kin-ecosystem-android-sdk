
package com.kin.ecosystem.core.bi.events;

// Augmented by script
import com.kin.ecosystem.core.bi.Event;
import com.kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * User chooses an option in the settings page
 * 
 */
public class SettingsOptionTapped implements Event {
    public static final String EVENT_NAME = "settings_option_tapped";
    public static final String EVENT_TYPE = "analytics";

    // Augmented by script
    public static SettingsOptionTapped create(SettingsOptionTapped.SettingOption settingOption) {
        return new SettingsOptionTapped(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            settingOption);
    }

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_name")
    @Expose
    private String eventName = EVENT_NAME;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("event_type")
    @Expose
    private String eventType = EVENT_TYPE;
    /**
     * common properties for all events
     * (Required)
     * 
     */
    @SerializedName("common")
    @Expose
    private Common common;
    /**
     * common user properties
     * (Required)
     * 
     */
    @SerializedName("user")
    @Expose
    private User user;
    /**
     * common properties for all client events
     * (Required)
     * 
     */
    @SerializedName("client")
    @Expose
    private Client client;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("setting_option")
    @Expose
    private SettingsOptionTapped.SettingOption settingOption;

    /**
     * No args constructor for use in serialization
     * 
     */
    public SettingsOptionTapped() {
    }

    /**
     * 
     * @param common

     * @param client

     * @param settingOption
     * @param user
     */
    public SettingsOptionTapped(Common common, User user, Client client, SettingsOptionTapped.SettingOption settingOption) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.settingOption = settingOption;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * common properties for all events
     * (Required)
     * 
     */
    public Common getCommon() {
        return common;
    }

    /**
     * common properties for all events
     * (Required)
     * 
     */
    public void setCommon(Common common) {
        this.common = common;
    }

    /**
     * common user properties
     * (Required)
     * 
     */
    public User getUser() {
        return user;
    }

    /**
     * common user properties
     * (Required)
     * 
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * common properties for all client events
     * (Required)
     * 
     */
    public Client getClient() {
        return client;
    }

    /**
     * common properties for all client events
     * (Required)
     * 
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * 
     * (Required)
     * 
     */
    public SettingsOptionTapped.SettingOption getSettingOption() {
        return settingOption;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setSettingOption(SettingsOptionTapped.SettingOption settingOption) {
        this.settingOption = settingOption;
    }

    public enum SettingOption {

        @SerializedName("backup")
        BACKUP("backup"),
        @SerializedName("restore")
        RESTORE("restore");
        private final String value;
        private final static Map<String, SettingsOptionTapped.SettingOption> CONSTANTS = new HashMap<String, SettingsOptionTapped.SettingOption>();

        static {
            for (SettingsOptionTapped.SettingOption c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private SettingOption(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static SettingsOptionTapped.SettingOption fromValue(String value) {
            SettingsOptionTapped.SettingOption constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
