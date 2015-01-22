package simpleui.util;

import simpleui.util.BleTriggerOnDistance.BleDeviceInRangeListener;
import android.bluetooth.BluetoothDevice;

/**
 * based on the id of a BLE device you can trigger commands. As soon as the user
 * gets close to a device the command is triggered once. when another device
 * gets in range the command for that other device is triggered
 */
public class BleCommandSystem {

	private final BleTriggerOnDistance trigger;
	private final float maxRangeInPercent;
	private String lastFoundBleDeviceId;
	private Command commandToTriggerOnNextRefresh;

	public BleCommandSystem(int updateSpeedInMs, float maxRangeInPercent) {
		trigger = new BleTriggerOnDistance(updateSpeedInMs) {
			@Override
			protected void onScanReset() {
				if (commandToTriggerOnNextRefresh != null) {
					commandToTriggerOnNextRefresh.execute();
					commandToTriggerOnNextRefresh = null;
				}
			}
		};
		this.maxRangeInPercent = maxRangeInPercent;
	}

	public void addCommand(String bleDeviceId, final Command command) {
		trigger.addBleDeviceFoundListener(bleDeviceId,
				new BleDeviceInRangeListener(maxRangeInPercent) {

					@Override
					public boolean onDeviceInRange(String deviceId,
							Integer deviceRssi, BluetoothDevice device,
							long totalTimeRunningInMs,
							float currentRangeInPercent) {
						if (!deviceId.equals(lastFoundBleDeviceId)) {
							lastFoundBleDeviceId = deviceId;
							commandToTriggerOnNextRefresh = command;
						}
						return true;
					}
				});
	}

	public void startWatching() {
		trigger.startWatching();
	}

	public void stopWatching() {
		trigger.stopWatching();
	}

}
