package kin.ecosystem.test.base

import com.kin.ecosystem.core.bi.EventsStore.ClientModifier
import com.kin.ecosystem.core.bi.EventsStore.DynamicValue
import com.kin.ecosystem.core.bi.events.ClientProxy

class ClientModifierFake : ClientModifier {

    private var os: String? = null
    private var language: String? = null
    private var carrier: String? = null
    private var deviceId: String? = null
    private var deviceManufacturer: String? = null
    private var deviceModel: String? = null

    init {
        this.os = "android"
        this.language = "english"
        this.carrier = "test_carrier"
        this.deviceId = "test_device_id"
        this.deviceManufacturer = "test_manufacturer"
        this.deviceModel = "test_device_model"
    }

    override fun modify(mutable: ClientProxy) {
        mutable.setOs { os }
        mutable.setLanguage { language }
        mutable.setCarrier { carrier }
        mutable.setDeviceId { deviceId }
        mutable.setDeviceManufacturer { deviceManufacturer }
        mutable.setDeviceModel { deviceModel }
    }
}
