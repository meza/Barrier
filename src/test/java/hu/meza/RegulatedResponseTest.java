package hu.meza;

import org.junit.Assert;
import org.junit.Test;

public class RegulatedResponseTest {

	@Test
	public void testResult() throws Exception {
		String retval = "xx";
		RegulatedResponse regulatedResponse = new RegulatedResponse(retval);
		Assert.assertTrue(regulatedResponse.success());
		Assert.assertSame(retval, regulatedResponse.result());

	}
	
	@Test
	public void testSuccess() throws Exception {
		RegulatedResponse regulatedResponse = new RegulatedResponse(null, true);
		Assert.assertTrue(regulatedResponse.success());

		regulatedResponse = new RegulatedResponse(null, false);
		Assert.assertFalse(regulatedResponse.success());
	}

	@Test
	public void testException() throws Exception {
		Exception e = new RuntimeException();
		RegulatedResponse regulatedResponse = new RegulatedResponse(null, false, e);
		Assert.assertSame(regulatedResponse.exception(), e);
	}
}
