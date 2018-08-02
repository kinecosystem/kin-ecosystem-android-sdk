package kin.ecosystem.test.base;

import com.kin.ecosystem.core.bi.EventsStore;
import org.junit.Before;


public class BaseTestClass {

	protected UserModifierFake userModifierFake = new UserModifierFake();
	protected CommonModifierFake commonModifierFake = new CommonModifierFake();
	protected ClientModifierFake clientModifierFake = new ClientModifierFake();

	@Before
	public void setUp() throws Exception {
		setUpEventsCommonData();
	}

	private void setUpEventsCommonData() {
		EventsStore.init(userModifierFake, commonModifierFake, clientModifierFake);
	}
}
