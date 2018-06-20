package com.kin.ecosystem;

import com.kin.ecosystem.bi.EventsStore.ClientModifier;
import com.kin.ecosystem.bi.EventsStore.DynamicValue;
import com.kin.ecosystem.bi.events.ClientProxy;

public class ClientModifierFake implements ClientModifier {

	private String os;
	private String language;
	private String carrier;
	private String deviceId;
	private String deviceManufacturer;
	private String deviceModel;

	public ClientModifierFake() {
		this.os = "android";
		this.language = "english";
		this.carrier = "test_carrier";
		this.deviceId = "test_device_id";
		this.deviceManufacturer = "test_manufacturer";
		this.deviceModel = "test_device_model";
	}

	@Override
	public void modify(ClientProxy mutable) {
		mutable.setOs(new DynamicValue<String>() {
			@Override
			public String get() {
				return os;
			}
		});
		mutable.setLanguage(new DynamicValue<String>() {
			@Override
			public String get() {
				return language;
			}
		});
		mutable.setCarrier(new DynamicValue<String>() {
			@Override
			public String get() {
				return carrier;
			}
		});
		mutable.setDeviceId(new DynamicValue<String>() {
			@Override
			public String get() {
				return deviceId;
			}
		});
		mutable.setDeviceManufacturer(new DynamicValue<String>() {
			@Override
			public String get() {
				return deviceManufacturer;
			}
		});
		mutable.setDeviceModel(new DynamicValue<String>() {
			@Override
			public String get() {
				return deviceModel;
			}
		});
	}

	public void setOs(String os) {
		this.os = os;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void setDeviceManufacturer(String deviceManufacturer) {
		this.deviceManufacturer = deviceManufacturer;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
}
