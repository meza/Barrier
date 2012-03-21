package hu.meza.stubs;

import hu.meza.RegulatedCommand;
import hu.meza.RegulatedResponse;

public class StubCommand implements RegulatedCommand {

	private final RegulatedResponse rsp;

	public StubCommand() {
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
		return rsp;
	}

	@Override
	public RegulatedResponse response() {
		return rsp;
	}
}
