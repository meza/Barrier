package hu.meza.stubs;

import hu.meza.RegulatedCommand;
import hu.meza.RegulatedResponse;

/**
 * Created by IntelliJ IDEA.
 * User: mmmesz
 * Date: 21/03/12
 * Time: 12:30
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionThrowingStubCommand implements RegulatedCommand {

	private final Exception exception;
	private final RegulatedResponse rsp;

	public ExceptionThrowingStubCommand(Exception toBeThrown) {
		exception = toBeThrown;
		class Resp implements RegulatedResponse {

			private Exception exception;
			private boolean success;

			@Override
			public boolean success() {
				return success;
			}

			@Override
			public void succeed(boolean answer) {
				success = answer;
			}

			@Override
			public void receivedException(Exception e) {
				this.exception = e;
			}

			@Override
			public Exception exception() {
				return exception;
			}

		}

		rsp = new Resp();
	}

	@Override
	public RegulatedResponse execute() throws Exception {
		throw exception;
	}

	@Override
	public RegulatedResponse response() {
		return rsp;
	}
}
