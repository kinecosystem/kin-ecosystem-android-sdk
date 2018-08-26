package kin.ecosystem.test.base

import com.kin.ecosystem.core.bi.EventsStore.DynamicValue
import com.kin.ecosystem.core.bi.EventsStore.UserModifier
import com.kin.ecosystem.core.bi.events.UserProxy

class UserModifierFake : UserModifier {

    private var digitalServiceUserId: String? = null
    private var balance: Double = 0.toDouble()
    private var earnCount: Int = 0
    private var totalKinSpent: Double = 0.toDouble()
    private var digitalServiceId: String? = null
    private var transactionCount: Int = 0
    private var entryPointParam: String? = null
    private var spendCount: Int = 0
    private var totalKinEarned: Double = 0.toDouble()

    init {
        this.digitalServiceUserId = "test_digital_service_user_id"
        this.balance = 20.0
        this.earnCount = 2
        this.totalKinSpent = 0.0
        this.digitalServiceId = "test_digital_service_id"
        this.transactionCount = 0
        this.entryPointParam = "test_entry_point"
        this.spendCount = 0
        this.totalKinEarned = 20.0
    }

    override fun modify(mutable: UserProxy) {
        mutable.setDigitalServiceUserId { digitalServiceUserId }
        mutable.setBalance { balance }
        mutable.setEarnCount { earnCount }
        mutable.setTotalKinSpent { totalKinSpent }
        mutable.setDigitalServiceId { digitalServiceId }
        mutable.setTransactionCount { transactionCount }
        mutable.setEntryPointParam { entryPointParam }
        mutable.setSpendCount { spendCount }
        mutable.setTotalKinEarned { totalKinEarned }
    }
}
