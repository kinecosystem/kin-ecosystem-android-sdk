
package com.kin.ecosystem.core.bi.events;

// Augmented by script
import com.kin.ecosystem.core.bi.Event;
import com.kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Asking the server for the current blockchain version for the account- succeeded
 * 
 */
public class MigrationBCVersionCheckSucceeded implements Event {
    public static final String EVENT_NAME = "migration_BC_version_check_succeeded";
    public static final String EVENT_TYPE = "log";

    // Augmented by script
    public static MigrationBCVersionCheckSucceeded create(String publicAddress, MigrationBCVersionCheckSucceeded.BlockchainVersion blockchainVersion) {
        return new MigrationBCVersionCheckSucceeded(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
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
    private MigrationBCVersionCheckSucceeded.BlockchainVersion blockchainVersion;

    /**
     * No args constructor for use in serialization
     * 
     */
    public MigrationBCVersionCheckSucceeded() {
    }

    /**
     * 
     * @param common
     * @param blockchainVersion

     * @param client
     * @param publicAddress

     * @param user
     */
    public MigrationBCVersionCheckSucceeded(Common common, User user, Client client, String publicAddress, MigrationBCVersionCheckSucceeded.BlockchainVersion blockchainVersion) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
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
    public MigrationBCVersionCheckSucceeded.BlockchainVersion getBlockchainVersion() {
        return blockchainVersion;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setBlockchainVersion(MigrationBCVersionCheckSucceeded.BlockchainVersion blockchainVersion) {
        this.blockchainVersion = blockchainVersion;
    }

    public enum BlockchainVersion {

        @SerializedName("2")
        _2("2"),
        @SerializedName("3")
        _3("3");
        private final String value;
        private final static Map<String, MigrationBCVersionCheckSucceeded.BlockchainVersion> CONSTANTS = new HashMap<String, MigrationBCVersionCheckSucceeded.BlockchainVersion>();

        static {
            for (MigrationBCVersionCheckSucceeded.BlockchainVersion c: values()) {
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

        public static MigrationBCVersionCheckSucceeded.BlockchainVersion fromValue(String value) {
            MigrationBCVersionCheckSucceeded.BlockchainVersion constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
