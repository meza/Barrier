package hu.meza;

import hu.meza.exceptions.CircuitBrokenException;
import hu.meza.stubs.ExceptionThrowingStubCommand;
import hu.meza.stubs.StubCommand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.ConnectException;

import static org.mockito.Mockito.*;

public class CircuitBreakerTest {

	private CircuitBreaker cb;

	@Before
	public void setUp() throws Exception {
		cb = new CircuitBreaker();
	}

	@Test
	public void executeTest() throws Exception {
		RegulatedCommand cmd = getACommandMock();
		cb.execute(cmd);

		verify(cmd, times(1)).execute();
	}

	@Test
	public void testSuccessIsTrue() {
		RegulatedCommand cmd = new StubCommand();
		RegulatedResponse resp = cb.execute(cmd);
		Assert.assertTrue(resp.success());
	}
	
	@Test
	public void executeMultipleCommandsTest() throws Exception {
		RegulatedCommand cmd = getACommandMock();
		RegulatedCommand cmd2 = getACommandMock();

		cb.execute(cmd);
		cb.execute(cmd2);

		verify(cmd, times(1)).execute();
		verify(cmd2, times(1)).execute();
	}

	private RegulatedCommand getACommandMock() {
		RegulatedCommand cmd = mock(RegulatedCommand.class);
		when(cmd.response()).thenReturn(mock(RegulatedResponse.class));
		return cmd;
	}

	@Test
	public void executeMultipleCommandsWithErrorsTest() throws Exception {
		RegulatedCommand cmd = getACommandMock();
		RegulatedCommand cmd2 = getACommandMock();

		doThrow(new RuntimeException()).when(cmd).execute();
		
		cb.execute(cmd);
		cb.execute(cmd2);

		verify(cmd, times(1)).execute();
		verify(cmd2, times(1)).execute();
	}

	@Test
	public void testExecutionErrorReporting() {

		RuntimeException toBeThrown = new RuntimeException();
		RegulatedCommand cmd = new ExceptionThrowingStubCommand(toBeThrown);

		RegulatedResponse resp = cb.execute(cmd);

		Assert.assertFalse(cmd.response().success());
		Assert.assertSame(toBeThrown, resp.exception());
	}

	@Test
	public void testCircuitBreaking() {
		ConnectException toBeThrown = new ConnectException();
		RegulatedCommand cmd  = new ExceptionThrowingStubCommand(toBeThrown);
		RegulatedCommand cmd1 = new StubCommand();
		RegulatedCommand cmd2 = new StubCommand();

		RegulatedResponse resp = cb.execute(cmd);
		RegulatedResponse resp1 = cb.execute(cmd1);
		RegulatedResponse resp2 = cb.execute(cmd2);

		Assert.assertFalse(resp.success());
		Assert.assertNotNull(resp.exception());
		Assert.assertSame(toBeThrown, resp.exception());

		Assert.assertFalse(resp1.success());
		Assert.assertNotNull(resp1.exception());
		Assert.assertEquals(CircuitBrokenException.class, resp1.exception().getClass());

		Assert.assertFalse(resp2.success());
		Assert.assertNotNull(resp2.exception());
		Assert.assertEquals(CircuitBrokenException.class, resp2.exception().getClass());

	}
}


