package simpleui.examples.bleTests;

import simpleui.util.BleTrigger;
import simpleui.util.BleTrigger.BleDeviceFoundListener;
import simpleui.util.BleTriggerOnDistance;
import simpleui.util.BleTriggerOnDistance.BleDeviceInRangeListener;
import simpleui.util.BleTriggerSystem;
import simpleui.util.BleTriggerSystemAndroid;
import simpleui.util.Log;
import android.bluetooth.BluetoothDevice;

public class BleTest {

	public static void startBasicTest() {
		BleTrigger<BleDeviceFoundListener> ble = new BleTrigger(1000);
		ble.addBleDeviceFoundListener(new BleDeviceFoundListener() {
			@Override
			public boolean onDeviceFound(String deviceId, Integer deviceRssi,
					BluetoothDevice device, long totalTimeRunningInMs) {
				Log.i("totalTimeRunningInMs=" + totalTimeRunningInMs);
				Log.i("device.getName()=" + device.getName());
				Log.i("deviceId=" + deviceId);
				Log.i("deviceRssi=" + deviceRssi);
				boolean keepRunning = totalTimeRunningInMs < 60 * 1000;
				return keepRunning; // stop after one minute
			}

		});
		ble.startWatching();
	}

	public static void startTriggerTest() {
		BleTriggerOnDistance ble = new BleTriggerOnDistance(2000);
		ble.addBleDeviceFoundListener(new BleDeviceInRangeListener(20) {

			@Override
			public boolean onDeviceInRange(String deviceId, Integer deviceRssi,
					BluetoothDevice device, long totalTimeRunningInMs,
					float currentRangeInPercent) {
				Log.i("totalTimeRunningInMs=" + totalTimeRunningInMs);
				Log.i("device.getName()=" + device.getName());
				Log.i("deviceId=" + deviceId);
				Log.i("deviceRssi=" + deviceRssi);
				Log.i("currentDistancePercent=" + currentRangeInPercent);
				boolean keepRunning = totalTimeRunningInMs < 60 * 1000;
				return keepRunning; // stop after one minute
			}

			@Override
			protected boolean onDeviceFoundButNotInRange(String deviceId,
					Integer deviceRssi, BluetoothDevice device,
					long totalTimeRunningInMs, float currentRangeInPercent) {
				Log.w("currentDistancePercent=" + currentRangeInPercent);
				boolean keepRunning = totalTimeRunningInMs < 60 * 1000;
				return keepRunning; // stop after one minute
			}

		});
		ble.startWatching();
	}

	public static void startCommandTriggerSystem() {
		BleTriggerSystem s = new BleTriggerSystemAndroid(1000);
		int distanceInPercent = 20;
		s.addCommand("BC:6A:29:AB:C2:62", distanceInPercent,
				new simpleui.util.Command() {

					@Override
					public boolean execute() {
						Log.d("Command triggered for " + "BC:6A:29:AB:C2:62");
						return true;
					}
				});
		s.addCommand("BC:6A:29:48:F9:D2", distanceInPercent,
				new simpleui.util.Command() {

					@Override
					public boolean execute() {
						Log.d("Command triggered for " + "BC:6A:29:48:F9:D2");
						return true;
					}
				});
		s.startWatching();

	}

}
