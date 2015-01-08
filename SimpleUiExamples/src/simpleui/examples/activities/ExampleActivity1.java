package simpleui.examples.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import simpleui.SimpleUI;
import simpleui.examples.M_CardViewTests;
import simpleui.examples.injection.ExampleButterknifeAndDaggerActivity;
import simpleui.examples.listwrapper.M_ListWrapperV2Tests;
import simpleui.examples.maps.ExampleMapActivity;
import simpleui.examples.maps.M_GoogleMapsMarkLocationTests;
import simpleui.examples.maps.M_GoogleMapsV2Tests;
import simpleui.examples.modifiers.M_DashboardTests;
import simpleui.examples.modifiers.M_MakePhotoTests;
import simpleui.examples.modifiers.M_RatingBarTests;
import simpleui.examples.modifiers.M_WebViewTests;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Container;
import simpleui.modifiers.v3.M_Container2;
import simpleui.modifiers.v3.M_DateModifier;
import simpleui.modifiers.v3.M_FilePickerButton;
import simpleui.modifiers.v3.M_HalfHalf;
import simpleui.modifiers.v3.M_ImageView;
import simpleui.modifiers.v3.M_InfoText;
import simpleui.modifiers.v3.M_ItemBar;
import simpleui.modifiers.v3.M_RadioButtonListCreator;
import simpleui.modifiers.v3.M_SpinnerWithCheckboxes;
import simpleui.modifiers.v3.M_SpinnerWithCheckboxes.SpinnerItem;
import simpleui.modifiers.v3.M_SpinnerWithCheckboxesCreator2;
import simpleui.modifiers.v3.M_SpinnerWithCheckboxesCreator2.DefaultSpinnerItem;
import simpleui.modifiers.v3.M_TextModifier;
import simpleui.modifiers.v3.maps.SimpleUIWithMaps;
import simpleui.util.DragAndDropListener;
import simpleui.util.ErrorHandler;
import simpleui.util.IO;
import simpleui.util.IntentHelper;
import simpleui.util.ProgressScreen;
import simpleui.util.SimpleAsyncTask;
import simpleui.util.ToastV2;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * See also {@link ExampleActivity2}
 */
public class ExampleActivity1 extends Activity {

	protected static final String LOG_TAG = ExampleActivity1.class
			.getSimpleName();

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ErrorHandler.registerNewErrorHandler(this,
				"errors/testErrorHandlerSimpleUiTests");
		ErrorHandler.enableEmailReports("simon.heinen@gmail.com",
				"Error in SimpleUi Test project");

		// throwExceptionForErrorHandlerTesting();

		M_Container c = new M_Container();
		c.add(new M_Button("Some Material UI tests") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				try {
					SimpleUI.showInfoDialog(context, "Close",
							new M_CardViewTests());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		c.add(new M_Button("ToastV2: Undo example") {

			@Override
			public void onClick(Context context, Button b) {
				ToastV2.showUndoToast(context, "Item deleted", "Undo",
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								System.out.println("Undo clicked");
							}
						});
			}
		});

		c.add(new M_Button("Copy assets to SD Card test") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				try {
					File targetFolder = new File(Environment
							.getExternalStorageDirectory(), "TestAssets");
					targetFolder.mkdirs();
					IO.copyAssets(context.getAssets(), targetFolder);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		final File fileToShare = new File(
				Environment.getExternalStorageDirectory(), "img.jpg");
		if (fileToShare.exists()) {
			c.add(new M_Button(
					"Share Button test: Share testimage img.jpg to Google+ ") {

				@Override
				public void onClick(Context context, Button arg1) {
					Intent i = IntentHelper.newSendImageIntent(fileToShare,
							"aaa", "bbb", "ccc");
					IntentHelper.launchGooglePlus(ExampleActivity1.this, i);
				}
			});
		}

		M_InfoText info = new M_InfoText(
				"Click on the http://www.google.de/ link! Or the wikipedia.org link..");
		info.setContainsUrls(true);
		c.add(info);

		c.add(new M_Button("Show " + ExampleButterknifeAndDaggerActivity.class
				+ " activity") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				ExampleActivity1.this.startActivity(new Intent(
						ExampleActivity1.this,
						ExampleButterknifeAndDaggerActivity.class));
			}
		});

		c.add(new M_HalfHalf(new M_ImageView(Uri
				.parse("http://lorempixel.com/200/320/")), new M_InfoText(
				"<-image")));

		addListWrapperTestUi(c);

		addCrashButtonForErrorHandlerTesting(c);

		addTestFileSelector(c);

		c.add(new M_Button("Show M_MakePhotoTests") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				SimpleUI.showInfoDialog(context, "Close",
						new M_MakePhotoTests());
			}
		});

		mapsV2Tests(c);
		addInfoTextFormattingTests(c);
		addDateFormattingTests(c);
		addcheckboxtestingstuff(c);
		addcheckboxtestingstuff2(c);
		add5SecWaitButton(c);

		addM_ItemBar(c);
		testSpinnerWithCheckboxes(c);
		testBasicModifiers(c);

		addTestContainer2Ui(c);

		addWebViewTest(c);

		c.add(new M_Button(
				"Start normal activity (ExampleActivity2) which uses SimpleUI "
						+ "to generate its UI content") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				ExampleActivity1.this.startActivity(new Intent(
						ExampleActivity1.this, ExampleActivity2.class));
			}
		});
		c.add(new M_Button("Start ExampleSurveyActivity") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				ExampleActivity1.this.startActivity(new Intent(
						ExampleActivity1.this, ExampleSurveyActivity1.class));
			}
		});

		c.add(new M_Button("Show M_DashboardTests") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				SimpleUI.showUi(ExampleActivity1.this, new M_DashboardTests());
			}
		});

		c.add(new M_Button("Show M_RatinBarTests") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				SimpleUI.showInfoDialog(context, "Close",
						new M_RatingBarTests());

			}
		});

		c.add(new M_Button("Close SimpleUi Demp App") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				ExampleActivity1.this.finish();
			}
		});

		setContentView(c.getView(this));

	}

	private void throwExceptionForErrorHandlerTesting() {
		Object o = null;
		o.toString();
	}

	private void addListWrapperTestUi(M_Container c) {

		final ArrayList<String> answers = new ArrayList<String>();
		answers.add("aA");
		answers.add("aB");
		answers.add("aC");

		final ArrayList<String> questions = new ArrayList<String>();
		questions.add("fA");
		questions.add("fB");
		questions.add("fC");

		final ArrayList<Long> numbers = new ArrayList<Long>();
		numbers.add(2L);
		numbers.add(1L);
		numbers.add(0L);

		c.add(new M_Button("List wrapper test ui") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				M_Container c = new M_Container();

				// c.add(M_ListWrapperV2.newStringCollectionModifier(l1,
				// "Add"));
				M_ListWrapperV2Tests
						.generateEditUiForAssociationQuestionQuestion(c,
								answers, questions, numbers);
				SimpleUI.showCancelOkDialog(context, "Cancel", "Ok", c);
			}
		});

	}

	private void addCrashButtonForErrorHandlerTesting(M_Container c) {
		c.add(new M_Button("Crash") {

			@Override
			public void onClick(Context context, Button clickedButton) {

				String s = null;
				s.toCharArray();
			}
		});
	}

	private void addTestFileSelector(M_Container c) {

		c.add(new M_Button("File-Tests") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				M_Container c2 = new M_Container();

				c2.add(new M_FilePickerButton("Load file") {
					@Override
					public void onFilePathReceived(Activity a, String filePath,
							File file, Intent data) {
						Toast.makeText(a, "path=" + filePath, Toast.LENGTH_LONG)
								.show();
					}
				});
				SimpleUI.showCancelOkDialog(ExampleActivity1.this, "Cancel",
						"Ok", c2);

			}
		});
	}

	private void addWebViewTest(M_Container c) {
		c.add(new M_Button("Web view Example") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				SimpleUI.showInfoDialog(context, "Ok", new M_WebViewTests(
						context));
			}
		});
	}

	private void addTestContainer2Ui(M_Container c) {
		M_Container2 c2 = new M_Container2("Click me to show my content", true);
		c2.add(new M_InfoText("Karl", "otfto"));
		c2.add(new M_InfoText("Ka7rl", "o9tto"));
		c2.add(new M_InfoText("Kar234l", "votto"));
		c2.add(new M_InfoText("Karvl", "otto234"));
		c2.add(new M_InfoText("K5arl", "ot234to"));
		c2.add(new M_InfoText("Ka23rl", "otyto"));
		c2.add(new M_InfoText("K6arl", "ott5o"));
		c.add(c2);
	}

	private void addM_ItemBar(M_Container c) {
		M_ItemBar itemBar = new M_ItemBar();
		itemBar.add(2F, new M_InfoText("ABC"));
		itemBar.add(1F, new M_InfoText("DEF"));
		itemBar.add(1F, new M_InfoText("ABC"));
		itemBar.add(1F, new M_InfoText("DEF"));
		itemBar.add(1F, new M_InfoText("ABC"));
		itemBar.add(1F, new M_InfoText("DEF"));
		itemBar.add(1F, new M_InfoText("ABC"));
		c.add(itemBar);
	}

	private void testBasicModifiers(M_Container c) {
		c.add(new simpleui.modifiers.v3.M_IntModifier() {

			@Override
			public int loadInt() {
				return Integer.MAX_VALUE;
			}

			@Override
			public boolean saveInt(int intValue) {
				Log.d(LOG_TAG, "new value=" + intValue);
				return false;
			}

			@Override
			public String getVarName() {
				return "int";
			}

		});

		c.add(new simpleui.modifiers.v3.M_DoubleModifier() {

			@Override
			public String getVarName() {
				return "double";
			}

			@Override
			public double loadDouble() {
				return Double.MAX_VALUE;
			}

			@Override
			public boolean saveDouble(double doubleValue) {
				Log.d(LOG_TAG, "new value=" + doubleValue);
				return false;
			}

		});

		c.add(new simpleui.modifiers.v3.M_LongModifier() {

			@Override
			public String getVarName() {
				return "double";
			}

			@Override
			public long loadLong() {
				return Long.MAX_VALUE;
			}

			@Override
			public boolean saveLong(long longValue) {
				Log.d(LOG_TAG, "new value=" + longValue);
				return false;
			}

		});

		M_TextModifier m = new simpleui.modifiers.v3.M_TextModifier() {

			@Override
			public String load() {
				return "test";
			}

			@Override
			public String getVarName() {
				return "text modifier";
			}

			@Override
			public boolean save(String newValue) {
				Log.d(LOG_TAG, "new value=" + newValue);
				return false;
			}

			@Override
			public View getView(Context context) {
				// TODO Auto-generated method stub
				View v = super.getView(context);
				v.setOnTouchListener(new DragAndDropListener() {
					@Override
					public void onElementDropped(float rawX, float rawY) {
						System.out.println("rawX=" + rawX);
						System.out.println("rawY=" + rawY);
					}
				});
				return v;
			}

		};
		c.add(m);
	}

	private void testSpinnerWithCheckboxes(M_Container c) {
		final List<SpinnerItem> list = new ArrayList<SpinnerItem>();
		list.add(new SpinnerItem(1, "Aaaa", true));
		list.add(new SpinnerItem(2, "Bbbb", false));
		list.add(new SpinnerItem(3, "Cccc", false));
		list.add(new SpinnerItem(4, "Dddd", true));
		list.add(new SpinnerItem(1, "Aaaa", true));
		list.add(new SpinnerItem(2, "Bbbb", false));
		list.add(new SpinnerItem(3, "Cccc", false));
		list.add(new SpinnerItem(4, "Dddd", true));
		list.add(new SpinnerItem(1, "Aaaa", true));
		list.add(new SpinnerItem(2, "Bbbb", false));
		list.add(new SpinnerItem(3, "Cccc", false));
		list.add(new SpinnerItem(4, "Dddd", true));

		c.add(new M_SpinnerWithCheckboxes() {

			@Override
			public boolean save(List<SpinnerItem> list) {
				return true;
			}

			@Override
			public List<SpinnerItem> loadListToDisplay() {
				return list;
			}

			@Override
			public String getVarName() {
				return "Demo Var";
			}
		});
	}

	private void addDateFormattingTests(M_Container c) {
		c.add(new M_DateModifier() {
			Date d = getDateFor(17, 11, 1986);

			@Override
			public String getTextFor(Date d, String dateSting) {
				return "Date: " + dateSting;
			}

			@Override
			public Date loadDate() {
				return d;
			}

			@Override
			public boolean save(Date newSelectedDate) {
				d = newSelectedDate;
				return true;
			}

		});
	}

	private void addInfoTextFormattingTests(M_Container c) {
		c.add(new M_InfoText("sssssssssssssssssssssssssssssssssssssssss",
				"abcsfrb gdsfrb1"));
		c.add(new M_InfoText("asd a Tes wer t2", "sdvgssd gbvsd rfgrderfb"));
		c.add(new M_InfoText("as Terwe werwe werwe est3", "abrfgdsfrbgd rfc1"));
		c.add(new M_InfoText(
				"Tesdsfrt1",
				"srdsdwerwer werwer werwe rfgdsrfgsrger werwe werw wewerw wewr werw ewe werwe rwerw wer rwerwer werwerwe werwe werwe w werwerwerw werwer wsfrsfr"));
		c.add(new M_InfoText("asda Tewerw werwe ssrbt1", "abdsfgsdf rbc1"));
		c.add(new M_InfoText("Tedsfrdsst1", "absrfbds frc1"));
		c.add(new M_InfoText("Terfgdsrfrst1", "abdsf  rb werwe dsfrc1"));

		c.add(new M_InfoText(R.drawable.ic_dialog_alert, "sdfsefswegf"));
		c.add(new M_InfoText("sdfsefswegf"));
		c.add(new M_InfoText(R.drawable.ic_dialog_alert, "sdf\nse\nfsw\negf"));
	}

	private void mapsV2Tests(M_Container c) {
		c.add(new M_Button("Google Maps v2") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				SimpleUIWithMaps.showUi(ExampleActivity1.this,
						new M_GoogleMapsV2Tests(), ExampleMapActivity.class);
			}
		});
		c.add(new M_Button("Select pos on map v2") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				SimpleUIWithMaps.showUi(ExampleActivity1.this,
						new M_GoogleMapsMarkLocationTests(),
						ExampleMapActivity.class);
			}
		});
	}

	protected void addcheckboxtestingstuff(M_Container c) {
		final ArrayList<simpleui.modifiers.v3.M_SpinnerWithCheckboxesCreator2.SpinnerItem> l = new ArrayList<M_SpinnerWithCheckboxesCreator2.SpinnerItem>();
		l.add(new DefaultSpinnerItem("AA", true));
		l.add(new DefaultSpinnerItem("BB", false));
		l.add(new DefaultSpinnerItem("CC", true));
		l.add(new DefaultSpinnerItem("DD", false));
		l.add(new DefaultSpinnerItem("EE", true));

		c.add(new M_Button("CheckboxCreator") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				M_Container c = new M_Container();
				c.add(new M_SpinnerWithCheckboxesCreator2() {
					@Override
					public String getAddItemButtonText() {
						return "Neue Antwort";
					}

					@Override
					public List<simpleui.modifiers.v3.M_SpinnerWithCheckboxesCreator2.SpinnerItem> getItemList() {
						return l;
					}

					@Override
					public boolean save(List<SpinnerItem> itemList,
							ArrayList<Integer> selectedItemIdsList) {
						return !selectedItemIdsList.isEmpty();
					}

				});
				SimpleUI.showCancelOkDialog(context, "cancel", "ok", c);
			}
		});
	}

	protected void addcheckboxtestingstuff2(M_Container c) {
		final ArrayList<String> l = new ArrayList<String>();
		l.add("A");
		l.add("B");
		l.add("C");
		l.add("D");
		l.add("E");

		c.add(new M_Button("radiobutton creator") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				M_Container c = new M_Container();
				c.add(new M_RadioButtonListCreator() {

					private Integer selectedItemNr = 1;

					@Override
					public boolean save(ArrayList<String> itemList,
							Integer selectedItemNr) {
						if (selectedItemNr == -1) {
							return false;
						}
						this.selectedItemNr = selectedItemNr;
						return true;
					}

					@Override
					public Integer getSelectedItemNr() {
						return selectedItemNr;
					}

					@Override
					public ArrayList<String> getItemList() {
						return l;
					}

					@Override
					public String getAddItemButtonText() {
						return "Add";
					}
				});
				SimpleUI.showCancelOkDialog(context, "cancel", "ok", c);
			}
		});
	}

	protected void add5SecWaitButton(M_Container c) {
		c.add(new M_Button("Do something 5s long") {

			ProgressScreen s = new ProgressScreen() {

				@Override
				public boolean onAbortRequest() {
					return true;
				}

			};

			@Override
			public void onClick(Context context, Button clickedButton) {
				s.setProgressImageIds(R.drawable.ic_dialog_alert,
						R.drawable.ic_dialog_info);
				s.start(context);
				new SimpleAsyncTask() {

					@Override
					public void onRun() {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						s.finish();
					}
				}.execute();
			}
		});
	}

}
