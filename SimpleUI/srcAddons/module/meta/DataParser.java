package module.meta;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class can be used for parsing an set of data objects with its meta data
 * 
 */
public class DataParser {
	public static final String ALL_DB_DATA = "allDbData";

	JSON gsonParserAllData;
	JSON gsonParserAllDataWithoutInvisibleFields;

	public DataParser() {
		this.gsonParserAllData = new JSON();
		this.gsonParserAllDataWithoutInvisibleFields = JSON
				.newParserWithoutInvisibleFields();
		// .disableInnerClassSerialization().create();
	}

	/**
	 * This method is for parsing all data from objects excluding its transient
	 * fields defined by module.meta.MetaAttr
	 * 
	 * @param allData
	 * @param includeInvisibleFields
	 * @return
	 */
	public JSONObject parseDBSet(Iterator<?> allData,
			boolean includeInvisibleFields, boolean includeMeta) {
		JSONObject result = new JSONObject();
		Object lastObject = null;
		boolean hasData = false;
		// collect all data which is visible to front end
		while (allData.hasNext()) {
			hasData = true;
			try {
				lastObject = allData.next();
				String entry = "";
				if (!includeInvisibleFields) {
					entry = gsonParserAllDataWithoutInvisibleFields
							.toJson(lastObject);
				} else {
					entry = gsonParserAllData.toJson(lastObject);
				}
				result = MetaValidator.myAppend(result, ALL_DB_DATA,
						new JSONObject(entry));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// add meta data to the all data if needed
		if (includeMeta && hasData) {
			try {
				result.put(MetaParser.META_DATA,
						new MetaParser().getMeta(lastObject));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public JSONObject parseSingleData(Object singleData,
			boolean includeInvisibleFields, boolean includeMeta) {
		List<Object> temp = new LinkedList<Object>();
		temp.add(singleData);
		return parseDBSet(temp.iterator(), includeInvisibleFields, includeMeta);
	}

	public JSON getGsonForAllData() {
		return gsonParserAllData;
	}
}
