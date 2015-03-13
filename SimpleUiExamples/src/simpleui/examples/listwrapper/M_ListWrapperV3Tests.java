package simpleui.examples.listwrapper;

import java.util.ArrayList;
import java.util.List;

import simpleui.SimpleUI;
import simpleui.adapters.SimpleBaseAdapter;
import simpleui.adapters.SimpleBaseAdapter.HasItsOwnView;
import simpleui.examples.R;
import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.M_Container1;
import simpleui.modifiers.v1.M_ListWrapperV3Editable;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.modifiers.v3.M_ListWrapperV3;
import simpleui.modifiers.v3.M_ListWrapperV4Editable;
import simpleui.util.ColorUtils;
import simpleui.util.NameGenerator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class M_ListWrapperV3Tests {

	protected static final String LOG_TAG = M_ListWrapperV3Tests.class
			.getSimpleName();
	private static boolean testWithoutContainer = false;

	public static M_Button newButtonForM_ListWrapperV3Grid() {

		final List<HasItsOwnView> l = new ArrayList<HasItsOwnView>();
		final NameGenerator n = new NameGenerator();
		for (int i = 0; i < 200; i++) {
			l.add(newElement(n));
		}

		return new M_Button("Listview tests (with grid design)") {

			@Override
			public void onClick(Context context, Button arg1) {
				M_ListWrapperV3<HasItsOwnView> m = new M_ListWrapperV3<HasItsOwnView>(
						l, 800, 3, true);
				if (testWithoutContainer) {
					SimpleUI.showUi(context, m);
				} else {
					M_Container c = new M_Container();
					c.add(m);
					for (int j = 0; j < 20; j++) {
						c.add(new M_InfoText("" + j));
					}
					SimpleUI.showCancelOkDialog(context, "Cancel", "Ok", c);
				}
			}

		};
	}

	public static M_Button newButtonForM_ListWrapperV3Tests() {

		final List<HasItsOwnView> l = new ArrayList<HasItsOwnView>();
		final NameGenerator n = new NameGenerator();
		for (int i = 0; i < 200; i++) {
			l.add(newElement(n));
		}

		return new M_Button("M_ListWrapperV3Editable tests") {

			@Override
			public void onClick(Context context, Button clickedButton) {

				boolean instantModelUpdates = false;
				ModifierInterface m = new M_ListWrapperV3Editable<HasItsOwnView>(
						l, "+Add+", instantModelUpdates) {

					@Override
					public HasItsOwnView getNewItemInstance(Context arg0,
							int arg1) {
						return newElement(n);
					}

					@Override
					public boolean onAddRequest(HasItsOwnView arg0) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public boolean onRemoveRequest(HasItsOwnView arg0) {
						// TODO Auto-generated method stub
						return false;
					}

				};

				if (testWithoutContainer) {
					SimpleUI.showUi(context, m);
				} else {
					M_Container1 c = new M_Container1();
					c.add(m);
					for (int j = 0; j < 20; j++) {
						c.add(new M_InfoText("" + j));
					}
					SimpleUI.showCancelOkDialog(context, "Cancel", "Ok", c);
				}
			}

		};
	}

	public static M_Button newButtonForM_ListWrapperV4Tests() {

		final List<HasItsOwnView> l = new ArrayList<HasItsOwnView>();
		final NameGenerator n = new NameGenerator();
		for (int i = 0; i < 200; i++) {
			l.add(newElement2(n));
		}

		return new M_Button("M_ListWrapperV4Editable tests") {

			private M_ListWrapperV4Editable<HasItsOwnView> listModifier;

			@Override
			public void onClick(Context context, Button clickedButton) {

				boolean instantModelUpdates = false;
				View a = new M_InfoText("Top").getView(context);
				View b = new M_Button("Add new element at beginning") {

					@Override
					public void onClick(Context arg0, Button arg1) {
						System.out.println("Top Clicked");
						listModifier.addListElement(0, newElement2(n));
					}
				}.getView(context);

				listModifier = new M_ListWrapperV4Editable<HasItsOwnView>(l,
						instantModelUpdates, R.id.front, R.id.back, a, b) {

					@Override
					public boolean onRemoveRequest(HasItsOwnView arg0) {
						return true; // always allow delete in this demo
					}

				};

				if (testWithoutContainer) {
					SimpleUI.showUi(context, listModifier);
				} else {
					M_Container1 c = new M_Container1();
					c.add(listModifier);
					for (int j = 0; j < 20; j++) {
						c.add(new M_InfoText("" + j));
					}
					SimpleUI.showCancelOkDialog(context, "Cancel", "Ok", c);
				}
			}

		};
	}

	private static HasItsOwnView newElement(final NameGenerator n) {
		final String name = n.getName() + " " + n.getName();
		return new HasItsOwnView() {

			@Override
			public boolean onItemLongClick(View itemView, int posInList) {
				System.out.println("onItemLongClick: " + name);
				return true;
			}

			@Override
			public void onItemClick(View itemView, int posInList) {
				System.out.println("onItemClick: " + name);
			}

			@Override
			public View getView(Context context, View convertView,
					ViewGroup parent, SimpleBaseAdapter simpleBaseAdapter,
					List<? extends HasItsOwnView> containerList,
					int positionInList) {
				TextView t;
				if (convertView != null && convertView instanceof TextView) {
					t = (TextView) convertView;
				} else {
					t = new TextView(context);
					t.setBackgroundColor(ColorUtils.randomColor());
					t.setGravity(Gravity.CENTER_HORIZONTAL);
					int p = 40;
					t.setPadding(p, p, p, p);
				}
				t.setText(name);
				t.setVisibility(View.VISIBLE);
				return t;
			}
		};
	}

	private static HasItsOwnView newElement2(final NameGenerator n) {
		final String name = n.getName() + " " + n.getName();
		return new ExampleListItemView(name);
	}
}
