package hu.meza;

public class RegulatedResponse {
	private boolean success = false;
	private Throwable error;

	public RegulatedResponse(boolean success, Throwable e) {
		this.success = success;
		error = e;
	}

	public RegulatedResponse(boolean success) {
		this.success = success;
	}

	public boolean success() {
		return success;
	}

	public Throwable exception() {
		return error;
	}
}
