package simpleui.util;

/**
 * A system independent interface for BLE events, to be implemented on different
 * platforms
 */
public interface BleTriggerSystem {

	void addCommand(String bleDeviceId, float maxRangeInPercent, Command command);

	void startWatching();

	void stopWatching();

	boolean isWatching();

}
