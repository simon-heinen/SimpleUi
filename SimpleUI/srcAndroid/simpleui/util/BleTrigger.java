package simpleui.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import simpleui.util.Log;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleTrigger {
	protected static final String LOG_TAG = BleTrigger.class.getSimpleName();

	public interface BleDeviceFoundListener {

		/**
		 * @param deviceId
		 *            the unique device id
		 * @param deviceRssi
		 *            The RSSI value is basically the negative signal strength
		 *            (example: it will be around -40 when the device is really
		 *            close and will decrease to -90 for 3 meters distance). It
		 *            will be null if no RSSI value is available for that device
		 * @param device
		 *            if you need the other values of the found device like the
		 *            Name
		 * @param totalTimeRunningInMs
		 * @return
		 */
		boolean onDeviceFound(String deviceId, Integer deviceRssi,
				BluetoothDevice device, long totalTimeRunningInMs);

	}

	private static final String DEVICE_ID_ANY_DEVICE = "";
	private Timer timer;
	private final int updateSpeedInMs;
	private final Map<String, BleDeviceFoundListener> listeners = new HashMap<String, BleTrigger.BleDeviceFoundListener>();
	protected LeScanCallback scanCallback;
	private long startTime;

	public BleTrigger(int updateSpeedInMs) {
		if (updateSpeedInMs < 1000) {
			Log.w(LOG_TAG, "Update intervals faster than "
					+ "1 second are not allowed");
			updateSpeedInMs = 1000;
		}
		this.updateSpeedInMs = updateSpeedInMs;
	}

	public boolean startWatching() {
		final BluetoothAdapter ble = BluetoothAdapter.getDefaultAdapter();
		if (enableBluetoothIfNotOn(ble)) {
			if (timer != null) {
				Log.w(LOG_TAG, "startWatching called but this BleTrigger is "
						+ "already watching! Call stopWatching() first..");
				return false;
			}
			timer = new Timer();
			TimerTask task = new TimerTask() {

				@SuppressWarnings("deprecation")
				@Override
				public void run() {

					if (scanCallback != null) {
						ble.stopLeScan(scanCallback);
					}

					scanCallback = new LeScanCallback() {

						@Override
						public void onLeScan(BluetoothDevice device, int rssi,
								byte[] scanRecord) {
							String deviceId = device.getAddress();

							long totalTimeRunning = System.currentTimeMillis()
									- startTime;
							boolean keepRunning = true;
							BleDeviceFoundListener l = listeners.get(deviceId);
							if (l != null) {
								keepRunning = l.onDeviceFound(deviceId,
										rssi != 0 ? rssi : null, device,
										totalTimeRunning);
							}
							BleDeviceFoundListener anyDeviceListener = listeners
									.get(DEVICE_ID_ANY_DEVICE);
							if (anyDeviceListener != null) {
								keepRunning = anyDeviceListener.onDeviceFound(
										deviceId, rssi, device,
										totalTimeRunning);
							}
							if (!keepRunning) {
								stopWatching();
							}
						}

					};
					if (!ble.startLeScan(scanCallback)) {
						Log.w(LOG_TAG, "Could not start bluetooth scan");
					}
				}
			};
			startTime = System.currentTimeMillis();
			timer.schedule(task, 0, updateSpeedInMs);
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public void stopWatching() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (scanCallback != null) {
			BluetoothAdapter.getDefaultAdapter().stopLeScan(scanCallback);
			scanCallback = null;
		}
	}

	private boolean enableBluetoothIfNotOn(BluetoothAdapter ble) {
		if (!ble.isEnabled()) {
			Log.i(LOG_TAG, "Bluetooth was not yet enabled");
			boolean blEnabled = ble.enable();
			Log.i(LOG_TAG, "  > Enabled bluetooth: " + blEnabled);
			return blEnabled;
		}
		return true;
	}

	public boolean hasDeviceBleSupport(Context context) {
		return context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE);
	}

	/**
	 * will inform the passed {@link BleDeviceFoundListener} of all found
	 * devices
	 * 
	 * @param bleDeviceFoundListener
	 * @return the replaced listener if there was already one set before
	 */
	public BleDeviceFoundListener addBleDeviceFoundListener(
			BleDeviceFoundListener bleDeviceFoundListener) {
		return addBleDeviceFoundListener(DEVICE_ID_ANY_DEVICE,
				bleDeviceFoundListener);
	}

	/**
	 * will inform the passed listener when the specified device was found in
	 * range
	 * 
	 * @param deviceId
	 * @param bleDeviceFoundListener
	 * @return the replaced listener if for the same deviceId there was already
	 *         one
	 */
	public BleDeviceFoundListener addBleDeviceFoundListener(String deviceId,
			BleDeviceFoundListener bleDeviceFoundListener) {
		if (deviceId == null) {
			deviceId = DEVICE_ID_ANY_DEVICE;
		}
		return listeners.put(deviceId, bleDeviceFoundListener);
	}

}
