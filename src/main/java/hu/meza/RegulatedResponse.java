package hu.meza;

public interface RegulatedResponse {
	public boolean success();

	public void succeed(boolean answer);

	public void receivedException(Exception e);

	Exception exception();
}
