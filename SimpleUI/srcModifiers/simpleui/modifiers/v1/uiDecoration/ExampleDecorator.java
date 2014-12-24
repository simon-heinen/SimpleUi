package simpleui.modifiers.v1.uiDecoration;

import simpleui.util.ColorUtils;
import simpleui.util.TextUtils;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * An example how to implement a {@link UiDecorator}. To simplify things the
 * {@link DefaultUiDecorator} class is used
 * 
 * @author Simon Heinen
 * 
 */
public class ExampleDecorator extends DefaultUiDecorator {

	private TextUtils myWhiteDeco;
	private TextUtils myBlackDeco;
	private ColorUtils myBackgroundBox;

	public ExampleDecorator() {
		super(true);
	}

	@Override
	public boolean decorate(Context context, View targetView, int level,
			int type) {
		if (type == UiDecorator.TYPE_CONTAINER && level > 2) {
			getBackgroundBox().applyBackgroundTo(targetView);
		}
		return true;
	}

	private ColorUtils getBackgroundBox() {
		if (myBackgroundBox == null)
			myBackgroundBox = ColorUtils.newGreenBackground();
		return myBackgroundBox;
	}

	private TextUtils getBackShadowTextUtil(Context context) {
		if (myBlackDeco == null)
			myBlackDeco = TextUtils.TextWithwhiteShadow(context);
		return myBlackDeco;
	}

	private TextUtils getWhiteShadowTextUtil(Context context) {
		if (myWhiteDeco == null)
			myWhiteDeco = TextUtils.TextWithBlackShadow(context);
		return myWhiteDeco;
	}

	@Override
	public boolean decorate(Context context, TextView targetView, int level,
			int type) {
		getBackShadowTextUtil(context).applyTo(targetView);
		return true;
	}

	@Override
	public boolean decorate(Context context, EditText targetView, int level,
			int type) {
		getWhiteShadowTextUtil(context).applyTo(targetView);
		return true;
	}

	@Override
	public boolean decorate(Context context, Button targetView, int level,
			int type) {
		getWhiteShadowTextUtil(context).applyTo(targetView);
		return true;
	}

}
