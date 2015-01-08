package simpleui.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import simpleui.util.Log;

/**
 * use {@link ProxyFactory#create(Class, String, ProxyMethodCallHandler)}
 * 
 * This is a client proxy to use with Jax-RS REST interfaces. It will use the
 * annotations to automatically create a communication channel. This allows you
 * to reuse the server side REST interface definition and reuse it on your Java
 * client.
 * 
 * This is a custom implementation similar to the Jersey implementation which
 * does not work on Android (jet):
 * 
 * http://blog.alutam.com/2012/05/04/proxy-client-on-top-of-jax-rs-2-0-client-
 * api/
 * 
 * http://code.google.com/p/utils-apl-derived/wiki/RestClientFactoryJersey
 * http:/ /code.google.com/p/utils-apl-derived/source/search?q=
 * RestClientFactoryJersey &origq=RestClientFactoryJersey&btnG=Search+Trunk
 * 
 * infos about regex expressions:
 * http://www.vogella.com/articles/JavaRegularExpressions/article.html
 * 
 * 
 * TODO parameter regex evaluation also for the other parameters if a regex was
 * provided
 * 
 * 
 * @author Simon Heinen
 * 
 */
public class ProxyFactory {

	public static abstract class ProxyMethodCallHandler implements
			InvocationHandler {

		private static final String LOG_TAG = "ProxyMethodCallHandler";
		public static boolean EXTENDED_LOGGING = false;
		private String baseUrl;
		private Class<?> proxiedClass;

		public void setProxiedClass(Class<?> c) {
			this.proxiedClass = c;
		}

		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}

		public String getBaseUrl(Method method) {
			String pathParamOfClass = getPathParamOfClass(method
					.getDeclaringClass());
			return baseUrl + pathParamOfClass;
		}

		private String getPathParamOfClass(Class<?> classOfMethod) {
			String pathParamOfClass = "";
			if (classOfMethod.getAnnotation(Path.class) == null) {
				Log.d(LOG_TAG, "The path annotation at the class "
						+ classOfMethod + " was missing, ");
				return getPathParamOfClass(proxiedClass);
			} else {
				pathParamOfClass = classOfMethod.getAnnotation(Path.class)
						.value();
			}
			return pathParamOfClass;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] params)
				throws Throwable {

			/*
			 * checken ob der proxy die konkrete klasse ist und das bei der base
			 * url berechnung mit �bergeben
			 */

			initHandlerFields();

			// is it get, put, post or delete?:
			String httpMethodType = getRestMethodType(method).value();

			baseUrl = analyseBaseUrlForPossibleErrors(baseUrl);

			/*
			 * First extract the parameters
			 */
			String methodPath = method.getAnnotation(Path.class).value();

			methodPath = replaceReservedChars(methodPath);
			for (int i = 0; i < params.length; i++) {
				if (params[i] == null) {
					if (EXTENDED_LOGGING) {
						Log.w(LOG_TAG, "" + method + " - Parameter " + i
								+ " was null!");
					}
				}

				String valueToSet = getStringForParamValue(params[i]);
				Annotation[] aList = method.getParameterAnnotations()[i];

				if (aList != null && aList.length != 0) {

					DefaultValue defAnn = getAnnotation(DefaultValue.class,
							aList);

					PathParam pathParam = getAnnotation(PathParam.class, aList);
					if (pathParam != null) {
						methodPath = insertInPath(methodPath, pathParam,
								defAnn, valueToSet, true);
					} else {
						QueryParam queryAnn = getAnnotation(QueryParam.class,
								aList);
						if (queryAnn != null) {
							if (valueToSet != null) {
								addQuerryParam(queryAnn, defAnn, valueToSet);
							} else {
								if (EXTENDED_LOGGING) {
									Log.i(LOG_TAG,
											queryAnn.value()
													+ " was null, "
													+ "will not be added to query params");
								}
							}
						} else {
							FormParam formAnn = getAnnotation(FormParam.class,
									aList);
							if (formAnn != null) {
								addFormParam(formAnn, defAnn, valueToSet);
							} else {
								CookieParam cookie = getAnnotation(
										CookieParam.class, aList);
								if (cookie != null) {
									addCookieParam(cookie, defAnn, valueToSet,
											getHostForUrl(getBaseUrl(method)));
								} else {
									HeaderParam header = getAnnotation(
											HeaderParam.class, aList);
									if (header != null) {
										addHeaderParam(header, defAnn,
												valueToSet);
									}
								}
							}
						}
					}
				} else {
					/*
					 * this part is called if there was no annotation for the
					 * variable
					 */
					if (HttpMethod.PUT.equals(httpMethodType)
							|| HttpMethod.POST.equals(httpMethodType)) {

						/*
						 * if it is a put or a post request the not annotated
						 * variable normally should be the request body
						 * 
						 * if its not a PUT or POST request the rest interface
						 * is not specified correctly! only these to type can
						 * have a entity body
						 */
						setPostOrPutRequestBody(valueToSet);
					} else {
						warningFromJaxRsParsingAnalysis("The parameter "
								+ params[i] + " had no annotation and it "
								+ "was not a PUT or POST request with "
								+ "an entity body. REST interface incorrect");
					}
				}
			}

			/*
			 * then the other relevant attributes:
			 */
			Class<?> responseType = method.getReturnType();
			String contentType = getContentType(proxy, method);
			String[] typesThatCanBeProducedByServer = getAcceptedMediaTypes(
					proxy, method);

			if (!"".equals(methodPath)) {
				/*
				 * create complete url
				 */
				if (!(methodPath.charAt(0) == '/')
						&& !getBaseUrl(method).endsWith("/")) {
					methodPath = "/" + methodPath;
				} else if ((methodPath.charAt(0) == '/')
						&& getBaseUrl(method).endsWith("/")) {
					methodPath = methodPath.substring(1);
				}
			}
			String completeUrl = this.getBaseUrl(method) + methodPath;

			return checkForCorrectType(
					doRequest(httpMethodType, completeUrl,
							typesThatCanBeProducedByServer, contentType,
							responseType), responseType);
		}

		private String analyseBaseUrlForPossibleErrors(String baseUrl) {
			try {
				URL url = new URL(baseUrl);

				if (EXTENDED_LOGGING) {
					Log.d(LOG_TAG, "analysing url=" + url);
					Log.d(LOG_TAG, "  > host=" + url.getHost());
					Log.d(LOG_TAG, "  > protocol=" + url.getProtocol());
				}
				if (baseUrl.endsWith("/")) {
					return baseUrl.substring(0, baseUrl.length() - 1);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, e, "baseUrl=" + baseUrl
						+ " seems to be incorrect!");
			}
			return baseUrl;
		}

		/*
		 * This method will make sure that the returned object from the request
		 * result is the one the method needs
		 */
		private Object checkForCorrectType(Object value,
				Class<?> neededResponseType) {

			if (value == null) {
				return null;
			}

			/*
			 * default case
			 */
			if (neededResponseType.isAssignableFrom(value.getClass())) {
				return value;
			}

			/*
			 * The Jax-RS specification does not allow to return something else
			 * then a String or a Result object (I think..) so the following
			 * will probably never be used
			 */
			if (String.class.isAssignableFrom(value.getClass())) {
				String stringValue = (String) value;
				if (is(Integer.class, int.class, neededResponseType)) {
					return Integer.parseInt(stringValue);
				}
				if (is(Boolean.class, boolean.class, neededResponseType)) {
					return Boolean.parseBoolean(stringValue);
				}
				if (is(Double.class, double.class, neededResponseType)) {
					return Double.parseDouble(stringValue);
				}
				if (is(Float.class, float.class, neededResponseType)) {
					return Float.parseFloat(stringValue);
				}
				if (is(Long.class, long.class, neededResponseType)) {
					return Long.parseLong(stringValue);
				}
				if (is(short.class, Short.class, neededResponseType)) {
					return Short.parseShort(stringValue);
				}
				if (is(byte.class, Byte.class, neededResponseType)) {
					return Byte.parseByte(stringValue);
				}
				// additionally check for json objects and json arrays:
				try {
					if (neededResponseType.equals(JSONObject.class)) {
						return new JSONObject(stringValue);
					}
					if (neededResponseType.equals(JSONArray.class)) {
						return new JSONArray(stringValue);
					}
				} catch (JSONException e1) {
					Log.e(LOG_TAG, e1, "String could not be "
							+ "converted to an Json object");
					e1.printStackTrace();
				}
				/*
				 * check for valueOf(String s) or fromString(String s) methods
				 */
				try {
					Method m = null;
					try {
						m = neededResponseType.getMethod("valueOf",
								String.class);
					} catch (NoSuchMethodException e) {
					}
					if (m == null) {
						try {
							m = neededResponseType.getMethod("fromString",
									String.class);
						} catch (NoSuchMethodException e) {
						}
					}
					if (m != null && Modifier.isStatic(m.getModifiers())) {
						try {
							return m.invoke(null, stringValue);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
			if (JSONObject.class.isAssignableFrom(value.getClass())) {
				/*
				 * if the proxy handler returns a json object but the methods
				 * needs a string (so the json object as a string) it has to be
				 * converted back first. this means unneccecary json parsing so
				 * a warning is added here
				 */
				Log.w(LOG_TAG,
						"The returned object was a valid JSONObject but the method did expect a "
								+ neededResponseType
								+ ", so it was converted back to a string "
								+ "automatically. This is probably an unneccesary step: "
								+ "Change your rest interface to directly have a JsonObject as the return method!");
				return checkForCorrectType(((JSONObject) value).toString(),
						neededResponseType);
			}
			if (JSONArray.class.isAssignableFrom(value.getClass())) {
				Log.w(LOG_TAG,
						"The returned object was a valid JSONArray but the method did expect a "
								+ neededResponseType
								+ ", so it was converted back to a string "
								+ "automatically. This is probably an unneccesary step: "
								+ "Change your rest interface to directly have a JSONArray as the return method!");
				return checkForCorrectType(((JSONArray) value).toString(),
						neededResponseType);
			}
			return dontKnowWhatToCastThisTo(neededResponseType, value);
		}

		@SuppressWarnings("rawtypes")
		private boolean is(Class a, Class b, Class test) {
			return test.equals(a) || test.equals(b);
		}

		public String getStringForParamValue(Object param) {
			/*
			 * TODO what if a file or an input stream should be sent? this might
			 * not be the right place to allow only Strings as parameters
			 */
			if (param instanceof Iterable) {
				if (EXTENDED_LOGGING) {
					Log.i(LOG_TAG, "Will generate list representation for "
							+ param.getClass() + "-parameter");
				}
				String result = "";
				for (Object o : (Iterable) param) {
					if (o != null) {
						result += makeWebSaveString(o.toString()) + ",";
					}
				}
				if (EXTENDED_LOGGING) {
					Log.d(LOG_TAG, "   > generated list-string: " + result);
				}
				return result.substring(0, result.length() - 1);
			}
			if (param == null) {
				return null;
			}
			return param.toString();
		}

		/**
		 * Here all the possible fields etc can be reseted to be ready for a new
		 * request
		 */
		public abstract void initHandlerFields();

		public abstract void addHeaderParam(HeaderParam header,
				DefaultValue defAnn, String valueToSet);

		public abstract void setPostOrPutRequestBody(String valueToSet);

		public abstract void addFormParam(FormParam formAnn,
				DefaultValue defAnn, String valueToSet);

		public abstract void addCookieParam(CookieParam cookie,
				DefaultValue def, String valueToSet, String baseUrl);

		public abstract void addQuerryParam(QueryParam queryParam,
				DefaultValue def, String valueToSet);

		// , boolean dontMakeWebsave);

		/**
		 * Use something like URIUtil.encodeQuery(valueToSet) or
		 * Uri.encode(valueToSet) depending on the plattform
		 * 
		 * @param valueToSet
		 * @return
		 */
		public abstract String makeWebSaveString(String valueToSet);

		/**
		 * use e.g. HttpMethod.GET.equals(httpMethodType) to check for the HTTP
		 * method type and then create a request according to this
		 * 
		 * @param httpMethodType
		 *            will be e.g. HttpMethod.GET
		 * @param path
		 *            the URL path e.g. http://www.google.de (but without the
		 *            query parameters added!)
		 * @param typesThatCanBeProducedByServer
		 *            media types that can be produced by the server
		 * @param contentType
		 *            the media types the server accepts for sent content
		 * @param responseType
		 *            The class type the result has to have
		 * @return the response from the server
		 */
		public abstract Object doRequest(String httpMethodType, String path,
				String[] typesThatCanBeProducedByServer, String contentType,
				Class<?> responseType);

		public abstract void warningFromJaxRsParsingAnalysis(String warning);

		/**
		 * The system can't create the object of type responseType automatically
		 * so it has to be done manually
		 * 
		 * @param responseType
		 * @param value
		 *            The String where to extract the object to create from
		 * @return the created object (needs to have the type responseType!)
		 */
		public abstract Object dontKnowWhatToCastThisTo(Class<?> responseType,
				Object value);

		/**
		 * This method is called when the passed parameter value did not match
		 * the regex expression
		 * 
		 * @param wordToReplace
		 * @param valueToSet
		 * @param regexToMatch
		 * @return true if the request should be continued even if the parameter
		 *         did not match the regex expression
		 */
		public abstract boolean parameterDidNotMatchRegex(String wordToReplace,
				String valueToSet, String regexToMatch);

		/**
		 * Searches the specified list if it contains an annotation of the
		 * passed annotationType and returns it.
		 * 
		 * @param annotationType
		 * @param annotationList
		 * @return The found annotation or null if there was none in the list
		 */
		@SuppressWarnings("unchecked")
		private <T> T getAnnotation(Class<T> annotationType,
				Annotation[] annotationList) {
			for (Annotation a : annotationList) {
				if (annotationType.isAssignableFrom(a.getClass())) {
					return (T) a;
				}
			}
			return null;
		}

		public String replaceReservedChars(String methodPath) {
			/*
			 * find the most inner { and } bracelets and replace them with
			 * special chars
			 */
			return transformInnerBracelets(methodPath);
		}

		// could be any char, just use one that will not occur in any regex
		// normally
		public static final char REPL_CHAR = (char) 0x034F;

		private String restoreInnerBracelets(String transformedTestString) {
			String revertSpecialChar = "([" + REPL_CHAR + "])(\\d*[,]*\\d*)(["
					+ REPL_CHAR + "])";
			return transformedTestString.replaceAll(revertSpecialChar, "{"
					+ "$2" + "}");
		}

		/**
		 * regex bracelets can contain numbers {23423} and something like
		 * {2,453}
		 * 
		 * @param testString
		 * @return
		 */
		private String transformInnerBracelets(String testString) {
			String setToSpecialChar = "([{])(\\d*[,]*\\d*)([}])";
			return testString.replaceAll(setToSpecialChar, REPL_CHAR + "$2"
					+ REPL_CHAR);
		}

		public String insertInPath(String path, PathParam a,
				DefaultValue defaultValueForPathParam, String valueToSet,
				boolean makeWebsaveString) {

			if (valueToSet == null) {
				valueToSet = defaultValueForPathParam.value();
			}

			String wordToReplace = a.value();

			if (!path.contains(wordToReplace)) {
				warningFromJaxRsParsingAnalysis("Jax RS path definition incorrect! "
						+ wordToReplace
						+ " was not specified in the url regex!");
				return null;
			}

			Matcher m = Pattern
					.compile("[{][^}]*" + wordToReplace + "[^{]*[}]").matcher(
							path);
			m.find();
			int firstChar = m.start() + 1;
			int lastChar = m.end() - 1;

			String pathPartForCurrentParam = (String) path.subSequence(
					firstChar, lastChar);

			String regex = getRegexIfThereIsOne(pathPartForCurrentParam,
					wordToReplace);
			if (regex != null) {
				if (!Pattern.compile(regex).matcher(valueToSet).matches()) {
					/*
					 * the passed value does not match the extracted regex so
					 * the request can fail before the network connection is
					 * even used
					 */
					if (!parameterDidNotMatchRegex(wordToReplace, valueToSet,
							regex)) {
						return null;
					}
				}
			}

			/*
			 * Then after the regex evaluation replace the word with its value
			 */
			if (makeWebsaveString) {
				valueToSet = makeWebSaveString(valueToSet);
			}
			path = m.replaceFirst(valueToSet);
			return path;
		}

		private String getRegexIfThereIsOne(String pathPartForCurrentParam,
				String wordToReplace) {
			/*
			 * extract evaluate regex if there is one for fast failing on client
			 * side
			 */
			String regexToExtractRegexFromPath = "([\\s]*" + wordToReplace
					+ "[\\s]*)([:][\\s]*)(.*)"; // yo dawg
			String regex = pathPartForCurrentParam.replaceFirst(
					regexToExtractRegexFromPath, "$3");
			// remove leading and ending whitespaces
			regex = regex.replaceAll("([\\s]*)(.*[^\\s])([\\s]*)", "$2");
			if (regex.equals(wordToReplace)) {
				return null;
			}
			// restore the inner bracelets in the regex if there are some
			regex = restoreInnerBracelets(regex);
			return regex;
		}

		private String getContentType(Object proxy, Method method) {
			// determine content type
			String contentType = null;

			Consumes consumes = method.getAnnotation(Consumes.class);
			if (consumes == null) {
				Class<?> proxyIfc = proxy.getClass().getInterfaces()[0]; // TODO
																			// 0?
				consumes = proxyIfc.getAnnotation(Consumes.class);
			}
			if (consumes != null && consumes.value().length > 0) {
				// TODO: should consider q/qs instead of picking the first one
				contentType = consumes.value()[0]; //
			}
			return contentType;
		}

		private HttpMethod getRestMethodType(Method method) {
			HttpMethod httpMethod = getHttpMethodName(method);
			if (httpMethod == null) {
				for (Annotation ann : method.getAnnotations()) {
					httpMethod = getHttpMethodName(ann.annotationType());
					if (httpMethod != null) {
						break;
					}
				}
			}
			return httpMethod;
		}

		/**
		 * "The @Produces annotation is used to specify the MIME media types of
		 * representations a resource can produce and send back to the client."
		 * 
		 * @param proxy
		 * @param method
		 * @return the list of types the server can send back to the client
		 */
		private String[] getAcceptedMediaTypes(Object proxy, Method method) {
			// accepted media types
			Class<?> proxyIfc = proxy.getClass().getInterfaces()[0]; // TODO 0?
			Produces produces = method.getAnnotation(Produces.class);
			if (produces == null) {
				/*
				 * check if the annotation is at the class itself and not at the
				 * method
				 */
				produces = proxyIfc.getAnnotation(Produces.class);
			}
			String[] acceptedMediaTypes = produces == null ? null : produces
					.value();
			return acceptedMediaTypes;
		}

		private HttpMethod getHttpMethodName(AnnotatedElement ae) {
			return ae.getAnnotation(HttpMethod.class);
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> c, String url,
			ProxyMethodCallHandler methodCallHandler) {
		methodCallHandler.setProxiedClass(c);
		methodCallHandler.setBaseUrl(url);
		return (T) Proxy.newProxyInstance(c.getClassLoader(),
				new Class[] { c }, methodCallHandler);
	}

	public static String getHostForUrl(String domainUrl) {
		if (domainUrl.startsWith("http://")) {
			try {
				URL url = new URL(domainUrl);
				return url.getHost();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return domainUrl;
			}
		}
		if (domainUrl.contains(":")) {
			return domainUrl.substring(0, domainUrl.indexOf(':'));
		}
		if (domainUrl.contains("/")) {
			return domainUrl.substring(0, domainUrl.indexOf('/'));
		}
		return domainUrl;
	}

}
