package module.meta;

import java.lang.reflect.Type;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonElement;

/**
 * wraps the json implementation, currently gson and jackson are both used, in
 * the future only jackson will be used because only jackson supports the
 * {@link JSON#fromJsonInto(String, Object)} functionality
 * 
 */
public class JSON {

	/**
	 * This strategy excluded all fields which are marked with MetaAttr as
	 * transient
	 */
	private static class CDExclusionStrategy implements ExclusionStrategy {

		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			if (f.getAnnotation(MetaAttr.class) != null) {
				MetaAttr attr = f.getAnnotation(MetaAttr.class);
				if (attr.type() != 0) {
					if ((attr.type() & MetaAttr.FIELDS_TRANSIENT) != 0) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}
	}

	public static JSON newParserWithoutInvisibleFields() {
		return new JSON(new GsonBuilder().setExclusionStrategies(
				new CDExclusionStrategy()).create());
	}

	private final Gson gson;

	public JSON() {
		this(new Gson());
	}

	public JSON(Gson gson) {
		this.gson = gson;
	}

	public String toJson(Object o) {
		return gson.toJson(o);
	}

	/**
	 * writes the updates into a passed object
	 * 
	 * @param objAsJSON
	 * @param targetObj
	 *            this object will be changed according to the new values in the
	 *            json string
	 * @return true if the update procedure worked
	 */
	public boolean fromJsonInto(String objAsJSON, final Object targetObj) {
		Gson gson = new GsonBuilder().registerTypeAdapter(targetObj.getClass(),
				new InstanceCreator() {
					@Override
					public Object createInstance(Type t) {
						// return the same object so that it is "updated"
						return targetObj;
					}
				}).create();
		gson.fromJson(objAsJSON, targetObj.getClass());
		return true;
	}

	public <T> T fromJson(String objAsJSON, Class<T> objClass) {
		return gson.fromJson(objAsJSON, objClass);
	}

	public boolean equals(String a, String b) {
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		JsonElement oa = parser.parse(a);
		JsonElement ob = parser.parse(b);
		return oa.equals(ob);
	}

}
