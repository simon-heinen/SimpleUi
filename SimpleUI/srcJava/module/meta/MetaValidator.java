package module.meta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is needed to validate an object which should be created or updated
 * depends on its meta model
 * 
 */
public class MetaValidator {

	MetaListener listener;

	/**
	 * This method validates if the old object can be updated by old. If old
	 * object is null, then it validates if new object can be created
	 * 
	 * @param oOld
	 *            - old object or null
	 * @param oNew
	 *            - new object
	 * @return true if validation is successful
	 * @throws JSONException
	 */
	public boolean validateUpdateObject(Object oOld, Object oNew)
			throws JSONException {

		JSON gson = new JSON();
		JSONObject meta = new MetaParser().getMeta(oOld);
		JSONObject oOldJSON = null;
		if (oOld != null) {
			oOldJSON = new JSONObject(gson.toJson(oOld));
		}
		JSONObject oNewJSON = new JSONObject(gson.toJson(oNew));

		JSONObject test = scanJSONObjects(meta, oOldJSON, oNewJSON, false);
		if (test == null) {
			return false;
		}

		return true;
	}

	/**
	 * This method not just validate but also returns an object which can be
	 * used for update if validation was correct
	 * 
	 * @param oOld
	 * @param oNew
	 * @return
	 * @throws JSONException
	 */
	public JSONObject updateObject(Object oOld, Object oNew)
			throws JSONException {
		JSON json = new JSON();
		JSONObject meta = new MetaParser().getMeta(oOld);
		JSONObject oOldJSON = null;
		if (oOld != null) {
			oOldJSON = new JSONObject(json.toJson(oOld));
		}
		JSONObject oNewJSON = new JSONObject(json.toJson(oNew));

		return scanJSONObjects(meta, oOldJSON, oNewJSON, true);

	}

	/**
	 * Validates if old object can be updated by new one. Thereby old object
	 * must have an meta
	 * 
	 * @param oOld
	 * @param oNew
	 * @param tryUpdate
	 * @return
	 * @throws JSONException
	 */
	public boolean validateUpdateObject(String oOld, String oNew,
			boolean tryUpdate) throws JSONException {
		JSONObject oOldJSON = new JSONObject(oOld);
		JSONObject meta = oOldJSON.getJSONObject(MetaParser.META_DATA);
		JSONObject oOldDataJSON = oOldJSON.getJSONArray(DataParser.ALL_DB_DATA)
				.getJSONObject(0);
		JSONObject oNewJSON = new JSONObject(oNew);
		JSONObject test = scanJSONObjects(meta, oOldDataJSON, oNewJSON,
				tryUpdate);

		if (test == null) {
			return false;
		}

		return true;
	}

	/**
	 * This method not just validate but also returns an object which can be
	 * used for update if validation was correct
	 * 
	 * @param oOld
	 * @param oNew
	 * @param tryUpdate
	 * @return
	 * @throws JSONException
	 */
	public JSONObject updateObject(String oOld, String oNew, boolean tryUpdate)
			throws JSONException {
		JSONObject oOldJSON = new JSONObject(oOld);
		JSONObject meta = oOldJSON.getJSONObject(MetaParser.META_DATA);
		JSONObject oOldDataJSON = oOldJSON.getJSONArray(DataParser.ALL_DB_DATA)
				.getJSONObject(0);
		JSONObject oNewJSON = new JSONObject(oNew);
		JSONObject test = scanJSONObjects(meta, oOldDataJSON, oNewJSON,
				tryUpdate);

		return test;
	}

	/**
	 * This function tests two objects depends on their metaModel, if old object
	 * can be updated by new object. This is done without checking metaModel
	 * itself! Use for this first checkMetaModel if needed
	 * 
	 * @param metaAsJSON
	 * @param oOldJSON
	 * @param oNewJSON
	 * @return New Object that is cleaned up from from all unwanted fields or
	 *         null if update is not possible
	 * @throws JSONException
	 */
	private JSONObject scanJSONObjects(JSONObject meta, JSONObject oOldJSON,
			JSONObject oNewJSON, boolean doUpdate) throws JSONException {

		ArrayList<String> allFields = MetaParser.getAsArrayList(meta,
				MetaParser.FIELDS_ALL);

		ArrayList<String> transientFields = MetaParser.getAsArrayList(meta,
				MetaParser.FIELDS_TRANSIENT);

		@SuppressWarnings("unchecked")
		Iterator<String> iterator = oNewJSON.keys();

		// collect that what should be deleted here, because of runtime
		ArrayList<String> toBeDeleted = new ArrayList<String>();

		// check all fields and invisible fields
		while (iterator.hasNext()) {
			String field = iterator.next();
			if (!allFields.contains(field)) {
				if (doUpdate) {
					showWarningMsg(new MetaMsg()
							.warning()
							.causedByRule(
									"Was not in allFields! Removed from new object!")
							.causedByField(field)
							.msgDev("allFields: " + allFields).create());
					toBeDeleted.add(field);
				} else {
					showErrorMsg(new MetaMsg().error()
							.causedByRule("Not exist in allFields!")
							.causedByField(field)
							.msgDev("allFields: " + allFields).create());
					return null;
				}
			}
			if (transientFields.contains(field)) {
				if (doUpdate) {
					showWarningMsg(new MetaMsg()
							.warning()
							.causedByRule(
									"Should be invisible! Will be overwritten by old object!")
							.causedByField(field)
							.msgDev("from ." + oNewJSON.get(field) + ". to ."
									+ oOldJSON.get(field)
									+ ". transientfields: " + transientFields)
							.create());
					toBeDeleted.add(field);
					// oNewJSON.accumulate(field, oOldJSON.get(field));
				} else {
					showErrorMsg(new MetaMsg().error()
							.causedByRule("Should be invisible!")
							.causedByField(field)
							.msgDev("transientfields: " + transientFields)
							.create());
					return null;
				}
			}
		}

		// after all fields are checked fields tobedeleted can be deleted
		if (doUpdate) {
			for (String string : toBeDeleted) {
				oNewJSON.remove(string);
			}
		}

		// check read only fields
		ArrayList<String> readOnly = MetaParser.getAsArrayList(meta,
				MetaParser.FIELDS_READ_ONLY);
		if (oOldJSON != null) {
			for (String readOnlyField : readOnly) {
				if (!oOldJSON.isNull(readOnlyField)) {
					if (!oNewJSON.isNull(readOnlyField)) {
						String str1 = oOldJSON.get(readOnlyField).toString();
						String str2 = oNewJSON.get(readOnlyField).toString();
						// this need to be done because it becomes data without
						// its data types, e.g. true as String "true"
						str1 = str1.replace("\"", "").replace("[", "")
								.replace("]", "").toLowerCase();
						str2 = str2.replace("\"", "").replace("[", "")
								.replace("]", "").toLowerCase();
						if (!str1.equals(str2)) {
							if (doUpdate) {
								showWarningMsg(new MetaMsg()
										.warning()
										.causedByRule(
												"Read only field was modified! Will be overwritten by old object!")
										.causedByField(readOnlyField)
										.msgDev("from ."
												+ oOldJSON.get(readOnlyField)
												+ ". to ."
												+ oNewJSON.get(readOnlyField)
												+ ".").create());
								oNewJSON.remove(readOnlyField);
								oNewJSON.put(readOnlyField,
										oOldJSON.get(readOnlyField));

							} else {
								showErrorMsg(new MetaMsg()
										.error()
										.causedByRule(
												"Read only field was modified!")
										.causedByField(readOnlyField)
										.msgDev("from ."
												+ oOldJSON.get(readOnlyField)
												+ ". to ."
												+ oNewJSON.get(readOnlyField)
												+ ".").create());

								return null;
							}
						}
					} else {
						if (doUpdate) {
							showWarningMsg(new MetaMsg()
									.warning()
									.causedByRule(
											"Read only field was modified! Will be overwritten by old object!")
									.causedByField(readOnlyField)
									.msgDev("from ."
											+ oOldJSON.get(readOnlyField)
											+ ". to .null.").create());
							oNewJSON.accumulate(readOnlyField,
									oOldJSON.get(readOnlyField));

						} else {
							showErrorMsg(new MetaMsg()
									.error()
									.causedByRule(
											"Read only field was modified!")
									.causedByField(readOnlyField)
									.msgDev("from ."
											+ oOldJSON.get(readOnlyField)
											+ ". to .null.").create());
							return null;
						}
					}
				} else {
					if (!oNewJSON.isNull(readOnlyField)) {
						if (doUpdate) {
							showErrorMsg(new MetaMsg()
									.error()
									.causedByRule(
											"Read only field was modified! Will be overwritten by value of old object!")
									.causedByField(readOnlyField)
									.msgDev("from .null. to ."
											+ oNewJSON.get(readOnlyField) + ".")
									.create());
							oNewJSON.remove(readOnlyField);
						} else {
							showErrorMsg(new MetaMsg()
									.error()
									.causedByRule(
											"Read only field was modified!")
									.causedByField(readOnlyField)
									.msgDev("from .null. to ."
											+ oNewJSON.get(readOnlyField) + ".")
									.create());
							return null;
						}
					}
				}
			}
		}

		// check id fields if exist and if no new object
		if (oOldJSON != null) {
			ArrayList<String> typeId = MetaParser.getAsArrayList(meta,
					MetaParser.TYPE_ID);
			if (typeId.size() != 0) {
				String idFieldName = typeId.get(0);
				if (typeId.size() != 1) {
					if (oNewJSON.isNull(idFieldName)) {
						showErrorMsg(new MetaMsg().error()
								.causedByRule("More than one id field!")
								.causedByField(typeId.toArray().toString())
								.create());
						return null;
					}
				}

				String str1 = "" + oOldJSON.get(idFieldName);
				String str2 = "" + oNewJSON.get(idFieldName);
				if (!str1.equals(str2)) {
					showErrorMsg(new MetaMsg()
							.error()
							.causedByRule(
									"ID field was different from old object")
							.causedByField(idFieldName)
							.msgDev("was ." + str1 + ". now ." + str2 + ".")
							.create());
					return null;
				}
			}
		}

		// check regex fields
		if (!meta.isNull(MetaParser.FIELDS_REGEX)) {
			Object obj = meta.get(MetaParser.FIELDS_REGEX);
			if (obj instanceof JSONObject) {
				if (!checkRegex(oNewJSON, (JSONObject) obj)) {
					return null;
				}
			} else if (obj instanceof JSONArray) {
				for (int i = 0; i < ((JSONArray) obj).length(); i++) {
					JSONObject oo = ((JSONArray) obj).getJSONObject(i);
					if (!checkRegex(oNewJSON, oo)) {
						return null;
					}
				}
			}
		}

		// check complex fields recursively (if they are no read only fields)
		if (!meta.isNull(MetaParser.FIELDS_COMPLEX)) {
			Object obj = meta.get(MetaParser.FIELDS_COMPLEX);
			if (obj instanceof JSONArray) {
				for (int i = 0; i < ((JSONArray) obj).length(); i++) {
					JSONObject field = ((JSONArray) obj).getJSONObject(i);
					Iterator<String> it = field.keys();
					String key = it.next();
					if (!readOnly.contains(key)) {
						oNewJSON = checkComplex(oOldJSON, oNewJSON, field,
								doUpdate);
					}
					if (oNewJSON == null) {
						return null;
					}
				}
			} else {
				showErrorMsg(new MetaMsg().error()
						.causedByRule("Should never happen! Meta is corrupt!")
						.causedByField(obj.toString())
						.msgDev("meta: " + meta.toString()).create());
				return null;
			}
		}

		return oNewJSON;

	}

	/**
	 * Additional method for validation of complex fields
	 * 
	 * @param oOldJSON
	 * @param oNewJSON
	 * @param field
	 * @return
	 * @throws JSONException
	 */
	private JSONObject checkComplex(JSONObject oOldJSON, JSONObject oNewJSON,
			JSONObject field, boolean tryFixProblems) throws JSONException {
		@SuppressWarnings("unchecked")
		Iterator<String> it = field.keys();
		while (it.hasNext()) {
			String key = it.next();
			if (!oNewJSON.isNull(key)) {
				JSONObject newMeta = new JSONObject();
				if (!field.getJSONObject(key).isNull(
						MetaParser.COMPLEX_MULTIPLE_DATA)) {
					newMeta = field.getJSONObject(key).getJSONObject(
							MetaParser.COMPLEX_MULTIPLE_DATA);
					// this is a collection of objects
					JSONArray arrayOld = null;
					if (oOldJSON != null) {
						arrayOld = oOldJSON.getJSONArray(key);
					}
					JSONArray arrayNew = oNewJSON.getJSONArray(key);
					oNewJSON.remove(key);
					for (int i = 0; i < arrayNew.length(); i++) {
						JSONObject updatedJSON = null;
						if (arrayOld != null) {
							updatedJSON = scanJSONObjects(newMeta,
									arrayOld.getJSONObject(i),
									arrayNew.getJSONObject(i), tryFixProblems);
						} else {
							updatedJSON = scanJSONObjects(newMeta, null,
									arrayNew.getJSONObject(i), tryFixProblems);

						}
						if (updatedJSON != null) {
							oNewJSON = myAppend(oNewJSON, key, updatedJSON);
						} else {
							return null;
						}
					}
				} else if (!field.getJSONObject(key).isNull(
						MetaParser.COMPLEX_SINGLE_DATA)) {
					newMeta = field.getJSONObject(key).getJSONObject(
							MetaParser.COMPLEX_SINGLE_DATA);
					// this is only one object
					JSONObject oldObject = null;
					if (oOldJSON != null) {
						oldObject = oOldJSON.getJSONObject(key);
					}
					JSONObject newObject = oNewJSON.getJSONObject(key);

					oNewJSON.remove(key);
					JSONObject updatedJSON = null;

					if (oldObject != null) {
						updatedJSON = scanJSONObjects(newMeta, oldObject,
								newObject, tryFixProblems);
					} else {
						updatedJSON = scanJSONObjects(newMeta, null, newObject,
								tryFixProblems);
					}
					if (updatedJSON != null) {
						oNewJSON.accumulate(key, updatedJSON);
					} else {
						return null;
					}
				}

			}
		}
		return oNewJSON;
	}

	/**
	 * Additional method for validation of regex fields
	 * 
	 * @param oNewJSON
	 * @param oo
	 * @return
	 * @throws JSONException
	 */
	private boolean checkRegex(JSONObject oNewJSON, JSONObject oo)
			throws JSONException {
		@SuppressWarnings("unchecked")
		Iterator<String> keys = oo.keys();
		while (keys.hasNext()) {
			String field = keys.next();
			if (!oNewJSON.isNull(field)) {
				String value = oNewJSON.getString(field);
				String regex = oo.getString(field);
				if (!checkRegex(value, regex)) {
					showErrorMsg(new MetaMsg()
							.error()
							.causedByRule("Does not match its regex")
							.causedByField(field)
							.msgDev("Value ." + value + ". does not match ."
									+ regex + ". pattern").create());
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * This method checks a value by matching with given regex. The grammar of
	 * regex can be read <a href=
	 * "http://www.vogella.com/articles/JavaRegularExpressions/article.html"
	 * >here</a>
	 * 
	 * @param value
	 * @param regex
	 * @return
	 */
	private boolean checkRegex(String value, String regex) {
		if (regex.charAt(0) == '?') {
			// TODO: define own regex rules
			return true;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	public void setListener(MetaListener listener) {
		this.listener = listener;
	}

	private void showErrorMsg(String msg) {
		if (listener != null) {
			listener.showErrorMsg(msg);
		}
	}

	private void showWarningMsg(String msg) {
		if (listener != null) {
			listener.showWarningMsg(msg);
		}
	}

	/**
	 * This method is used to append one object like function append to an
	 * array, which is not supported in android
	 * 
	 * @param root
	 * @param field
	 * @param toBeAppend
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject myAppend(JSONObject root, String field,
			Object toBeAppend) throws JSONException {
		if (root.isNull(field)) {
			root.put(field, new JSONArray().put(toBeAppend));
		} else {
			root.accumulate(field, toBeAppend);
		}
		return root;
	}

}
