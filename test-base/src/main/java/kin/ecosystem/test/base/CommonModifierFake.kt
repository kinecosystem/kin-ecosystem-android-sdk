package kin.ecosystem.test.base

import com.kin.ecosystem.core.bi.EventsStore.CommonModifier
import com.kin.ecosystem.core.bi.EventsStore.DynamicValue
import com.kin.ecosystem.core.bi.events.CommonProxy
import java.util.UUID

class CommonModifierFake : CommonModifier {

    private var eventId: UUID? = null
    private var version: String? = null
    private var userId: String? = null
    private var timestamp: Long = 0

    init {
        this.eventId = UUID.randomUUID()
        this.version = "test_version"
        this.userId = "test_user_id"
        this.timestamp = System.currentTimeMillis()
    }

    override fun modify(mutable: CommonProxy) {
        mutable.setEventId { eventId }
        mutable.setVersion { version }
        mutable.setUserId { userId }
        mutable.setTimestamp { timestamp }
    }
}
