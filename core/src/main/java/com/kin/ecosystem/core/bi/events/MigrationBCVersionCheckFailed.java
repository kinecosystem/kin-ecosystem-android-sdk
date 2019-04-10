
package com.kin.ecosystem.core.bi.events;

// Augmented by script
import com.kin.ecosystem.core.bi.Event;
import com.kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Asking the server for the current blockchain version for the account- failed
 * 
 */
public class MigrationBCVersionCheckFailed implements Event {
    public static final String EVENT_NAME = "migration_BC_version_check_failed";
    public static final String EVENT_TYPE = "log";

    // Augmented by script
    public static MigrationBCVersionCheckFailed create(String errorReason, String publicAddress, MigrationBCVersionCheckFailed.BlockchainVersion blockchainVersion) {
        return new MigrationBCVersionCheckFailed(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            errorReason,
            publicAddress,
            blockchainVersion);
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
    @SerializedName("error_reason")
    @Expose
    private String errorReason;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("public_address")
    @Expose
    private String publicAddress;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("blockchain_version")
    @Expose
    private MigrationBCVersionCheckFailed.BlockchainVersion blockchainVersion;

    /**
     * No args constructor for use in serialization
     * 
     */
    public MigrationBCVersionCheckFailed() {
    }

    /**
     * 
     * @param common
     * @param errorReason
     * @param blockchainVersion

     * @param client
     * @param publicAddress

     * @param user
     */
    public MigrationBCVersionCheckFailed(Common common, User user, Client client, String errorReason, String publicAddress, MigrationBCVersionCheckFailed.BlockchainVersion blockchainVersion) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.errorReason = errorReason;
        this.publicAddress = publicAddress;
        this.blockchainVersion = blockchainVersion;
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
    public String getErrorReason() {
        return errorReason;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getPublicAddress() {
        return publicAddress;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setPublicAddress(String publicAddress) {
        this.publicAddress = publicAddress;
    }

    /**
     * 
     * (Required)
     * 
     */
    public MigrationBCVersionCheckFailed.BlockchainVersion getBlockchainVersion() {
        return blockchainVersion;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setBlockchainVersion(MigrationBCVersionCheckFailed.BlockchainVersion blockchainVersion) {
        this.blockchainVersion = blockchainVersion;
    }

    public enum BlockchainVersion {

        @SerializedName("2")
        _2("2"),
        @SerializedName("3")
        _3("3");
        private final String value;
        private final static Map<String, MigrationBCVersionCheckFailed.BlockchainVersion> CONSTANTS = new HashMap<String, MigrationBCVersionCheckFailed.BlockchainVersion>();

        static {
            for (MigrationBCVersionCheckFailed.BlockchainVersion c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private BlockchainVersion(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static MigrationBCVersionCheckFailed.BlockchainVersion fromValue(String value) {
            MigrationBCVersionCheckFailed.BlockchainVersion constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
