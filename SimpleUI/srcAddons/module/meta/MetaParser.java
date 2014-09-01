package module.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MetaParser {

	// Main meta descriptor
	public static final String META_DATA = "metaData";
	public static final String META_TABLE = "metaTable";

	// Type descriptors
	public static final String TYPE_ID = "typeId";
	public static final String TYPE_URL_IMAGE = "typeUrlImage";
	public static final String TYPE_DATE_LONG = "typeDateLong";

	// Field descriptors
	public static final String FIELDS_ALL = "fieldsAll";
	public static final String FIELDS_READ_ONLY = "fieldsReadOnly";
	public static final String FIELDS_UNIQ_IN_SCOPE = "fieldsUniqInScope";
	public static final String FIELDS_REGEX = "fieldsRegex";
	public static final String FIELDS_TRANSIENT = "fieldsTransient";
	public static final String FIELDS_COMPLEX = "fieldsComplex";
	public static final String FIELDS_PRIVATE = "fieldsPrivate";
	public static final String FIELDS_NOT_NULL = "fieldsNotNull";
	public static final String COMPLEX_SINGLE_DATA = "singleData";
	public static final String COMPLEX_MULTIPLE_DATA = "multipleData";

	// Class name
	public static final String CLASS_NAME = "className";

	/**
	 * Determines the meta model of given object including all inner classes OR
	 * determines the meta of given class if object is null
	 * 
	 * @param object
	 *            to get meta, if clazz set, can be null
	 * @param clazz
	 *            if object set can be null
	 * @return meta model as JSONObject
	 */
	public JSONObject getMeta(Object object, Class<? extends Object> clazz) {
		if (object != null) {
			clazz = object.getClass();
		}

		JSONObject result = new JSONObject();
		List<Field> allFields = new LinkedList<Field>();
		Set<ComplexClass> complexeCalsses = new HashSet<ComplexClass>();

		// store object class name
		try {
			result.accumulate(CLASS_NAME, clazz.getSimpleName());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		// get all parent fields
		do {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				allFields.add(field);
			}
			clazz = clazz.getSuperclass();
		} while (clazz != Object.class);

		// iterate over all fields
		for (Field field : allFields) {
			if (!Modifier.isFinal(field.getModifiers())) {
				try {

					// save all non primitive types to call it recursive later
					if (!isPrimitive(field)) {
						if (!isCollection(field.getType())) {
							ComplexClass cc;

							field.setAccessible(true);

							if (object != null && field.get(object) != null) {
								// takes the sub class of complex object
								cc = new ComplexClass(field.get(object), null,
										field.getName(), false);
							} else {
								// takes just class
								cc = new ComplexClass(null, field.getType(),
										field.getName(), false);
							}
							if (!cc.isInterface()) {
								complexeCalsses.add(cc);
							}
						} else {
							ComplexClass cc;

							field.setAccessible(true);

							if (object != null
									&& field.get(object) != null
									&& ((Collection<?>) field.get(object))
											.iterator().hasNext()) {
								Object o = ((Collection<?>) field.get(object))
										.iterator().next();
								if (o != null) {
									cc = new ComplexClass(o, null,
											field.getName(), true);
								} else {
									cc = new ComplexClass(
											null,
											((Class<?>) ((ParameterizedType) field
													.getGenericType())
													.getActualTypeArguments()[0]),
											field.getName(), true);
								}

							} else {
								// takes just class
								cc = new ComplexClass(null,
										((Class<?>) ((ParameterizedType) field
												.getGenericType())
												.getActualTypeArguments()[0]),
										field.getName(), true);
							}
							if (!cc.isInterface()) {
								complexeCalsses.add(cc);
							}
						}
					}

					// save all fields
					result.accumulate(FIELDS_ALL, field.getName());

					// check annotation specific fields
					if (field.isAnnotationPresent(MetaAttr.class)) {
						MetaAttr attr = field.getAnnotation(MetaAttr.class);
						if (!attr.regex().equals("")) {
							result.accumulate(FIELDS_REGEX, new JSONObject()
									.accumulate(field.getName(), attr.regex()));
						}
						if (attr.type() != 0) {
							result = addAllTypes(result, attr.type(),
									field.getName());
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					return null;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					return null;
				}
			}
		}

		// recursive call for complexe classes
		if (complexeCalsses.size() != 0) {
			for (ComplexClass cClass : complexeCalsses) {
				try {
					result = myAppend(
							result,
							FIELDS_COMPLEX,
							new JSONObject()
									.accumulate(
											cClass.fieldName,
											new JSONObject()
													.accumulate(
															cClass.collection ? COMPLEX_MULTIPLE_DATA
																	: COMPLEX_SINGLE_DATA,
															getMeta(cClass.object,
																	cClass.clazz))));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	public JSONObject getMeta(Object object) {
		return getMeta(object, null);
	}

	private static boolean isCollection(Class<?> clazzTest) {
		while (clazzTest != null && clazzTest != Object.class) {
			Class<?>[] allInterfaces = clazzTest.getInterfaces();
			for (Class<?> interfaze : allInterfaces) {
				if (interfaze == Collection.class) {
					return true;
				}
			}
			clazzTest = clazzTest.getSuperclass();
		}
		return false;
	}

	public static boolean isPrimitive(Field field) {
		if (isCollection(field.getType())) {
			return isPrimitive(((Class<?>) ((ParameterizedType) field
					.getGenericType()).getActualTypeArguments()[0]));
		}
		return isPrimitive(field.getType());
	}

	/**
	 * check if given class is primitive or is array of primitive classes
	 * 
	 * @param clazz
	 *            - to check
	 * @return true if it primitive
	 */
	public static boolean isPrimitive(Class<?> clazz) {

		if (clazz.isArray()) {
			return isPrimitive(clazz.getComponentType());
		}

		Set<Class<?>> allPrimitives = new HashSet<Class<?>>();
		allPrimitives.add(Boolean.class);
		allPrimitives.add(Character.class);
		allPrimitives.add(Byte.class);
		allPrimitives.add(Short.class);
		allPrimitives.add(Integer.class);
		allPrimitives.add(Long.class);
		allPrimitives.add(Float.class);
		allPrimitives.add(Double.class);
		allPrimitives.add(Void.class);
		allPrimitives.add(String.class);
		allPrimitives.add(int.class);
		allPrimitives.add(boolean.class);
		allPrimitives.add(byte.class);
		allPrimitives.add(char.class);
		allPrimitives.add(short.class);
		allPrimitives.add(long.class);
		allPrimitives.add(float.class);
		allPrimitives.add(double.class);
		allPrimitives.add(void.class);
		allPrimitives.add(Object.class);
		allPrimitives.add(Date.class);

		return allPrimitives.contains(clazz);
	}

	private class ComplexClass {
		Object object;
		Class<?> clazz;
		String fieldName;
		boolean collection;

		public ComplexClass(Object object, Class<?> clazz, String fieldName,
				boolean collection) {
			this.object = object;
			this.clazz = clazz;
			this.fieldName = fieldName;
			this.collection = collection;
		}

		public boolean isInterface() {
			boolean result = false;
			if (object != null) {
				result = result || object.getClass().isInterface();
			}
			if (clazz != null) {
				result = result || clazz.isInterface();
			}
			return result;
		}
	}

	/**
	 * Sets all types defined in CDMetaAttribute. Important!: this method should
	 * be updated if there are new types added in ADMetaAttribute
	 * 
	 * @param jsonObject
	 *            in which new types should be added
	 * @param types
	 *            - all set types
	 * @param fieldName
	 *            - which should be tested for a type
	 * @return updated JSONObject
	 */
	private JSONObject addAllTypes(JSONObject jsonObject, int types,
			String fieldName) {

		int[] allAttrs = { MetaAttr.FIELDS_READ_ONLY,
				MetaAttr.FIELDS_UNIQ_IN_SCOPE, MetaAttr.FIELDS_TRANSIENT,
				MetaAttr.TYPE_DATE_LONG, MetaAttr.TYPE_ID,
				MetaAttr.TYPE_URL_IMAGE, MetaAttr.FIELDS_PRIVATE };

		for (int attr : allAttrs) {
			if ((types & attr) != 0) {
				try {
					jsonObject = myAppend(jsonObject, translateType(attr),
							fieldName);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return jsonObject;
	}

	/**
	 * Translates an integer attribute to its string representation. Important!:
	 * this method should be updated if there are new types added in
	 * ADMetaAttribute
	 * 
	 * @param type
	 *            - defined in CDMetaAttribute
	 * @return its string representation, defined in CDMetaParser
	 */
	public String translateType(int type) {
		switch (type) {
		case MetaAttr.FIELDS_READ_ONLY:
			return FIELDS_READ_ONLY;
		case MetaAttr.FIELDS_UNIQ_IN_SCOPE:
			return FIELDS_UNIQ_IN_SCOPE;
		case MetaAttr.FIELDS_TRANSIENT:
			return FIELDS_TRANSIENT;
		case MetaAttr.TYPE_DATE_LONG:
			return TYPE_DATE_LONG;
		case MetaAttr.TYPE_ID:
			return TYPE_ID;
		case MetaAttr.TYPE_URL_IMAGE:
			return TYPE_URL_IMAGE;
		case MetaAttr.FIELDS_PRIVATE:
			return FIELDS_PRIVATE;
		case MetaAttr.FIELDS_NOT_NULL:
			return FIELDS_NOT_NULL;
		default:
			return "unknownType";
		}
	}

	/**
	 * Gives a list of fields of an object, which have a certain parameter
	 * defined by <b>MetaParser</b>
	 * 
	 * @param object
	 *            - JSONObject which should be proved to find fields
	 * @param parameter
	 *            - defined by CDGsonParser
	 * @return a list of fields defined by parameter
	 */
	public static List<String> getAllFieldsByParameter(JSONObject object,
			String parameter) {
		List<String> result = new LinkedList<String>();
		if (object != null && !object.isNull(parameter)) {
			JSONArray specificFieldJSON;
			try {
				if (object.get(parameter) instanceof JSONArray) {
					specificFieldJSON = (JSONArray) object.get(parameter);
					for (int i = 0; i < specificFieldJSON.length(); i++) {
						result.add(specificFieldJSON.get(i).toString());
					}
				} else {
					result.add(object.get(parameter).toString());
				}
			} catch (JSONException e) {

			}
		}
		return result;
	}

	public static List<String> getAllComplexFields(JSONObject meta) {
		List<String> result = new LinkedList<String>();
		if (!meta.isNull(FIELDS_COMPLEX)) {
			try {
				JSONArray allComplexfields = meta.getJSONArray(FIELDS_COMPLEX);
				for (int i = 0; i < allComplexfields.length(); i++) {
					JSONObject complexField = allComplexfields.getJSONObject(i);
					@SuppressWarnings("unchecked")
					Iterator<String> iterator = complexField.keys();
					while (iterator.hasNext()) {
						result.add(iterator.next());
					}
				}
			} catch (JSONException e) {
			}
		}

		return result;
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

	public static JSONObject getMetaFromComplexField(JSONObject rootMeta,
			String complexField) {
		try {
			if (!rootMeta.isNull(FIELDS_COMPLEX)) {
				JSONArray complexArray = rootMeta.getJSONArray(FIELDS_COMPLEX);
				for (int i = 0; i < complexArray.length(); i++) {
					JSONObject subComplex = complexArray.getJSONObject(i);
					if (!subComplex.isNull(complexField)) {
						if (!subComplex.getJSONObject(complexField).isNull(
								COMPLEX_SINGLE_DATA)) {
							return subComplex.getJSONObject(complexField)
									.getJSONObject(COMPLEX_SINGLE_DATA);
						} else if (!subComplex.getJSONObject(complexField)
								.isNull(COMPLEX_MULTIPLE_DATA)) {
							return subComplex.getJSONObject(complexField)
									.getJSONObject(COMPLEX_MULTIPLE_DATA);
						}
					}
				}
			}
		} catch (JSONException e) {

		}
		return null;
	}

	/**
	 * This method does from an key:value or key:[values] a ArrayList with
	 * values
	 * 
	 * @param jsonObject
	 * @param fieldType
	 * @return
	 * @throws JSONException
	 */
	public static ArrayList<String> getAsArrayList(JSONObject jsonObject,
			String fieldType) throws JSONException {
		ArrayList<String> result = new ArrayList<String>();
		if (!jsonObject.isNull(fieldType)) {
			Object object = jsonObject.get(fieldType);
			if (object instanceof JSONObject) {
				result.add(object.toString());
			} else if (object instanceof JSONArray) {
				JSONArray array = (JSONArray) object;
				for (int i = 0; i < array.length(); i++) {
					result.add(array.getString(i));
				}
			} else if (object instanceof String) {
				result.add(object.toString());
			}
		}
		return result;
	}
}
