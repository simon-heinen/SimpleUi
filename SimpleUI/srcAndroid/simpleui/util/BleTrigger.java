package simpleui.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * The idea of this trigger is that you can use ble beacons to trigger logic in
 * your application, so "when the user gets close to beacon x do y"
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleTrigger<T extends BleTrigger.BleDeviceFoundListener> {
	private static final String LOG_TAG = BleTrigger.class.getSimpleName();

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
		 * @return true if the scann should be continued or false if it should
		 *         be stopped
		 */
		boolean onDeviceFound(String deviceId, Integer deviceRssi,
				BluetoothDevice device, long totalTimeRunningInMs);

	}

	private static final String DEVICE_ID_ANY_DEVICE = "";
	private Timer timer;
	private final int updateSpeedInMs;
	private final Map<String, T> listeners = new HashMap<String, T>();
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
		if (enableBluetoothIfNotOn()) {
			if (timer != null) {
				Log.w(LOG_TAG, "startWatching called but this BleTrigger is "
						+ "already watching! Call stopWatching() first..");
				return false;
			}
			if (listeners.isEmpty()) {
				Log.w(LOG_TAG, "started listening for ble devices "
						+ "but no listeners registered yet");
			}
			timer = new Timer();
			TimerTask task = new TimerTask() {

				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					BluetoothAdapter ble = BluetoothAdapter.getDefaultAdapter();
					if (scanCallback != null) {
						ble.stopLeScan(scanCallback);
						onScanReset();
					}

					scanCallback = new LeScanCallback() {

						@Override
						public void onLeScan(BluetoothDevice device, int rssi,
								byte[] scanRecord) {
							processScanResult(device, rssi);
						}

					};
					if (!ble.startLeScan(scanCallback)) {
						Log.w(LOG_TAG, "Could not start ble scan");
						onScanStartError();
					}
				}
			};
			startTime = System.currentTimeMillis();
			timer.schedule(task, 0, updateSpeedInMs);
			return true;
		}
		Log.w(LOG_TAG, "Could not enable bluetooth, "
				+ "permission in manifest added?");
		return false;
	}

	/**
	 * called by the ble system when the scanner finds something. this method
	 * then informs all registered listeners
	 * 
	 * @param device
	 * @param rssi
	 */
	protected void processScanResult(BluetoothDevice device, int rssi) {
		String deviceId = device.getAddress();

		long totalTimeRunning = System.currentTimeMillis() - startTime;
		boolean keepRunning = true;
		T l = listeners.get(deviceId);
		if (l != null) {
			keepRunning = l.onDeviceFound(deviceId, rssi != 0 ? rssi : null,
					device, totalTimeRunning);
		}
		T anyDeviceListener = listeners.get(DEVICE_ID_ANY_DEVICE);
		if (anyDeviceListener != null) {
			keepRunning = anyDeviceListener.onDeviceFound(deviceId, rssi,
					device, totalTimeRunning);
		}
		if (!keepRunning) {
			stopWatching();
		}
	}

	/**
	 * called when the scan process could not be started
	 */
	protected void onScanStartError() {
	}

	/**
	 * Called every time the scanner resets itself and starts again to scan for
	 * updates. how often this happens is mainly defined by
	 * {@link BleTrigger#updateSpeedInMs}
	 */
	protected void onScanReset() {
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

	public static boolean enableBluetoothIfNotOn() {
		BluetoothAdapter ble = BluetoothAdapter.getDefaultAdapter();
		if (!ble.isEnabled()) {
			Log.i(LOG_TAG, "Bluetooth was not yet enabled");
			boolean blEnabled = ble.enable();
			Log.i(LOG_TAG, "  > Enabled bluetooth: " + blEnabled);
			return blEnabled;
		}
		return true;
	}

	/**
	 * @param context
	 * @return true if the device does support BLE communication
	 */
	public static boolean isDeviceCapableOfBLE(Context context) {
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
	public T addBleDeviceFoundListener(T bleDeviceFoundListener) {
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
	public T addBleDeviceFoundListener(String deviceId, T bleDeviceFoundListener) {
		if (deviceId == null) {
			deviceId = DEVICE_ID_ANY_DEVICE;
		}
		return listeners.put(deviceId, bleDeviceFoundListener);
	}

}
