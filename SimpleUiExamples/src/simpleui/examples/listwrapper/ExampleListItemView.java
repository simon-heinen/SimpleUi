package simpleui.examples.listwrapper;

import java.util.List;

import simpleui.adapters.SimpleBaseAdapter;
import simpleui.adapters.SimpleBaseAdapter.HasItsOwnView;
import simpleui.util.ButterknifeHelper;
import simpleui.util.ColorUtils;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;

import com.googlecode.simpleui.R;

public class ExampleListItemView implements HasItsOwnView {

	private static final String LOG_TAG = ExampleListItemView.class.getSimpleName();
	@InjectView(R.id.swype_list_item_front_text)
	TextView t;
	@InjectView(R.id.swype_list_item_back_button)
	Button b;

	private final String name;

	public ExampleListItemView(String name) {
		this.name = name;
	}

	@Override
	public View getView(final Context context, View convertView,
			ViewGroup parent, SimpleBaseAdapter simpleBaseAdapter,
			List<? extends HasItsOwnView> containerList, int positionInList) {
		convertView = ButterknifeHelper.injectFieldsInListItem(context, this,
				convertView, R.layout.swipe_list_item);
		// set values for item:
		t.setText(name);
		t.setBackgroundColor(ColorUtils.randomColor());
		b.setText("Button for " + name);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Clicked on " + name, Toast.LENGTH_LONG)
						.show();
			}
		});
		return convertView;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void onItemClick(View itemView, int posInList) {
		String text = "Item clicked: " + t.getText();
		System.out.println(text);
		Toast.makeText(itemView.getContext(), text, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onItemLongClick(View itemView, int posInList) {
		String text = "Item clicked long: " + t.getText();
		System.out.println(text);
		Toast.makeText(itemView.getContext(), text, Toast.LENGTH_LONG).show();
		return true;
	}

}
