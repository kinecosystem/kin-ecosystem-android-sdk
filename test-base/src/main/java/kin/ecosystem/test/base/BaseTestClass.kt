package kin.ecosystem.test.base

import com.kin.ecosystem.core.bi.EventsStore
import org.junit.Before


open class BaseTestClass {

    private val userModifierFake = UserModifierFake()
    private val commonModifierFake = CommonModifierFake()
    private val clientModifierFake = ClientModifierFake()

    @Before
    @Throws(Exception::class)
    open fun setUp() {
        setUpEventsCommonData()
    }

    private fun setUpEventsCommonData() {
        EventsStore.init(userModifierFake, commonModifierFake, clientModifierFake)
    }
}
