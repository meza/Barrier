package hu.meza.tools.barrier;

import org.junit.Assert;
import org.junit.Test;

public class ResponseTest {

	@Test
	public void testResult() throws Exception {
		String retval = "xx";
		Response response = new Response(retval);

		Assert.assertTrue(response.success());
		Assert.assertSame(retval, response.result());
	}

	@Test
	public void testSuccess() throws Exception {
		Response response = new Response(null, true);
		Assert.assertTrue(response.success());

		response = new Response(null, false);
		Assert.assertFalse(response.success());
	}

	@Test
	public void testException() throws Exception {
		Exception e = new RuntimeException();
		Response response = new Response(null, false, e);

		Assert.assertSame(response.exception(), e);
	}
}
