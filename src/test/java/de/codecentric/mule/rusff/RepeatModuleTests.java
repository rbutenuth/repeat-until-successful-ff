package de.codecentric.mule.rusff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
		assertEquals(100, end - start, 60);
	}
	
	@Test
	public void computedSecondDelay() throws Exception {
		// one run to warm up
		try {
			flowRunner("computed-second-delay").run();
			fail("Exception missing");
		} catch (MuleException e) {
			assertEquals("MULE:EXPRESSION", e.getInfo().get(MuleException.INFO_ERROR_TYPE_KEY).toString());
		}
		// second with time measurement
		long start = System.currentTimeMillis();
		try {
			flowRunner("computed-second-delay").run();
			fail("Exception missing");
		} catch (MuleException e) {
			assertEquals("MULE:EXPRESSION", e.getInfo().get(MuleException.INFO_ERROR_TYPE_KEY).toString());
		}
		long end = System.currentTimeMillis();
		// we can't expect to exactly 100ms + 2 * 100ms for the two retries
		assertEquals(300, end - start, 60);
	}
	
	@Test
	public void defaultSecondDelay() throws Exception {
		// one run to warm up
		try {
			flowRunner("default-second-delay").run();
			fail("Exception missing");
		} catch (MuleException e) {
			assertEquals("MULE:EXPRESSION", e.getInfo().get(MuleException.INFO_ERROR_TYPE_KEY).toString());
		}
		// second with time measurement
		long start = System.currentTimeMillis();
		try {
			flowRunner("default-second-delay").run();
			fail("Exception missing");
		} catch (MuleException e) {
			assertEquals("MULE:EXPRESSION", e.getInfo().get(MuleException.INFO_ERROR_TYPE_KEY).toString());
		}
		long end = System.currentTimeMillis();
		// we can't expect to exactly 100ms + 100ms for the two retries
		assertEquals(200, end - start, 60);
	}
	
	@Test
	public void successOnSecondTry() throws Exception {
		// one run to warm up
		Boolean payload = (Boolean) flowRunner("success-on-second-try").run().getMessage().getPayload().getValue();
		assertFalse(payload);
		// second with time measurement
		long start = System.currentTimeMillis();
		payload = (Boolean) flowRunner("success-on-second-try").run().getMessage().getPayload().getValue();
		assertFalse(payload);
		long end = System.currentTimeMillis();
		// we can't expect to exactly 100ms for one retry.
		assertEquals(100, end - start, 60);
	}
}
