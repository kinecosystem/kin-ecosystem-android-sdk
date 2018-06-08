package kin.ecosystem.core.bi;

import kin.ecosystem.core.bi.events.Common;
import kin.ecosystem.core.bi.events.Common.EventType;
import kin.ecosystem.core.bi.events.Common.Platform;
import kin.ecosystem.core.bi.events.User;

public final class Store {
    public static final User user;
    public static final Common common;

    static {
        user = new User(
            "digitalServiceUserId",
            "digitalServiceName",
            0.0,
            0,
            0.0,
            "digitalServiceId",
            0,
            "entryPointParam",
            0,
            0.0);

        common = new Common(
            "eventId",
            "os",
            "version",
            "language",
            "carrier",
            "deviceIc",
            EventType.ANALYTICS,
            "latitude",
            "userId",
            (double) System.currentTimeMillis(),
            "city",
            "deviceType",
            "longitude",
            "country",
            "ipAddress",
            Platform.WEB,
            "region",
            "deviceManufacturer",
            "deviceModel",
            0.0);
    }
}
