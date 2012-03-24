package hu.meza;

import org.junit.Assert;
import org.junit.Test;

public class RegulatedResponseTest {

	@Test
	public void testSuccess() throws Exception {
		RegulatedResponse regulatedResponse = new RegulatedResponse(true);
		Assert.assertTrue(regulatedResponse.success());

		regulatedResponse = new RegulatedResponse(false);
		Assert.assertFalse(regulatedResponse.success());
	}

	@Test
	public void testException() throws Exception {
		Exception e = new RuntimeException();
		RegulatedResponse regulatedResponse = new RegulatedResponse(false, e);
		Assert.assertSame(regulatedResponse.exception(), e);
	}
}
