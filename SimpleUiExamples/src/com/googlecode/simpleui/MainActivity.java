package com.googlecode.simpleui;

import injectionTests.ButterknifeAndDaggerTestActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tools.DragAndDropListener;
import tools.ErrorHandler;
import tools.IO;
import tools.IntentHelper;
import tools.SimpleAsyncTask;
import v1.Headline;
import v1.InfoText;
import v1.V1SimpleUI;
import v1.V1SimpleUiController;
import v2.simpleUi.M_Button;
import v2.simpleUi.M_Caption;
import v2.simpleUi.M_Checkbox;
import v2.simpleUi.M_Container;
import v2.simpleUi.M_Container2;
import v2.simpleUi.M_Double;
import v2.simpleUi.M_EmailInput;
import v2.simpleUi.M_InfoText;
import v2.simpleUi.M_Integer;
import v2.simpleUi.M_LeftRight;
import v2.simpleUi.M_PlusMinus;
import v2.simpleUi.M_SpinnerWithCheckboxes;
import v2.simpleUi.M_SpinnerWithCheckboxes.SpinnerItem;
import v2.simpleUi.M_TextInput;
import v2.simpleUi.ModifierInterface;
import v2.simpleUi.SimpleUI;
import v2.simpleUi.uiDecoration.ExampleDecorator;
import v2.simpleUi.util.ProgressScreen;
import v3.M_DateModifier;
import v3.M_FilePickerButton;
import v3.M_ImageView;
import v3.M_ItemBar;
import v3.M_MakePhoto;
import v3.M_RadioButtonListCreator2;
import v3.M_SpinnerWithCheckboxesCreator2;
import v3.M_SpinnerWithCheckboxesCreator2.DefaultSpinnerItem;
import v3.M_TextModifier;
import v3.maps.SimpleUIWithMaps;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.squareup.picasso.Picasso;

public class MainActivity extends Activity {

	protected static final String LOG_TAG = "Main";
	private static String text;
	private double doubleValue1;
	private double doubleValue2;
	private int intValue;

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

		c.add(new M_Button("Copy assets") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				try {
					File targetFolder = new File(Environment
							.getExternalStorageDirectory(), "TestAssets");
					targetFolder.mkdirs();
					IO.copyAssets(context, targetFolder);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		final File fileToShare = new File(
				Environment.getExternalStorageDirectory(), "img.jpg");
		if (fileToShare.exists()) {
			c.add(new M_Button("Share testimage img.jpg to Google+ ") {

				@Override
				public void onClick(Context context, Button arg1) {
					Intent i = IntentHelper.newSendImageIntent(fileToShare, "",
							"", "");
					IntentHelper.launchGooglePlus(MainActivity.this, i);
				}
			});
		}

		c.add(new M_Button("Simple Start Example") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				SimpleUI.showUi(context, new StartExampleUi());
			}
		});

		c.add(new M_Button("Meta Test Demo") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				SimpleUI.showInfoDialog(context, "Close", new MetaTestDemo());
			}
		});

		M_InfoText info = new M_InfoText(
				"Bitte auf http://www.google.de/ klicken.de!");
		info.setContainsUrls(true);
		c.add(info);

		c.add(new M_Button("Show " + ButterknifeAndDaggerTestActivity.class
				+ " activity") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				MainActivity.this.startActivity(new Intent(MainActivity.this,
						ButterknifeAndDaggerTestActivity.class));
			}
		});

		addImageView(c);

		addListWrapperTestUi(c);

		addCrashButtonForErrorHandlerTesting(c);

		addTestFileSelector(c);

		addTestPhotoModifier(c);

		mapsV2Tests(c);
		addInfoTextFormattingTests(c);
		addDateFormattingTests(c);
		addcheckboxtestingstuff(c);
		addcheckboxtestingstuff2(c);
		add5SecWaitButton(c);

		c.add(new M_Button("SimpleUi v3 testbed") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				SimpleUI.showInfoDialog(context, "Close", new V3Testbed());
			}
		});

		testItemBar(c);
		testSpinnerWithCheckboxes(c);
		testBasicModifiers(c);

		addTestContainer2Ui(c);

		addWebViewTest(c);

		c.add(new M_Button("Code example") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				exampleSimpleUiCode(context);
			}
		});

		c.add(new M_Button("V2 Example using the SimpleUI Activity") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				M_Container m = new M_Container();
				m.add(new M_Caption("Hello", 2));
				m.add(new M_EmailInput() {

					@Override
					public boolean save(String newText) {
						text = newText;
						return true;
					}

					@Override
					public String load() {
						return text;
					}

					@Override
					public String getVarName() {
						return "Email";
					}

				});
				m.add(new M_TextInput() {

					@Override
					public boolean save(String newText) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public String load() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getVarName() {
						// TODO Auto-generated method stub
						return null;
					}
				});
				m.add(new M_PlusMinus(R.drawable.btn_minus, R.drawable.btn_plus) {

					@Override
					public boolean save(String currentValue) {
						doubleValue1 = Double.parseDouble(currentValue);
						return true;
					}

					@Override
					public String plusEvent(ImageButton plusButton,
							String currentValue) {
						double d = Double.parseDouble(currentValue);
						d += 2.9423;
						return "" + d;
					}

					@Override
					public String minusEvent(ImageButton minusButton,
							String currentValue) {
						double d = Double.parseDouble(currentValue);
						d -= 1.9423;
						return "" + d;
					}

					@Override
					public String load() {
						return "" + doubleValue1;
					}

					@Override
					public String getVarName() {
						// TODO Auto-generated method stub
						return "DoubleValue1 ( original value was "
								+ doubleValue1 + ")";
					}
				});
				m.add(new M_Double() {

					@Override
					public boolean save(double newValue) {
						doubleValue2 = newValue;
						return true;
					}

					@Override
					public double load() {
						return doubleValue2;
					}

					@Override
					public String getVarName() {
						return "Double 2";
					}
				});
				m.add(new M_Integer() {

					@Override
					public boolean save(int newValue) {
						intValue = newValue;
						return true;
					}

					@Override
					public int load() {
						return intValue;
					}

					@Override
					public String getVarName() {
						// TODO Auto-generated method stub
						return "Test";
					}
				});

				m.assignNewDecorator(new ExampleDecorator());

				SimpleUI.showInfoDialog(MainActivity.this, "Save", m);
			}
		});
		c.add(new M_Button("V2 Example") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				MainActivity.this.startActivity(new Intent(MainActivity.this,
						V2ExampleUI.class));
			}
		});
		c.add(new M_Button("SimpleUi survey generated with simpleUi") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				MainActivity.this.startActivity(new Intent(MainActivity.this,
						ExampleSurveyActivity.class));
			}
		});

		c.add(new M_Button("Old SimpleUI V1 Example") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				final V1SimpleUiController i = new V1SimpleUiController() {

					@Override
					public void customizeScreen(List<ModifierInterface> group,
							Object message) {
						ModifierInterface m = new v1.Headline(
								"Infos about the old v1 version");
						group.add(m);
						group.add(new Headline(
								R.drawable.btn_dialog,
								"This is the old deprecated"
										+ " version of SimpleUI. "
										+ "The Modifiers of v1 are all compatible"
										+ " with the v2 version."));
						group.add(new Headline(R.drawable.btn_plus,
								"The theme concept was replaced by a more "
										+ "flexible visitor pattern "
										+ "like UIDecorater concept."));
						group.add(new Headline(R.drawable.btn_plus,
								"Most of the Modifiers were improved "
										+ "and start all with a M_... now to "
										+ "be easier to find."));

					}
				};
				V1SimpleUI.showInfoScreen(MainActivity.this, i, null);
			}
		});

		c.add(new M_Button("v2 Testbed") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				SimpleUI.showUi(MainActivity.this, new SimpleUiTestbed());
			}
		});

		c.add(new M_Button("M_RatinBarTests") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				SimpleUI.showInfoDialog(context, "Close",
						new M_RatingBarTests());

			}
		});

		c.add(new M_Button("JSON parser v1 example") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				MainActivity.this.startActivity(new Intent(MainActivity.this,
						JsonDemoMain.class));
			}
		});

		c.add(new M_Button("Close app") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				MainActivity.this.finish();
			}
		});

		setContentView(c.getView(this));

	}

	public void addImageView(M_Container c) {
		M_ImageView image = new M_ImageView();
		Picasso p = Picasso.with(this);
		p.setDebugging(true);
		File f = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Haus-Wiesenweg-Nacht_01.jpg");
		System.out.println("file " + f + " exists=" + f.exists());
		p.load(Uri.fromFile(f)).into(image);
		p.load(Uri.parse("http://wikipedia.de/img/logo.png")).into(image);

		// c.add(image);
		c.add(new M_LeftRight(image, 1, new M_InfoText("<-image"), 1));
	}

	private void throwExceptionForErrorHandlerTesting() {
		Object o = null;
		o.toString();
	}

	public void addListWrapperTestUi(M_Container c) {

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
				ListWrapperTests.generateEditUiForAssociationQuestionQuestion(
						c, answers, questions, numbers);
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
				SimpleUI.showCancelOkDialog(MainActivity.this, "Cancel", "Ok",
						c2);

			}
		});
	}

	private void addTestPhotoModifier(M_Container c) {
		c.add(new M_Button("Photo tests") {

			Uri uri = null;

			@Override
			public void onClick(Context context, Button clickedButton) {
				M_Container c2 = new M_Container();
				c2.add(new M_MakePhoto(uri) {

					@Override
					public boolean save(Activity activity, File takenBitmapFile) {
						uri = IO.toUri(takenBitmapFile);
						return true;
					}

					@Override
					public String getTextOnTakePhotoButton() {
						return "Make photo";
					}

					@Override
					public String getTextOnDeleteButton() {
						return "Delete photo";
					}

					@Override
					public boolean onDeleteRequest(Activity context) {
						uri = null;
						return true;
					}

					@Override
					public String getTextOnLoadFileButton() {
						return "From file";
					}

					@Override
					public String getModifierCaption() {
						return "Test photo box";
					}

					@Override
					public String getImageFileName() {
						return "/testImageCache/" + new Date().getTime()
								+ ".jpg";
					}
				});

				c2.add(new M_ImageView(uri));

				SimpleUI.showCancelOkDialog(MainActivity.this, "Cancel",
						"Save", c2);

			}
		});
	}

	private void addWebViewTest(M_Container c) {
		c.add(new M_Button("Web view Example") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				SimpleUI.showInfoDialog(context, "Ok",
						new WebViewTests(context));
			}
		});
	}

	private void addTestContainer2Ui(M_Container c) {
		M_Container2 c2 = new M_Container2("Section1", true);
		c2.add(new InfoText("Karl", "otfto"));
		c2.add(new InfoText("Ka7rl", "o9tto"));
		c2.add(new InfoText("Kar234l", "votto"));
		c2.add(new InfoText("Karvl", "otto234"));
		c2.add(new InfoText("K5arl", "ot234to"));
		c2.add(new InfoText("Ka23rl", "otyto"));
		c2.add(new InfoText("K6arl", "ott5o"));
		c.add(c2);
	}

	private void testItemBar(M_Container c) {
		M_ItemBar itemBar = new M_ItemBar();
		// itemBar.add(new M_InfoText("ABC"));
		// itemBar.add(new M_InfoText("DEF"));
		// itemBar.add(new M_InfoText("ABC"));
		// itemBar.add(new M_InfoText("DEF"));
		itemBar.add(2F, new M_InfoText("ABC"));
		itemBar.add(1F, new M_InfoText("DEF"));
		itemBar.add(1F, new M_InfoText("ABC"));
		itemBar.add(1F, new M_InfoText("DEF"));
		itemBar.add(1F, new M_InfoText("ABC"));
		itemBar.add(1F, new M_InfoText("DEF"));
		itemBar.add(1F, new M_InfoText("ABC"));
		// itemBar.add(1F, new M_InfoText("DEF"));
		// itemBar.add(1F, new M_InfoText("ABC"));
		// itemBar.add(1F, new M_InfoText("DEF"));
		// itemBar.add(1F, new M_InfoText("ABC"));
		// itemBar.add(1F, new M_InfoText("DEF"));
		c.add(itemBar);
	}

	private void testBasicModifiers(M_Container c) {
		c.add(new v3.M_IntModifier() {

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

		c.add(new v3.M_DoubleModifier() {

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

		c.add(new v3.M_LongModifier() {

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

		M_TextModifier m = new v3.M_TextModifier() {

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
				SimpleUIWithMaps.showUi(MainActivity.this,
						new GoogleMapV2TestContainer(),
						TestMapsV2Activity.class);
			}
		});
		c.add(new M_Button("Select pos on map v2") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				SimpleUIWithMaps.showUi(MainActivity.this,
						new GoogleMapV2PosOnMapTests(),
						TestMapsV2Activity.class);
			}
		});
	}

	protected void addcheckboxtestingstuff(M_Container c) {
		final ArrayList<v3.M_SpinnerWithCheckboxesCreator2.SpinnerItem> l = new ArrayList<M_SpinnerWithCheckboxesCreator2.SpinnerItem>();
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
					public List<v3.M_SpinnerWithCheckboxesCreator2.SpinnerItem> getItemList() {
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
				c.add(new M_RadioButtonListCreator2() {

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

	protected void exampleSimpleUiCode(Context context) {

		final M_Container box = new M_Container();
		box.add(new M_Caption("Hello World"));
		box.add(new M_InfoText(R.drawable.ic_dialog_info,
				"This is is an example UI with four elements to demonstrate"
						+ " how to SimpleUI concepts can be applied."));
		box.add(new M_Checkbox() {
			@Override
			public boolean save(boolean newValue) {
				storeResult(newValue);
				return true; // Modifier was saved correctly
			}

			@Override
			public boolean loadVar() {
				return false;
			}

			@Override
			public CharSequence getVarName() {
				return "I understand!";
			}
		});
		box.add(new M_Button("Save") {
			@Override
			public void onClick(Context context, Button clickedButton) {
				box.save();
			}
		});
		// To display the generated UI the SimpleUI class can be used:
		SimpleUI.showUi(context, box);

	}

	protected void storeResult(boolean newValue) {
		// TODO Auto-generated method stub

	}
}
