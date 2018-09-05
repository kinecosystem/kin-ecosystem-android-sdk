package kin.ecosystem.test.base

import com.kin.ecosystem.core.bi.EventsStore.UserModifier
import com.kin.ecosystem.core.bi.events.UserProxy

class UserModifierFake : UserModifier {

    private var digitalServiceUserId: String = "test_digital_service_user_id"
    private var balance: Double = 20.0
    private var earnCount: Int = 2
    private var totalKinSpent: Double = 0.0
    private var digitalServiceId: String = "test_digital_service_id"
    private var transactionCount: Int = 0
    private var entryPointParam: String = "test_entry_point"
    private var spendCount: Int = 0
    private var totalKinEarned: Double = 20.0

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
