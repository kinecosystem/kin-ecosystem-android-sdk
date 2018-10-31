package kin.ecosystem.test.base

import com.kin.ecosystem.core.bi.EventsStore.CommonModifier
import com.kin.ecosystem.core.bi.EventsStore.DynamicValue
import com.kin.ecosystem.core.bi.events.CommonProxy
import java.util.UUID

class CommonModifierFake : CommonModifier {

    private var eventId: UUID = UUID.randomUUID()
    private var version: String = "test_version"
    private var userId: String = "test_user_id"
    private var timestamp: Long = System.currentTimeMillis()

    override fun modify(mutable: CommonProxy) {
        mutable.setEventId { eventId }
        mutable.setVersion { version }
        mutable.setUserId { userId }
        mutable.setTimestamp { timestamp }
    }
}
