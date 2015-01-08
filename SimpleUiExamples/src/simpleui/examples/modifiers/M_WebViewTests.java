package simpleui.examples.modifiers;

import java.util.ArrayList;
import java.util.List;

import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_RadioButtonList;
import simpleui.modifiers.v3.M_Spinner;
import simpleui.modifiers.v3.M_WebView;
import android.content.Context;

public class M_WebViewTests extends M_Container {
	public M_WebViewTests(final Context c) {
		M_Spinner s = new M_Spinner() {

			@Override
			public String getVarName() {
				return "Choose one";
			}

			@Override
			public List<SpinnerItem> loadListToDisplay() {
				List<SpinnerItem> l = new ArrayList<SpinnerItem>();
				l.add(new SpinnerItem(1, "Test 1"));
				l.add(new SpinnerItem(2, "Test 2"));
				l.add(new SpinnerItem(4, "Test 4"));
				l.add(new SpinnerItem(3, "Test 3"));
				return l;
			}

			@Override
			public int loadSelectedItemId() {
				return 4;
			}

			@Override
			public boolean save(SpinnerItem selectedItem) {
				System.out.println("selected item with id="
						+ selectedItem.getId());
				return true;
			}
		};

		add(s);

		add(new M_RadioButtonList() {

			@Override
			public boolean save(SelectableItem i) {
				System.out.println("Selected item=" + i.getText());
				return true;
			}

			@Override
			public Integer loadSelectedItemId() {
				return 1;
			}

			@Override
			public List<SelectableItem> getItemList() {
				ArrayList<SelectableItem> l = new ArrayList<M_RadioButtonList.SelectableItem>();
				l.add(new SelectableItem() {

					@Override
					public String getText() {
						return "AAA";
					}

					@Override
					public int getId() {
						return 1;
					}
				});
				l.add(new SelectableItem() {

					@Override
					public String getText() {
						return "BBB";
					}

					@Override
					public int getId() {
						return 2;
					}
				});
				return l;
			}

		});

		add(new M_WebView(true, true) {

			@Override
			protected void onPageLoaded(String html) {
			}

			@Override
			public void onPageLoadProgress(int progressInPercent) {
			}

			@Override
			public String getUrlToDisplay() {
				return "file:///android_asset/" + "FAQ.htm";
			}

		});
	}
}
