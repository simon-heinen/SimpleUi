package simpleui.util;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * a collection of functional methods for Android {@link Camera} handling
 */
public class CameraHelper {

	private static final String LOG_TAG = CameraHelper.class.getSimpleName();

	/**
	 * http://stackoverflow.com/questions/3841122/android-camera-preview-is-
	 * sideways
	 * 
	 * @param camera
	 * @param context
	 * @param viewWidth
	 *            the surface width
	 * @param viewHeight
	 *            the surface height
	 */
	public static void setPreviewAccordingToScreenOrientation(Camera camera,
			Context context, int viewWidth, int viewHeight) {
		d("setPreviewAccordingToScreenOrientation");
		try {
			Parameters parameters = camera.getParameters();
			Display display = ((WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();
			int rotation = display.getRotation();
			Log.i(LOG_TAG, "setPreviewAccordingToScreenOrientation rotation="
					+ rotation);
			if (isTablet(rotation, viewWidth, viewHeight)) {
				Log.i(LOG_TAG, "device is tablet");
				setCamPreviewRotationForTablets(camera, viewWidth, viewHeight,
						parameters, rotation);
			} else {
				Log.i(LOG_TAG, "device is phone");
				setCamPreviewRotationForPhones(camera, viewWidth, viewHeight,
						parameters, rotation);
			}
			try {
				camera.setParameters(parameters);
			} catch (Exception e) {
				Log.e(LOG_TAG, e, "Could not set camera parameters:");
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setDisplayOrientation(Camera camera, int inDegree) {
		/*
		 * myCamera.setDisplayOrientation(inDegree);
		 * 
		 * does not work on older devices so use reflection
		 */
		try {
			camera.getClass().getMethod("setDisplayOrientation", int.class)
					.invoke(camera, inDegree);
		} catch (Exception e) {
			Log.w(LOG_TAG, "Could not rotate camera parameters:");
			e.printStackTrace();
		}
	}

	private static boolean isTablet(int rotation, int width, int height) {
		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
			if (width > height) {
				return true;
			}
		} else if (rotation == Surface.ROTATION_90
				|| rotation == Surface.ROTATION_270) {
			if (width < height) {
				return true;
			}
		}
		return false;
	}

	public interface RecorderControl {
		void stopRecording();
	}

	/**
	 * @param outputFile
	 *            the file to save the video to
	 * @param recordingQuality
	 *            e.g. {@link CamcorderProfile#QUALITY_480P}
	 * @param max_filesize_bytes
	 *            e.g. 50000000;// about 50 megabytes
	 * @param max_duration_ms
	 *            e.g. 60 * 60 * 1000; // 1 hour
	 * @param camera
	 *            the camera
	 * @param surface
	 *            a surface to render to
	 * @return
	 */
	public static RecorderControl startRecordingTo(File outputFile,
			int recordingQuality, int max_filesize_bytes, int max_duration_ms,
			Camera camera, Surface surface) {
		final MediaRecorder recorder = new MediaRecorder();
		camera.unlock();
		recorder.setCamera(camera);

		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

		recorder.setProfile(CamcorderProfile.get(recordingQuality));
		recorder.setOutputFile(outputFile.getAbsolutePath());

		recorder.setMaxFileSize(max_filesize_bytes);
		recorder.setMaxDuration(max_duration_ms);

		try {
			recorder.setPreviewDisplay(surface);
			recorder.prepare();
			recorder.start();
			return new RecorderControl() {
				@Override
				public void stopRecording() {
					recorder.stop();
					recorder.reset();
					recorder.release();
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void w(String warningText) {
		Log.w(LOG_TAG, warningText);
	}

	private static void d(String string) {
		Log.d(LOG_TAG, string);
	}

	private static void setCamPreviewRotationForTablets(Camera camera,
			int width, int height, Parameters parameters, int rotation) {
		if (rotation == Surface.ROTATION_270) {
			d("tablet rotation angle = 270");
			parameters.setPreviewSize(height, width);
			setDisplayOrientation(camera, 90);
		} else if (rotation == Surface.ROTATION_0) {
			d("tablet rotation angle = 180");
			parameters.setPreviewSize(width, height);
			setDisplayOrientation(camera, 0);
		} else if (rotation == Surface.ROTATION_90) {
			// up down sitched
			d("tablet rotation angle = 90");
			parameters.setPreviewSize(height, width);
			setDisplayOrientation(camera, 270);
		} else if (rotation == Surface.ROTATION_180) {
			d("tablet rotation angle = 180");
			parameters.setPreviewSize(width, height);
			setDisplayOrientation(camera, 180);
		}
	}

	public static boolean setCameraDisplayOrientation(int cameraId,
			android.hardware.Camera camera, Display display) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = display.getRotation();
		int degrees = 0;
		boolean landscapeMode = false;
		switch (rotation) {
		case Surface.ROTATION_0:
			// default portrait mode
			landscapeMode = false;
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			// default landscape mode
			landscapeMode = true;
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			landscapeMode = false;
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			landscapeMode = true;
			degrees = 270;
			break;
		}
		d("display orientation in degree = " + degrees + "(landscapeMode="
				+ landscapeMode + ")");
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
		return landscapeMode;
	}

	public static void logDetailsAbout(Camera c) {
		Parameters p = c.getParameters();
		switch (p.getPreviewFormat()) {
		case android.graphics.ImageFormat.YV12:
			Log.i(LOG_TAG, "preview format is YV12");
			break;
		case android.graphics.ImageFormat.RGB_565:
			Log.i(LOG_TAG, "preview format is RGB_565");
			break;
		case android.graphics.ImageFormat.NV21:
			Log.i(LOG_TAG, "preview format is NV21");
			break;
		case android.graphics.ImageFormat.NV16:
			Log.i(LOG_TAG, "preview format is NV16");
			break;
		case android.graphics.ImageFormat.JPEG:
			Log.i(LOG_TAG, "preview format is JPEG");
			break;
		case android.graphics.ImageFormat.YUV_420_888:
			Log.i(LOG_TAG, "preview format is YUV_420_888");
			break;
		case android.graphics.ImageFormat.YUY2:
			Log.i(LOG_TAG, "preview format is YUY2");
			break;
		case android.graphics.ImageFormat.UNKNOWN:
			Log.i(LOG_TAG, "preview format is UNKNOWN");
			break;
		}
		Size s = p.getPreviewSize();
		Log.i(LOG_TAG, "preview size: " + s.width + "X" + s.height);
	}

	public static Size getNativeFrameSizeOfAndroidCamera(Camera camera) {
		Size biggest = null;
		int max = 0;
		for (Size s : camera.getParameters().getSupportedPictureSizes()) {
			int currentSize = s.width * s.height;
			if (currentSize > max) {
				biggest = s;
				max = currentSize;
			}
		}
		return biggest;
	}

	private static final int DEFAULT_LARGE_PIC_WIDTH = 1280;
	private static final int DEFAULT_LARGE_PIC_HEIGHT = 720;

	@Deprecated
	public static void determineOptimalPictureSize(Camera mCamera,
			Display display, int w, int h) {

		Point psize = DeviceInformation.getScreenSize(display);
		int dispWidth = psize.x;
		int dispHeight = psize.y;

		w = w == 0 ? dispWidth : w;
		h = h == 0 ? dispHeight : h;

		if (w < DEFAULT_LARGE_PIC_WIDTH || h < DEFAULT_LARGE_PIC_HEIGHT) {
			w = DEFAULT_LARGE_PIC_WIDTH;
			h = DEFAULT_LARGE_PIC_HEIGHT;
		}

		if (mCamera != null) {
			Camera.Parameters params = mCamera.getParameters();

			List<Camera.Size> availableSizes = params
					.getSupportedPictureSizes();
			final double ASPECT_TOLERANCE = 0.05;
			double targetRatio = (double) w / h;
			if (availableSizes == null) {
				return;
			}

			Camera.Size optimalSize = null;
			double minDiff = Double.MAX_VALUE;

			int targetHeight = h;

			// Try to find an size match aspect ratio and size
			for (Camera.Size size : availableSizes) {
				double ratio = (double) size.width / size.height;
				if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
					continue;
				}
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}

			// Cannot find the one match the aspect ratio, ignore the
			// requirement
			if (optimalSize == null) {
				minDiff = Double.MAX_VALUE;
				for (Camera.Size size : availableSizes) {
					if (Math.abs(size.height - targetHeight) < minDiff) {
						optimalSize = size;
						minDiff = Math.abs(size.height - targetHeight);
					}
				}
			}

			if (optimalSize != null) {
				d("Picked Camera Resolution: w=" + optimalSize.width + ", h="
						+ optimalSize.height);
				params.setPictureSize(optimalSize.width, optimalSize.height);
				mCamera.setParameters(params);
			} else {
				w("Failed to pick Camera Resolution");
			}
		}
	}

	private static void setPreviewSize(Parameters parameters, int wantedWidth,
			int wantenHeight) {
		d("setPreviewSize wantedWidth=" + wantedWidth + ", wantenHeight="
				+ wantenHeight);

		Size calcedSize = calcPreviewFrameSize(parameters, wantedWidth);
		Log.i(LOG_TAG, "Camera preview will use resolution: calcedSize.width="
				+ calcedSize.width + " X calcedSize.height="
				+ calcedSize.height);
		parameters.setPreviewSize(calcedSize.width, calcedSize.height);
	}

	private static void setCamPreviewRotationForPhones(Camera camera,
			int viewWidth, int viewHeight, Parameters parameters, int rotation) {
		if (rotation == Surface.ROTATION_0) {
			d("rotation angle = 0");
			setPreviewSize(parameters, viewHeight, viewWidth);
			setDisplayOrientation(camera, 90);
		} else if (rotation == Surface.ROTATION_90) {
			d("rotation angle = 90");
			setPreviewSize(parameters, viewWidth, viewHeight);
			setDisplayOrientation(camera, 0);
		} else if (rotation == Surface.ROTATION_180) {
			d("rotation angle = 180");
			setPreviewSize(parameters, viewHeight, viewWidth);
			setDisplayOrientation(camera, 0);
		} else if (rotation == Surface.ROTATION_270) {
			d("rotation angle = 270");
			setPreviewSize(parameters, viewWidth, viewHeight);
			setDisplayOrientation(camera, 180);
		}
	}

	/**
	 * will calculate the closest available cam preview size which the camera of
	 * the device can provide based on the value which is wished via the second
	 * parameter of this method
	 * 
	 * @param parameters
	 * @param desiredCameraPreviewWidth
	 *            the next smaller or equal resolution to the passed one will be
	 *            used
	 * @return
	 */
	public static Size calcPreviewFrameSize(Parameters parameters,
			int desiredCameraPreviewWidth) {
		d("calcPreviewFrameSize with desiredCameraPreviewWidth="
				+ desiredCameraPreviewWidth);
		Size smallestOne = parameters.getPreviewSize();
		Size bestOne = null;
		for (Camera.Size s : parameters.getSupportedPreviewSizes()) {
			if (s.width <= desiredCameraPreviewWidth) {
				// get as close as possible to the wanted cam width:
				if (bestOne == null
						|| bestOne.width > desiredCameraPreviewWidth
						|| s.width > bestOne.width || s.height > bestOne.height) {
					bestOne = s;
				}
			}
			if (s.width < smallestOne.width || s.height < smallestOne.height) {
				smallestOne = s;
			}
		}
		if (bestOne == null) {
			bestOne = smallestOne;
		}
		d("Will use the cam preview size " + bestOne.width + "X"
				+ bestOne.height);
		return bestOne;
	}

	public static boolean switchParamsToRgb565(Parameters parameters) {
		List<Integer> pixelFormats = parameters.getSupportedPreviewFormats();
		for (Integer format : pixelFormats) {
			if (format == ImageFormat.RGB_565) {
				d("Camera supports RGB_565, will be used");
				parameters.setPreviewFormat(format);
				return true;
			}
		}
		return false;
	}

}
