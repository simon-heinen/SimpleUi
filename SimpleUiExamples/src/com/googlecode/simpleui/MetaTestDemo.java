package com.googlecode.simpleui;

import module.meta.MetaAttr;
import module.meta.MetaValidator;
import module.meta.RegexUtils;

import org.json.JSONException;

import v2.simpleUi.M_Container;

import com.google.gson.Gson;

public class MetaTestDemo extends M_Container {

	public static class ExampleClass {

		@MetaAttr(type = MetaAttr.TYPE_ID + MetaAttr.FIELDS_READ_ONLY
				+ MetaAttr.FIELDS_UNIQ_IN_SCOPE)
		private Long id;

		@MetaAttr(regex = "YES|MAYBE|NO")
		private String myField1;
		@MetaAttr(regex = RegexUtils.EMAIL_ADDRESS)
		private String email;
		@MetaAttr(type = MetaAttr.TYPE_DATE_LONG)
		private Long createDate;

	}

	public MetaTestDemo() {

		MetaValidator m = new MetaValidator();

		ExampleClass oldObjA = new ExampleClass();
		Gson gson = new Gson();
		ExampleClass objA = gson.fromJson(gson.toJson(oldObjA),
				ExampleClass.class);
		try {
			m.validateUpdateObject(oldObjA, objA);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
