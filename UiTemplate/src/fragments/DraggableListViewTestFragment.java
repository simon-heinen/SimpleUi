/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fragments;

import java.util.ArrayList;
import java.util.List;

import tools.SimpleBaseAdapter;
import tools.SimpleBaseAdapter.HasItsOwnView;
import v2.simpleUi.M_InfoText;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.bitstars.uitemplate.R;

import draggableListView.DynamicListView;
import draggableListView.DynamicListView.ListModificationListener;

/**
 * This application creates a listview where the ordering of the data set can be
 * modified in response to user touch events.
 * 
 * An item in the listview is selected via a long press event and is then moved
 * around by tracking and following the movement of the user's finger. When the
 * item is released, it animates to its new position within the listview.
 */
public class DraggableListViewTestFragment extends Fragment {

	protected static final String LOG_TAG = "FragmentDraggableListViewTests";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try {
			View activity_list_view = inflater.inflate(
					R.layout.activity_list_view, container, false);

			ArrayList<HasItsOwnView> listToDisplay = new ArrayList<HasItsOwnView>();
			addItem(listToDisplay, "A1");
			addItem(listToDisplay, "B1");
			addItem(listToDisplay, "C1");
			addItem(listToDisplay, "A2");
			addItem(listToDisplay, "B2");
			addItem(listToDisplay, "C2");
			addItem(listToDisplay, "A3");
			addItem(listToDisplay, "B3");
			addItem(listToDisplay, "C3");
			addItem(listToDisplay, "A4");
			addItem(listToDisplay, "B4");
			addItem(listToDisplay, "C4");
			addItem(listToDisplay, "A5");
			addItem(listToDisplay, "B5");
			addItem(listToDisplay, "C5");
			addItem(listToDisplay, "A6");
			addItem(listToDisplay, "B6");
			addItem(listToDisplay, "C6");
			addItem(listToDisplay, "A7");
			addItem(listToDisplay, "B7");
			addItem(listToDisplay, "C7");
			addItem(listToDisplay, "A8");
			addItem(listToDisplay, "B8");
			addItem(listToDisplay, "C8");
			SimpleBaseAdapter adapter = new SimpleBaseAdapter(getActivity(),
					listToDisplay);
			DynamicListView listView = (DynamicListView) activity_list_view
					.findViewById(R.id.dynamic_listview);
			listView.setModelModificationListener(new ListModificationListener(
					listToDisplay));
			listView.setAdapter(adapter);
			listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
			return activity_list_view;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private void addItem(ArrayList<HasItsOwnView> listToDisplay,
			final String str) {

		listToDisplay.add(new HasItsOwnView() {

			@Override
			public boolean onItemLongClick(View itemView, int posInList) {
				Log.e(LOG_TAG, "onItemLongClick posInList=" + posInList);
				return false;
			}

			@Override
			public void onItemClick(View itemView, int posInList) {
				Log.e(LOG_TAG, "onItemClick posInList=" + posInList);
			}

			View v;

			@Override
			public View getView(Context context, View convertView,
					ViewGroup parent, SimpleBaseAdapter simpleBaseAdapter,
					List<? extends HasItsOwnView> containerList,
					int positionInList) {
				if (v == null) {
					v = new M_InfoText(android.R.drawable.ic_dialog_info, str)
							.getView(context);
				}
				return v;
			}
		});
	}

}
