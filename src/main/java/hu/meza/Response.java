package hu.meza;

public class Response {
	private boolean success = false;
	private Throwable error;
	private Object result;

	public Response(Object result, boolean success, Throwable e) {
		this.result = result;
		this.success = success;
		error = e;
	}

	public Response(Object result, boolean success) {
		this.result = result;
		this.success = success;
	}

	public Response(Object result) {
		this(result, true);
	}

	public boolean success() {
		return success;
	}

	public Throwable exception() {
		return error;
	}

	public Object result() {
		return result;
	}
}
