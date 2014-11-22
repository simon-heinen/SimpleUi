package com.googlecode.simpleui;

import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import v1.BoolModifier;
import v1.DoubleModifier;
import v1.HasTheme;
import v1.Headline;
import v1.ModifierGroup;
import v1.TextModifier;
import v1.Theme;
import v1.Theme.ThemeColors;
import v1.V1SimpleUiController;
import v2.simpleUi.ModifierInterface;
import android.content.Context;

public class JsonEditorUI implements V1SimpleUiController {

	private JSONObject myJsonObj;
	private Theme myTheme;

	public JsonEditorUI(JSONObject jsonObj, Context context) {
		myJsonObj = jsonObj;
		myTheme = Theme.A(context, ThemeColors.initToBlack());
	}

	private JSONObject getJsonObj() {
		return myJsonObj;
	}

	private void createModifiers(List<ModifierInterface> modifierList,
			JSONObject o) {
		try {

			@SuppressWarnings("rawtypes")
			Iterator i = o.keys();
			while (i.hasNext()) {
				String attrName = (String) i.next();
				Object attr = o.get(attrName);
				if (attr instanceof String)
					addStringModifier(modifierList, o, attrName, (String) attr);
				if (attr instanceof Double)
					addDoubleModifier(modifierList, o, attrName, (Double) attr);
				if (attr instanceof Boolean)
					addBoolModifier(modifierList, o, attrName, (Boolean) attr);
				if (attr instanceof JSONObject) {
					modifierList.add(new Headline(firstToUpper(attrName)));
					ModifierGroup newGroup = new ModifierGroup();
					createModifiers(newGroup, (JSONObject) attr);
					modifierList.add(newGroup);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addBoolModifier(List<ModifierInterface> modifierList,
			final JSONObject o, final String attrName, final Boolean attr) {
		modifierList.add(new BoolModifier() {

			@Override
			public boolean save(boolean newValue) {
				return saveJsonAttr(o, attrName, attr);
			}

			@Override
			public boolean loadVar() {
				return attr;
			}

			@Override
			public CharSequence getVarName() {
				return firstToUpper(attrName);
			}
		});

	}

	protected String firstToUpper(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	private void addDoubleModifier(List<ModifierInterface> modifierList,
			final JSONObject o, final String attrName, final Double attr) {
		modifierList.add(new DoubleModifier() {

			@Override
			public boolean save(double newValue) {
				return saveJsonAttr(o, attrName, attr);
			}

			@Override
			public double load() {
				return attr;
			}

			@Override
			public String getVarName() {
				return firstToUpper(attrName);
			}
		});

	}

	private void addStringModifier(List<ModifierInterface> modifierList,
			final JSONObject o, final String attrName, final String attr) {
		modifierList.add(new TextModifier() {

			@Override
			public boolean save(String newValue) {
				return saveJsonAttr(o, attrName, attr);
			}

			@Override
			public String load() {
				return attr;
			}

			@Override
			public String getVarName() {
				return firstToUpper(attrName);
			}
		});
	}

	private boolean saveJsonAttr(final JSONObject o, final String attrName,
			final Object attr) {
		try {
			o.put(attrName, attr);
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void customizeScreen(List<ModifierInterface> modifierList,
			Object optionalMessage) {
		if (modifierList instanceof HasTheme)
			((HasTheme) modifierList).setTheme(myTheme); // TODO remove

		createModifiers(modifierList, getJsonObj());
	}
}
