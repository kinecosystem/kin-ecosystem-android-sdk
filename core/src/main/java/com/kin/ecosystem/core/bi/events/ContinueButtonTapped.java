
package com.kin.ecosystem.core.bi.events;

// Augmented by script
import com.kin.ecosystem.core.bi.Event;
import com.kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * User click on "call to action" button on  page in new UI
 * 
 */
public class ContinueButtonTapped implements Event {
    public static final String EVENT_NAME = "continue_button_tapped";
    public static final String EVENT_TYPE = "analytics";

    // Augmented by script
    public static ContinueButtonTapped create(ContinueButtonTapped.PageName pageName, ContinueButtonTapped.PageContinue pageContinue, ContinueButtonTapped.SettingOption settingOption) {
        return new ContinueButtonTapped(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            pageName,
            pageContinue,
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
    @SerializedName("page_name")
    @Expose
    private ContinueButtonTapped.PageName pageName;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("page_continue")
    @Expose
    private ContinueButtonTapped.PageContinue pageContinue;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("setting_option")
    @Expose
    private ContinueButtonTapped.SettingOption settingOption;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ContinueButtonTapped() {
    }

    /**
     * 
     * @param pageContinue
     * @param common

     * @param client

     * @param settingOption
     * @param user
     * @param pageName
     */
    public ContinueButtonTapped(Common common, User user, Client client, ContinueButtonTapped.PageName pageName, ContinueButtonTapped.PageContinue pageContinue, ContinueButtonTapped.SettingOption settingOption) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.pageName = pageName;
        this.pageContinue = pageContinue;
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
    public ContinueButtonTapped.PageName getPageName() {
        return pageName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setPageName(ContinueButtonTapped.PageName pageName) {
        this.pageName = pageName;
    }

    /**
     * 
     * (Required)
     * 
     */
    public ContinueButtonTapped.PageContinue getPageContinue() {
        return pageContinue;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setPageContinue(ContinueButtonTapped.PageContinue pageContinue) {
        this.pageContinue = pageContinue;
    }

    /**
     * 
     * (Required)
     * 
     */
    public ContinueButtonTapped.SettingOption getSettingOption() {
        return settingOption;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setSettingOption(ContinueButtonTapped.SettingOption settingOption) {
        this.settingOption = settingOption;
    }

    public enum PageContinue {

        @SerializedName("onboarding_continue_to_main_page")
        ONBOARDING_CONTINUE_TO_MAIN_PAGE("onboarding_continue_to_main_page"),
        @SerializedName("main_page_continue_to_my_kin")
        MAIN_PAGE_CONTINUE_TO_MY_KIN("main_page_continue_to_my_kin"),
        @SerializedName("main_page_empty_state_continue_to_my_kin")
        MAIN_PAGE_EMPTY_STATE_CONTINUE_TO_MY_KIN("main_page_empty_state_continue_to_my_kin"),
        @SerializedName("my_kin_page_continue_to_settings")
        MY_KIN_PAGE_CONTINUE_TO_SETTINGS("my_kin_page_continue_to_settings"),
        @SerializedName("settings_page_continue_to_options")
        SETTINGS_PAGE_CONTINUE_TO_OPTIONS("settings_page_continue_to_options"),
        @SerializedName("not_enough_kin_continue_button")
        NOT_ENOUGH_KIN_CONTINUE_BUTTON("not_enough_kin_continue_button"),
        @SerializedName("spend_confirmation_continue_button")
        SPEND_CONFIRMATION_CONTINUE_BUTTON("spend_confirmation_continue_button");
        private final String value;
        private final static Map<String, ContinueButtonTapped.PageContinue> CONSTANTS = new HashMap<String, ContinueButtonTapped.PageContinue>();

        static {
            for (ContinueButtonTapped.PageContinue c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private PageContinue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static ContinueButtonTapped.PageContinue fromValue(String value) {
            ContinueButtonTapped.PageContinue constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum PageName {

        @SerializedName("onboarding")
        ONBOARDING("onboarding"),
        @SerializedName("main_page")
        MAIN_PAGE("main_page"),
        @SerializedName("main_page_empty_state")
        MAIN_PAGE_EMPTY_STATE("main_page_empty_state"),
        @SerializedName("my_kin_page")
        MY_KIN_PAGE("my_kin_page"),
        @SerializedName("settings ")
        SETTINGS("settings "),
        @SerializedName("dialogs_not_enough_kin")
        DIALOGS_NOT_ENOUGH_KIN("dialogs_not_enough_kin"),
        @SerializedName("dialogs_spend_confirmation_screen")
        DIALOGS_SPEND_CONFIRMATION_SCREEN("dialogs_spend_confirmation_screen");
        private final String value;
        private final static Map<String, ContinueButtonTapped.PageName> CONSTANTS = new HashMap<String, ContinueButtonTapped.PageName>();

        static {
            for (ContinueButtonTapped.PageName c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private PageName(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static ContinueButtonTapped.PageName fromValue(String value) {
            ContinueButtonTapped.PageName constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum SettingOption {

        @SerializedName("backup")
        BACKUP("backup"),
        @SerializedName("restore")
        RESTORE("restore");
        private final String value;
        private final static Map<String, ContinueButtonTapped.SettingOption> CONSTANTS = new HashMap<String, ContinueButtonTapped.SettingOption>();

        static {
            for (ContinueButtonTapped.SettingOption c: values()) {
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

        public static ContinueButtonTapped.SettingOption fromValue(String value) {
            ContinueButtonTapped.SettingOption constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
