package com.kin.ecosystem;

import com.kin.ecosystem.bi.EventsStore.CommonModifier;
import com.kin.ecosystem.bi.EventsStore.DynamicValue;
import com.kin.ecosystem.bi.events.CommonProxy;
import java.util.UUID;

public class CommonModifierFake implements CommonModifier {

	private UUID eventId;
	private String version;
	private String userId;
	private long timestamp;

	public CommonModifierFake() {
		this.eventId = UUID.randomUUID();
		this.version = "test_version";
		this.userId = "test_user_id";
		this.timestamp = System.currentTimeMillis();
	}

	@Override
	public void modify(CommonProxy mutable) {
		mutable.setEventId(new DynamicValue<UUID>() {
			@Override
			public UUID get() {
				return eventId;
			}
		});
		mutable.setVersion(new DynamicValue<String>() {
			@Override
			public String get() {
				return version;
			}
		});
		mutable.setUserId(new DynamicValue<String>() {
			@Override
			public String get() {
				return userId;
			}
		});
		mutable.setTimestamp(new DynamicValue<Long>() {
			@Override
			public Long get() {
				return timestamp;
			}
		});
	}

	public void setEventId(UUID eventId) {
		this.eventId = eventId;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
