package simpleui.modifiers.v3;

import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.uiDecoration.UiDecoratable;
import simpleui.modifiers.v1.uiDecoration.UiDecorator;
import simpleui.util.ImageTransform;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

/**
 * use {@link M_HalfHalf} instead, has the same features
 */
@Deprecated
public class M_LeftRight implements ModifierInterface, UiDecoratable {

	private final ModifierInterface myLeft;
	private final ModifierInterface myRight;
	private UiDecorator myDecorator;
	private Integer minimumHeigthInDip;
	private boolean bothViewsSameHeigth;
	private float myLeftWeigth = 1;
	private float myRigthWeigth = 1;

	/**
	 * @param left
	 * @param leftWeigth
	 *            if the left view should be 3 times as big as the rigth one
	 *            pass 3 here
	 * @param right
	 * @param rigthWeigth
	 *            if the left view should be 3 times as big as the rigth one
	 *            pass 1 here
	 */
	public M_LeftRight(ModifierInterface left, int leftWeigth,
			ModifierInterface right, int rigthWeigth) {
		myLeft = left;
		myRight = right;
		myLeftWeigth = 1f / leftWeigth;
		myRigthWeigth = 1f / rigthWeigth;
	}

	public M_LeftRight(ModifierInterface left, int leftWeigth,
			ModifierInterface right, int rigthWeigth,
			int minimumLineHeigthInDIP, boolean bothViewsSameHeigth) {
		this(left, leftWeigth, right, rigthWeigth);
		this.minimumHeigthInDip = minimumLineHeigthInDIP;
		this.bothViewsSameHeigth = bothViewsSameHeigth;
	}

	@Override
	public View getView(Context context) {

		LinearLayout l = new LinearLayout(context);
		l.setPadding(l.getPaddingLeft(), l.getPaddingTop() + 10,
				l.getPaddingRight(), l.getPaddingBottom());
		l.setGravity(Gravity.CENTER);

		if (minimumHeigthInDip != null) {
			// params = new LinearLayout.LayoutParams(
			// LayoutParams.FILL_PARENT,
			// MeasureSpec.makeMeasureSpec(
			// (int) ImageTransform.dipToPixels(
			// context.getResources(), minimumHeigthInDip),
			// MeasureSpec.EXACTLY), 1);

			l.setMinimumHeight((int) ImageTransform.dipToPixels(
					context.getResources(), minimumHeigthInDip));
		}

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, l, level + 1,
					UiDecorator.TYPE_CONTAINER);
			myDecorator.setCurrentLevel(level + 1);
		}

		View left = myLeft.getView(context);
		View right = myRight.getView(context);

		l.addView(left);
		l.addView(right);

		if (bothViewsSameHeigth) {
			left.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					myLeftWeigth));
			right.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					myRigthWeigth));
			int h = (int) ImageTransform.dipToPixels(context.getResources(),
					minimumHeigthInDip);
			left.setMinimumHeight(h);
			right.setMinimumHeight(h);
		} else {
			// TODO or always use FILL_PARENT for height?

			left.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					myLeftWeigth));
			right.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					myRigthWeigth));
		}

		if (myDecorator != null) {
			myDecorator.setCurrentLevel(myDecorator.getCurrentLevel() - 1);
		}

		return l;
	}

	@Override
	public boolean save() {
		return myLeft.save() && myRight.save();
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		boolean l = false;
		boolean r = false;
		if (myLeft instanceof UiDecoratable) {
			l = ((UiDecoratable) myLeft).assignNewDecorator(decorator);
		}
		if (myRight instanceof UiDecoratable) {
			r = ((UiDecoratable) myRight).assignNewDecorator(decorator);
		}
		return l && r;
	}

}
