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

import android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import draggableListView.Cheeses;
import draggableListView.DynamicListView;
import draggableListView.StableArrayAdapter;

/**
 * This application creates a listview where the ordering of the data set can be
 * modified in response to user touch events.
 * 
 * An item in the listview is selected via a long press event and is then moved
 * around by tracking and following the movement of the user's finger. When the
 * item is released, it animates to its new position within the listview.
 */
public class ListViewDraggingAnimation extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try {
			View activity_list_view = inflater.inflate(
					R.layout.activity_list_view, container, false);

			ArrayList<String> mCheeseList = new ArrayList<String>();
			for (int i = 0; i < Cheeses.sCheeseStrings.length; ++i) {
				mCheeseList.add(Cheeses.sCheeseStrings[i]);
			}

			StableArrayAdapter adapter = new StableArrayAdapter(getActivity(),
					R.layout.listitem_view, mCheeseList);
			DynamicListView listView = (DynamicListView) activity_list_view
					.findViewById(R.layout.listitem_view);
			listView.setCheeseList(mCheeseList);
			listView.setAdapter(adapter);
			listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
			return activity_list_view;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
