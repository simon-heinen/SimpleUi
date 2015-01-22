package simpleui.examples.bleTests;

import simpleui.util.BleTrigger;
import simpleui.util.BleTrigger.BleDeviceFoundListener;
import simpleui.util.BleTriggerOnDistance;
import simpleui.util.BleTriggerOnDistance.BleDeviceInRangeListener;
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

}
