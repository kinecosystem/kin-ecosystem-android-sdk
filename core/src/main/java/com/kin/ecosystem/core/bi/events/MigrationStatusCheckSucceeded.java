
package com.kin.ecosystem.core.bi.events;

// Augmented by script
import com.kin.ecosystem.core.bi.Event;
import com.kin.ecosystem.core.bi.EventsStore;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Asking the server for account status - succeeded
 * 
 */
public class MigrationStatusCheckSucceeded implements Event {
    public static final String EVENT_NAME = "migration_status_check_succeeded";
    public static final String EVENT_TYPE = "log";

    // Augmented by script
    public static MigrationStatusCheckSucceeded create(String publicAddress, MigrationStatusCheckSucceeded.ShouldMigrate shouldMigrate, MigrationStatusCheckSucceeded.IsRestorable isRestorable, MigrationStatusCheckSucceeded.BlockchainVersion blockchainVersion) {
        return new MigrationStatusCheckSucceeded(
            (Common) EventsStore.common(),
            (User) EventsStore.user(),
            (Client) EventsStore.client(),
            publicAddress,
            shouldMigrate,
            isRestorable,
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
    @SerializedName("should_migrate")
    @Expose
    private MigrationStatusCheckSucceeded.ShouldMigrate shouldMigrate;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("is_restorable")
    @Expose
    private MigrationStatusCheckSucceeded.IsRestorable isRestorable;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("blockchain_version")
    @Expose
    private MigrationStatusCheckSucceeded.BlockchainVersion blockchainVersion;

    /**
     * No args constructor for use in serialization
     * 
     */
    public MigrationStatusCheckSucceeded() {
    }

    /**
     * 
     * @param common
     * @param blockchainVersion

     * @param client
     * @param publicAddress

     * @param shouldMigrate
     * @param isRestorable
     * @param user
     */
    public MigrationStatusCheckSucceeded(Common common, User user, Client client, String publicAddress, MigrationStatusCheckSucceeded.ShouldMigrate shouldMigrate, MigrationStatusCheckSucceeded.IsRestorable isRestorable, MigrationStatusCheckSucceeded.BlockchainVersion blockchainVersion) {
        super();
        this.common = common;
        this.user = user;
        this.client = client;
        this.publicAddress = publicAddress;
        this.shouldMigrate = shouldMigrate;
        this.isRestorable = isRestorable;
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
    public MigrationStatusCheckSucceeded.ShouldMigrate getShouldMigrate() {
        return shouldMigrate;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setShouldMigrate(MigrationStatusCheckSucceeded.ShouldMigrate shouldMigrate) {
        this.shouldMigrate = shouldMigrate;
    }

    /**
     * 
     * (Required)
     * 
     */
    public MigrationStatusCheckSucceeded.IsRestorable getIsRestorable() {
        return isRestorable;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setIsRestorable(MigrationStatusCheckSucceeded.IsRestorable isRestorable) {
        this.isRestorable = isRestorable;
    }

    /**
     * 
     * (Required)
     * 
     */
    public MigrationStatusCheckSucceeded.BlockchainVersion getBlockchainVersion() {
        return blockchainVersion;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setBlockchainVersion(MigrationStatusCheckSucceeded.BlockchainVersion blockchainVersion) {
        this.blockchainVersion = blockchainVersion;
    }

    public enum BlockchainVersion {

        @SerializedName("2")
        _2("2"),
        @SerializedName("3")
        _3("3");
        private final String value;
        private final static Map<String, MigrationStatusCheckSucceeded.BlockchainVersion> CONSTANTS = new HashMap<String, MigrationStatusCheckSucceeded.BlockchainVersion>();

        static {
            for (MigrationStatusCheckSucceeded.BlockchainVersion c: values()) {
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

        public static MigrationStatusCheckSucceeded.BlockchainVersion fromValue(String value) {
            MigrationStatusCheckSucceeded.BlockchainVersion constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum IsRestorable {

        @SerializedName("yes")
        YES("yes"),
        @SerializedName("no")
        NO("no");
        private final String value;
        private final static Map<String, MigrationStatusCheckSucceeded.IsRestorable> CONSTANTS = new HashMap<String, MigrationStatusCheckSucceeded.IsRestorable>();

        static {
            for (MigrationStatusCheckSucceeded.IsRestorable c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private IsRestorable(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static MigrationStatusCheckSucceeded.IsRestorable fromValue(String value) {
            MigrationStatusCheckSucceeded.IsRestorable constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum ShouldMigrate {

        @SerializedName("yes")
        YES("yes"),
        @SerializedName("no")
        NO("no");
        private final String value;
        private final static Map<String, MigrationStatusCheckSucceeded.ShouldMigrate> CONSTANTS = new HashMap<String, MigrationStatusCheckSucceeded.ShouldMigrate>();

        static {
            for (MigrationStatusCheckSucceeded.ShouldMigrate c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private ShouldMigrate(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static MigrationStatusCheckSucceeded.ShouldMigrate fromValue(String value) {
            MigrationStatusCheckSucceeded.ShouldMigrate constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
