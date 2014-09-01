package module.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CrowDev-Meta-Attribute can annotate one field. There are following
 * attributes:<br>
 * <ul>
 * <li><b>regex </b>, default: <b>""</b><br>
 * Read about regex: <a
 * href="http://www.vogella.com/articles/JavaRegularExpressions/article.html"
 * >here</a></li>
 * <li><b>type</b>, default: <b>""</b>
 * <ul>
 * <li>CDMetaAttribute.TYPE_ID</li>
 * <li>CDMetaAttribute.TYPE_URL_IMAGE</li>
 * <li>CDMetaAttribute.TYPE_DATE_LONG</li>
 * <li>CDMetaAttribute.FIELDS_READ_ONLY</li>
 * <li>CDMetaAttribute.FIELDS_UNIQ_IN_SCOPE</li>
 * <li>CDMetaAttribute.FIELDS_TRANSIENT</li>
 * <li>CDMetaAttribute.FIELDS_PRIVATE</li>
 * <li>CDMetaAttribute.FIELDS_NOT_NULL</li>
 * </ul>
 * </li>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MetaAttr {

	// Type descriptors
	public static final int TYPE_ID = 1;
	public static final int TYPE_URL_IMAGE = 1 << 1;
	public static final int TYPE_DATE_LONG = 1 << 2;

	// Field descriptors
	public static final int FIELDS_READ_ONLY = 1 << 3;
	public static final int FIELDS_UNIQ_IN_SCOPE = 1 << 4;
	/**
	 * when used the field is not added to the json representation, so DB fields
	 * which should never be send from the backend to the frontend can make use
	 * of this
	 */
	public static final int FIELDS_TRANSIENT = 1 << 5;

	@Deprecated
	// TODO not used?
	public static final int FIELDS_PRIVATE = 1 << 6;

	/**
	 * this marks the field to never be null
	 */
	public static final int FIELDS_NOT_NULL = 1 << 7;

	String regex() default "";

	int type() default 0;
}
