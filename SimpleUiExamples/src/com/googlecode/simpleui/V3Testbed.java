package com.googlecode.simpleui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import v2.simpleUi.ActivityLifecycleListener;
import v2.simpleUi.M_Button;
import v2.simpleUi.M_Container;
import v3.M_MakePhoto;
import v3.M_RadioButtonListCreator;
import v3.M_TextModifier;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;

public class V3Testbed extends M_Container implements ActivityLifecycleListener {
	private M_MakePhoto photo;
	private File file;

	public V3Testbed() {

		photo = new M_MakePhoto(file) {

			@Override
			public boolean save(Activity activity, File takenBitmapFile) {
				file = takenBitmapFile;
				return false;
			}

			@Override
			public String getTextOnTakePhotoButton() {
				return "Make photo";
			}

			@Override
			public String getTextOnLoadFileButton() {
				return "load photo";
			}

			@Override
			public String getTextOnDeleteButton() {
				return "Delete photo";
			}

			@Override
			public boolean onDeleteRequest(Activity context) {
				file = null;
				return true;
			}

			@Override
			public String getImageFileName() {
				return "test.png";
			}

			@Override
			public String getModifierCaption() {
				return "Photo:";
			}
		};
		add(photo);

		add(new M_TextModifier() {

			@Override
			public boolean save(String newValue) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String load() {
				// TODO Auto-generated method stub
				return "abc";
			}

			@Override
			public String getVarName() {
				// TODO Auto-generated method stub
				return "Test";
			}
		});
		add(new M_TextModifier() {

			@Override
			public boolean save(String newValue) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public String load() {
				// TODO Auto-generated method stub
				return "abc";
			}

			@Override
			public String getVarName() {
				// TODO Auto-generated method stub
				return "Test";
			}
		});

		final M_RadioButtonListCreator responseOptionList = new M_RadioButtonListCreator() {
			int selectedItem = -1;
			List<String> answers = new ArrayList<String>();

			@Override
			public void setSelectedItemId(int newId) {
				selectedItem = newId;
			}

			@Override
			public void removeItem(int id) {
				answers.remove(id);

			}

			@Override
			public int getSelectedItemId() {
				return selectedItem;
			}

			@Override
			public List<SelectableItem> getItemList() {
				List<SelectableItem> list = new ArrayList<SelectableItem>();
				for (int i = 0; i < answers.size(); i++) {
					final int index = i;
					list.add(new SelectableItem() {

						@Override
						public void setText(String newText) {
							answers.set(index, newText);
						}

						@Override
						public void setId(int newId) {
							answers.set(newId, getText());
						}

						@Override
						public String getText() {
							return answers.get(index);
						}

						@Override
						public int getId() {
							return index;
						}
					});

				}
				return list;
			}

			@Override
			public void addNewItemToSelectableList(int id, String text) {
				answers.add(id, text);

			}
		};

		add(responseOptionList);

		add(new M_Button("new answer") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				responseOptionList.addNewEmptyItem(context);
				this.getView(context).invalidate();
			}
		});

	}

	@Override
	public void onActivityResult(Activity a, int requestCode, int resultCode,
			Intent data) {
		photo.onActivityResult(a, requestCode, resultCode, data);
	}

	@Override
	public void onStop(Activity activity) {
	}

	@Override
	public void onPause(Activity activity) {
	}

	@Override
	public void onResume(Activity activity) {
	}

	@Override
	public boolean onCloseWindowRequest(Activity activity) {
		return true;
	}
}
