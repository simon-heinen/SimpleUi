package module.meta;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This tool is used for update of objects
 */
public class MetaUpdater {

	public JSONObject updateObject(JSONObject singleOldData,
			JSONObject validatedEntry, JSONObject meta) throws JSONException {

		JSONObject oOldDataJSON = singleOldData.getJSONArray(
				DataParser.ALL_DB_DATA).getJSONObject(0);

		ArrayList<String> transientFields = MetaParser.getAsArrayList(meta,
				MetaParser.FIELDS_TRANSIENT);

		ArrayList<String> readOnlyFields = MetaParser.getAsArrayList(meta,
				MetaParser.FIELDS_READ_ONLY);

		ArrayList<String> complexFields = MetaParser.getAsArrayList(meta,
				MetaParser.FIELDS_COMPLEX);

		ArrayList<String> updatedFields = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = oOldDataJSON.keys();
		while (iterator.hasNext()) {
			String field = iterator.next();
			if (!(transientFields.contains(field)
					|| readOnlyFields.contains(field) || complexFields
						.contains(field))) {
				updatedFields.add(field);
			}
		}

		for (String updatedField : updatedFields) {
			oOldDataJSON.put(updatedField, validatedEntry.get(updatedField));
		}

		for (String complexField : complexFields) {
			if (!(updatedFields.contains(complexField) || readOnlyFields
					.contains(complexField))) {
				JSONObject complexOject = updateObject(
						oOldDataJSON.getJSONObject(complexField),
						validatedEntry.getJSONObject(complexField),
						meta.getJSONObject(complexField));
				oOldDataJSON.put(complexField, complexOject);
			}
		}

		return oOldDataJSON;
	}

}
