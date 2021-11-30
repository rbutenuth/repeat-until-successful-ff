package de.codecentric.mule.rusff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.event.Event;
import org.mule.runtime.api.exception.MuleException;

public class RepeatModuleTests extends MuleArtifactFunctionalTestCase {

	@Override
	protected String getConfigFile() {
		return "test-flows.xml";
	}
	
	@Test
	public void successCase() throws Exception {
		Event event = flowRunner("success-case").run();
		String payload = (String) event.getMessage().getPayload().getValue();
		assertEquals("huhu", payload);
	}
	
	@Test
	public void dataWeaveWithError() throws Exception {
		try {
			flowRunner("dataweave-with-error").run();
			fail("Exception missing");
		} catch (MuleException e) {
			assertEquals("MULE:EXPRESSION", e.getInfo().get(MuleException.INFO_ERROR_TYPE_KEY).toString());
		}
	}
	
	@Test
	public void dataWeaveWithErrorSeveralTries() throws Exception {
		long start = System.currentTimeMillis();
		try {
			flowRunner("dataweave-with-error-several-tries").run();
			fail("Exception missing");
		} catch (MuleException e) {
			assertEquals("MULE:EXPRESSION", e.getInfo().get(MuleException.INFO_ERROR_TYPE_KEY).toString());
		}
		long end = System.currentTimeMillis();
		// we can't expect to exactly 100ms for one retry.
		assertEquals(100, end - start, 50);
	}
}
