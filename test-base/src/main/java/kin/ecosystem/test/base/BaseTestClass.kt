package kin.ecosystem.test.base

import com.kin.ecosystem.core.bi.EventsStore
import org.junit.Before


open class BaseTestClass {

    protected val userModifierFake = UserModifierFake()
    protected val commonModifierFake = CommonModifierFake()
    protected val clientModifierFake = ClientModifierFake()

    @Before
    @Throws(Exception::class)
    open fun setUp() {
        setUpEventsCommonData()
    }

    private fun setUpEventsCommonData() {
        EventsStore.init(userModifierFake, commonModifierFake, clientModifierFake)
    }
}
