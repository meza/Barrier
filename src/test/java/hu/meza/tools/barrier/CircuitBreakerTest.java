package hu.meza.tools.barrier;


import hu.meza.tools.barrier.exceptions.CircuitBrokenException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.ConnectException;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CircuitBreakerTest {

	private CircuitBreaker cb;
	private CoolDownStrategy coolDownStrategy;
	private TriggerStrategy triggerStrategy;

	@Before
	public void setUp() throws Exception {
		coolDownStrategy = mock(CoolDownStrategy.class);
		when(coolDownStrategy.isCool()).thenReturn(true);

		triggerStrategy = mock(TriggerStrategy.class);
		when(triggerStrategy.isBreaker(any(Throwable.class))).thenReturn(false);

		cb = new CircuitBreaker(coolDownStrategy, triggerStrategy);
	}

	@Test
	public void executeTest() throws Throwable {
		Command cmd = getACommandMock();
		cb.execute(cmd);

		verify(cmd, times(1)).execute();
	}

	@Test
	public void executeResultTest() throws Throwable {
		Command cmd = getACommandMock();
		String returnStr = "return";
		when(cmd.execute()).thenReturn(returnStr);

		Assert.assertSame(returnStr, cb.execute(cmd).result());

		verify(cmd, times(1)).execute();
	}

	@Test
	public void testSuccessIsTrue() {
		Command cmd = getACommandMock();
		Response rsp = cb.execute(cmd);

		Assert.assertTrue(rsp.success());
	}

	@Test
	public void executeMultipleCommandsTest() throws Throwable {
		Command cmd = getACommandMock();
		Command cmd2 = getACommandMock();

		cb.execute(cmd);
		cb.execute(cmd2);

		verify(cmd, times(1)).execute();
		verify(cmd2, times(1)).execute();
	}

	@Test
	public void executeMultipleCommandsWithErrorsTest() throws Throwable {
		Command faultyCommand = getACommandMock();
		doThrow(new RuntimeException()).when(faultyCommand).execute();

		Command cmd2 = getACommandMock();

		cb.execute(faultyCommand);
		cb.execute(cmd2);

		verify(faultyCommand, times(1)).execute();
		verify(cmd2, times(1)).execute();
	}

	@Test
	public void testExecutionErrorReporting() throws Throwable {

		RuntimeException toBeThrown = new RuntimeException();

		Command faultyCommand = getACommandMock();
		when(faultyCommand.execute()).thenThrow(toBeThrown);

		Response resp = cb.execute(faultyCommand);

		Assert.assertFalse(resp.success());
		Assert.assertSame(resp.exception(), toBeThrown);
	}

	@Test
	public void testCircuitBreaking() throws Throwable {
		ConnectException toBeThrown = new ConnectException();
		when(triggerStrategy.isBreaker(toBeThrown)).thenReturn(true);


		Command faultyCommand = getACommandMock();
		when(faultyCommand.execute()).thenThrow(toBeThrown);

		Command cmd1 = getACommandMock();
		Command cmd2 = getACommandMock();

		Response resp = cb.execute(faultyCommand);

		verify(coolDownStrategy, times(1)).makeHot();
		when(coolDownStrategy.isCool()).thenReturn(false);

		Response resp1 = cb.execute(cmd1);
		Response resp2 = cb.execute(cmd2);

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

	@Test
	public void testCoolDown() throws Throwable {

		ConnectException toBeThrown = new ConnectException();
		when(triggerStrategy.isBreaker(toBeThrown)).thenReturn(true);

		Command faultyCommand = getACommandMock();
		when(faultyCommand.execute()).thenThrow(toBeThrown);

		Command cmd1 = getACommandMock();
		Command cmd2 = getACommandMock();

		when(coolDownStrategy.isCool()).thenReturn(true);

		cb.execute(faultyCommand);
		verify(coolDownStrategy, times(1)).makeHot();

		when(coolDownStrategy.isCool()).thenReturn(false);

		Assert.assertFalse(cb.execute(cmd1).success());
		Assert.assertFalse(cb.execute(cmd2).success());

		when(coolDownStrategy.isCool()).thenReturn(true);

		Assert.assertTrue(cb.execute(cmd1).success());
		Assert.assertTrue(cb.execute(cmd2).success());

	}

	@Test
	public void testTrigger() throws Throwable {

		ConnectException toBeThrown = new ConnectException();
		when(triggerStrategy.isBreaker(toBeThrown)).thenReturn(true);

		Command faultyCommand = getACommandMock();
		when(faultyCommand.execute()).thenThrow(toBeThrown);

		Command cmd1 = getACommandMock();
		Command cmd2 = getACommandMock();

		Assert.assertTrue(cb.execute(cmd1).success());
		Assert.assertFalse(cb.execute(faultyCommand).success());

		verify(coolDownStrategy, times(1)).makeHot();
		when(coolDownStrategy.isCool()).thenReturn(false);

		Assert.assertFalse(cb.execute(cmd2).success());

		verify(triggerStrategy, times(1)).isBreaker(toBeThrown);

	}

	@Test
	public void isBroken() throws Throwable {

		when(coolDownStrategy.isCool()).thenReturn(true);
		Assert.assertFalse("Circuit breaker does not report non broken state correctly", cb.isBroken());

		when(coolDownStrategy.isCool()).thenReturn(false);
		Assert.assertTrue("Circuit breaker does not report broken state correctly", cb.isBroken());

	}

	@Test
	public void listeners() throws Throwable {
		CircuitMonitor listener = Mockito.mock(CircuitMonitor.class);

		ConnectException toBeThrown = new ConnectException();
		when(triggerStrategy.isBreaker(toBeThrown)).thenReturn(true);

		Command faultyCommand = getACommandMock();
		when(faultyCommand.execute()).thenThrow(toBeThrown);

		cb.addListener(listener);

		cb.execute(faultyCommand);

		verify(listener, Mockito.atLeastOnce()).circuitBroken();

		CircuitMonitor listener2 = Mockito.mock(CircuitMonitor.class);
		cb.addListener(listener2);
		cb.removeListener(listener2);
		cb.execute(faultyCommand);

		verify(listener2, Mockito.never()).circuitBroken();
	}

	private Command getACommandMock() {
		return mock(Command.class);
	}
}
