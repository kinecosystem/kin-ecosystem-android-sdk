package kin.ecosystem.test.base

import com.kin.ecosystem.core.bi.EventsStore.ClientModifier
import com.kin.ecosystem.core.bi.EventsStore.DynamicValue
import com.kin.ecosystem.core.bi.events.ClientProxy

class ClientModifierFake : ClientModifier {

    private var os: String = "android"
    private var language: String = "english"
    private var carrier: String = "test_carrier"
    private var deviceId: String = "test_device_id"
    private var deviceManufacturer: String = "test_manufacturer"
    private var deviceModel: String = "test_device_model"

    override fun modify(mutable: ClientProxy) {
        mutable.setOs { os }
        mutable.setLanguage { language }
        mutable.setCarrier { carrier }
        mutable.setDeviceId { deviceId }
        mutable.setDeviceManufacturer { deviceManufacturer }
        mutable.setDeviceModel { deviceModel }
    }
}
