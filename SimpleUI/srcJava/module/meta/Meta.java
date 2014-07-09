package module.meta;

import org.json.JSONException;
import org.json.JSONObject;

import util.Log;

public class Meta {

	private static final String LOG_TAG = "Meta";

	public <T> T cloneViaGson(T orig) {

		return (T) j.fromJson(j.toJson(orig), orig.getClass());
	}

	JSON j = new JSON();
	private final MetaValidator val;

	public Meta() {
		this(new MetaListener() {

			@Override
			public void showWarningMsg(String msg) {
				Log.w(LOG_TAG, msg);
			}

			@Override
			public void showErrorMsg(String msg) {
				Log.e(LOG_TAG, msg);
			}
		});
	}

	public Meta(MetaListener listener) {
		val = new MetaValidator();
		val.setListener(listener);
	}

	public boolean validate(Object o) {
		return validateUpdateObject(o, cloneViaGson(o));
	}

	/**
	 * will test if the new version of the object would be a valid update for
	 * the target object. to acutally update the object use
	 * {@link Meta#updateObject(Object, Object)} instead
	 * 
	 * @param target
	 *            the object which would be updated
	 * @param newObj
	 * @return true if the update would work correctly
	 */
	public <T> boolean validateUpdateObject(T target, T newObj) {
		try {
			return val.validateUpdateObject(target, newObj);
		} catch (JSONException e) {
			Log.e(LOG_TAG, "A field of the object might not be set:");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * will update the target object (using the values from the updated version
	 * passed as the second parameter) if all conditions are met
	 * 
	 * @param target
	 * @param updatedVersion
	 * @return true if the update is allowed, false if a condition was not met
	 */
	public <T> boolean updateObject(T target, T updatedVersion) {
		try {
			JSONObject updatedObj = val.updateObject(target, updatedVersion);
			if (updatedObj == null) {
				return false;
			}
			String updatedObjString = updatedObj.toString();
			if (!j.equals(updatedObjString, j.toJson(updatedVersion))) {
				// Log.w(LOG_TAG, "updatedObj          =" + updatedObjString);
				// Log.w(LOG_TAG, "updatedVersionAsJson=" +
				// j.toJson(updatedVersion));
				return false;
			}
			return j.fromJsonInto(updatedObjString, target);
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Could not update object:");
			e.printStackTrace();
			return false;
		}

	}

	public void toString(String objName, Object obj) {
		String json = j.toJson(obj);
		Log.i(LOG_TAG, "obj " + objName + " as json: " + json);

	}
}
