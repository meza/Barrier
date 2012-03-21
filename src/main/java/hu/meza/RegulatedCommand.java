package hu.meza;

public interface RegulatedCommand {
	public RegulatedResponse execute() throws Exception;

	RegulatedResponse response();
}
