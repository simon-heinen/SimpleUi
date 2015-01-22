package simpleui.util;

import android.bluetooth.BluetoothDevice;

/**
 * A special case of the {@link BleTrigger} which filters based on a percentage
 * concept to trigger events when a ble device is close to the user, so its not
 * enough that the device is found but it has to be in a certain distance
 * instead. This distance is specified in percent instead of absolute units to
 * compensate the unreliability of RSSI values and define a simpler unit. The
 * definition of the range is percentage is that when the ble device in in a
 * value range which is in the bottom 20% of the total possible value range then
 * trigger the event. So if you pass 20% for the trigger it will trigger on
 * close proximity, if you pass 100% it will trigger every time
 *
 */
public class BleTriggerOnDistance extends
		BleTrigger<BleTriggerOnDistance.BleDeviceInRangeListener> {

	public static final String LOG_TAG = BleTriggerOnDistance.class
			.getSimpleName();

	public static abstract class BleDeviceInRangeListener implements
			simpleui.util.BleTrigger.BleDeviceFoundListener {

		private float minRssi = 50;
		private float maxRssi = 100;
		private final float maxRangeInPercent;

		/**
		 * @param maxRangeInPercent
		 *            for close range triggers pass 20 here, if you want to get
		 *            every event you would have to pass 100
		 */
		public BleDeviceInRangeListener(float maxRangeInPercent) {
			this.maxRangeInPercent = maxRangeInPercent;
		}

		@Override
		public boolean onDeviceFound(String deviceId, Integer deviceRssi,
				BluetoothDevice device, long totalTimeRunningInMs) {
			if (deviceRssi != null) {
				deviceRssi = Math.abs(deviceRssi);
				if (deviceRssi < minRssi) {
					minRssi = deviceRssi;
				}
				if (deviceRssi > maxRssi) {
					maxRssi = deviceRssi;
				}
				float currentPercent = (deviceRssi - minRssi)
						/ (maxRssi - minRssi) * 100;
				if (currentPercent <= maxRangeInPercent) {
					return onDeviceInRange(deviceId, deviceRssi, device,
							totalTimeRunningInMs, currentPercent);
				} else {
					return onDeviceFoundButNotInRange(deviceId, deviceRssi,
							device, totalTimeRunningInMs, currentPercent);
				}
			}
			return true;
		}

		public abstract boolean onDeviceInRange(String deviceId,
				Integer deviceRssi, BluetoothDevice device,
				long totalTimeRunningInMs, float currentRangeInPercent);

		protected boolean onDeviceFoundButNotInRange(String deviceId,
				Integer deviceRssi, BluetoothDevice device,
				long totalTimeRunningInMs, float currentRangeInPercent) {
			return true;
		}

	}

	public BleTriggerOnDistance(int updateSpeedInMs) {
		super(updateSpeedInMs);
	}

}
