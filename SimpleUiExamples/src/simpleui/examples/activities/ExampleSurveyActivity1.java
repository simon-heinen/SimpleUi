package simpleui.examples.activities;

import java.util.ArrayList;

import simpleui.customViews.SimpleRatingBar;
import simpleui.customViews.SimpleRatingBar.RatingItem;
import simpleui.modifiers.v3.M_Button;
import simpleui.modifiers.v3.M_Caption;
import simpleui.modifiers.v3.M_EmailInput;
import simpleui.modifiers.v3.M_InfoText;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.googlecode.simpleui.R;

public class ExampleSurveyActivity1 extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ScrollView s = new ScrollView(this);
		LinearLayout l = new LinearLayout(this);
		l.setOrientation(LinearLayout.VERTICAL);
		l.addView(new M_Caption("Rate Simple UI").getView(this));

		l.addView(new M_InfoText("Some questions about Simple UI. This "
				+ "interface itself was generated using "
				+ "SimpleUI v2. Take a look at the " + "code yourself.")
				.getView(this));

		l.addView(new M_InfoText("If you want to add some additional messages "
				+ "click send mail when you are finished "
				+ "and put some text at the bottom of the mail").getView(this));

		final ArrayList<RatingItem> questions = createQuestions();

		l.addView(new SimpleRatingBar(this, R.drawable.undo, R.drawable.trash,
				R.drawable.bad, R.drawable.good, questions));

		final M_EmailInput email = new M_EmailInput() {

			private String getRatingSummary() {
				String s = "";
				for (RatingItem i : questions) {

					if (i.isCleared()) {
						s += i.getName() + " has no rating (previous "
								+ i.getRatingInPercent() + ")\n";
					} else if (i.isSpam()) {
						s += i.getName() + " is marked as spam (previous "
								+ i.getRatingInPercent() + ")\n";
					} else {
						s += i.getName() + " has a rating of "
								+ i.getRatingInPercent() + "\n";
					}
				}
				return s;
			}

			@Override
			public boolean save(String validEmailAddress) {
				sendMail(ExampleSurveyActivity1.this, validEmailAddress,
						"Some feedback for SimpleUI", getRatingSummary());
				return true;
			}

			@Override
			public String load() {
				return "simon.heinen@gmail.com";
			}

			@Override
			public String getVarName() {
				return "Mail";
			}
		};
		l.addView(email.getView(this));
		l.addView(new M_Button("Send ratings & Close") {

			@Override
			public void onClick(Context context, Button clickedButton) {
				if (email.save()) {
					ExampleSurveyActivity1.this.finish();
				}
			}
		}.getView(this));

		s.addView(l);
		setContentView(s);
	}

	private ArrayList<RatingItem> createQuestions() {

		ArrayList<RatingItem> n = new ArrayList<RatingItem>();
		q(n, "I instantly understood the rating bar view");
		q(n, "I could read the SimpleUI example code");
		q(n, "I find it easy to write SimpleUI code");
		q(n, "I like the idea of simpleUi");
		q(n, "I think there is something missing in simpleUI");
		q(n, "I think this are good questions");
		q(n, "I think it is easy to answer questions this way");
		q(n, "I get the idea of the back button");
		q(n, "I get the idea of the trash button");
		q(n, "I like icecreme");
		q(n, "These are way to many questions");
		q(n, "The last question made it even worse");
		return n;
	}

	private void q(ArrayList<RatingItem> n, String question) {
		n.add(SimpleRatingBar.newDefaultRatingItem(question));
	}
}
