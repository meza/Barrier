package hu.meza;

import hu.meza.cooldownstrategies.CoolDownStrategy;
import hu.meza.exceptions.CircuitBrokenException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.ConnectException;

import static org.mockito.Mockito.*;

public class CircuitBreakerTest {

	private CircuitBreaker cb;
	private CoolDownStrategy coolDownStrategy;
	private TriggerStrategy triggerStrategy;

	@Before
	public void setUp() throws Exception {
		coolDownStrategy = mock(CoolDownStrategy.class);
		when(coolDownStrategy.cool()).thenReturn(false);

		triggerStrategy = mock(TriggerStrategy.class);
		when(triggerStrategy.isBreaker(any(Throwable.class))).thenReturn(false);

		cb = new CircuitBreaker(coolDownStrategy, triggerStrategy);
	}

	@Test
	public void executeTest() throws Throwable {
		RegulatedCommand cmd = getACommandMock();
		cb.execute(cmd);

		verify(cmd, times(1)).execute();
	}

	@Test
	public void testSuccessIsTrue() {
		RegulatedCommand cmd = getACommandMock();
		RegulatedResponse rsp = cb.execute(cmd);

		Assert.assertTrue(rsp.success());
	}

	@Test
	public void executeMultipleCommandsTest() throws Throwable {
		RegulatedCommand cmd = getACommandMock();
		RegulatedCommand cmd2 = getACommandMock();

		cb.execute(cmd);
		cb.execute(cmd2);

		verify(cmd, times(1)).execute();
		verify(cmd2, times(1)).execute();
	}

	@Test
	public void executeMultipleCommandsWithErrorsTest() throws Throwable {
		RegulatedCommand faultyCommand = getACommandMock();
		doThrow(new RuntimeException()).when(faultyCommand).execute();

		RegulatedCommand cmd2 = getACommandMock();

		cb.execute(faultyCommand);
		cb.execute(cmd2);

		verify(faultyCommand, times(1)).execute();
		verify(cmd2, times(1)).execute();
	}

	@Test
	public void testExecutionErrorReporting() throws Throwable {

		RuntimeException toBeThrown = new RuntimeException();

		RegulatedCommand faultyCommand = getACommandMock();
		when(faultyCommand.execute()).thenThrow(toBeThrown);

		RegulatedResponse resp = cb.execute(faultyCommand);

		Assert.assertFalse(resp.success());
		Assert.assertSame(resp.exception(), toBeThrown);
	}

	@Test
	public void testCircuitBreaking() throws Throwable {
		ConnectException toBeThrown = new ConnectException();
		when(triggerStrategy.isBreaker(toBeThrown)).thenReturn(true);


		RegulatedCommand faultyCommand = getACommandMock();
		when(faultyCommand.execute()).thenThrow(toBeThrown);

		RegulatedCommand cmd1 = getACommandMock();
		RegulatedCommand cmd2 = getACommandMock();

		RegulatedResponse resp = cb.execute(faultyCommand);
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

	@Test
	public void testCoolDown() throws Throwable {

		ConnectException toBeThrown = new ConnectException();
		when(triggerStrategy.isBreaker(toBeThrown)).thenReturn(true);

		RegulatedCommand faultyCommand = getACommandMock();
		when(faultyCommand.execute()).thenThrow(toBeThrown);

		RegulatedCommand cmd1 = getACommandMock();
		RegulatedCommand cmd2 = getACommandMock();

		when(coolDownStrategy.cool()).thenReturn(false);

		cb.execute(faultyCommand);
		verify(coolDownStrategy, times(1)).trigger();

		Assert.assertFalse(cb.execute(cmd1).success());
		Assert.assertFalse(cb.execute(cmd2).success());

		when(coolDownStrategy.cool()).thenReturn(true);

		Assert.assertTrue(cb.execute(cmd1).success());
		Assert.assertTrue(cb.execute(cmd2).success());

	}

	@Test
	public void testTrigger() throws Throwable {

		ConnectException toBeThrown = new ConnectException();
		when(triggerStrategy.isBreaker(toBeThrown)).thenReturn(true);

		RegulatedCommand cmd = getACommandMock();
		when(cmd.execute()).thenThrow(toBeThrown);
		RegulatedCommand cmd1 = getACommandMock();
		RegulatedCommand cmd2 = getACommandMock();

		Assert.assertTrue(cb.execute(cmd1).success());
		Assert.assertFalse(cb.execute(cmd).success());
		Assert.assertFalse(cb.execute(cmd2).success());

		verify(triggerStrategy, times(1)).isBreaker(toBeThrown);

	}

	private RegulatedCommand getACommandMock() {
		return mock(RegulatedCommand.class);
	}
}


