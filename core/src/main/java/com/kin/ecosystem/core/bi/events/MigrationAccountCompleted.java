
package com.kin.ecosystem.core.bi.events;

// Augmented by script
import com.kin.ecosystem.core.bi.Event;
import com.kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * migration process has been completed all events in the process has been succeeded
 * 
 */
public class MigrationAccountCompleted implements Event {
    public static final String EVENT_NAME = "migration_account_completed";
    public static final String EVENT_TYPE = "business";

    // Augmented by script
    public static MigrationAccountCompleted create(MigrationAccountCompleted.BlockchainVersion blockchainVersion, String publicAddress) {
        return new MigrationAccountCompleted(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            blockchainVersion,
            publicAddress);
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
    @SerializedName("blockchain_version")
    @Expose
    private MigrationAccountCompleted.BlockchainVersion blockchainVersion;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("public_address")
    @Expose
    private String publicAddress;

    /**
     * No args constructor for use in serialization
     * 
     */
    public MigrationAccountCompleted() {
    }

    /**
     * 
     * @param common
     * @param blockchainVersion

     * @param client
     * @param publicAddress

     * @param user
     */
    public MigrationAccountCompleted(Common common, User user, Client client, MigrationAccountCompleted.BlockchainVersion blockchainVersion, String publicAddress) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.blockchainVersion = blockchainVersion;
        this.publicAddress = publicAddress;
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
    public MigrationAccountCompleted.BlockchainVersion getBlockchainVersion() {
        return blockchainVersion;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setBlockchainVersion(MigrationAccountCompleted.BlockchainVersion blockchainVersion) {
        this.blockchainVersion = blockchainVersion;
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

    public enum BlockchainVersion {

        @SerializedName("2")
        _2("2"),
        @SerializedName("3")
        _3("3");
        private final String value;
        private final static Map<String, MigrationAccountCompleted.BlockchainVersion> CONSTANTS = new HashMap<String, MigrationAccountCompleted.BlockchainVersion>();

        static {
            for (MigrationAccountCompleted.BlockchainVersion c: values()) {
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

        public static MigrationAccountCompleted.BlockchainVersion fromValue(String value) {
            MigrationAccountCompleted.BlockchainVersion constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
