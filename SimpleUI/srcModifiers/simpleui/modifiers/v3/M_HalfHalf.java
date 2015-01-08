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

public class M_HalfHalf implements ModifierInterface, UiDecoratable {

	private ModifierInterface myLeft;
	private ModifierInterface myRight;
	private UiDecorator myDecorator;
	private Integer minimumHeigthInDip;
	private boolean bothViewsSameHeigth;

	private float weightOfLeft = 1;
	private float weightOfRight = 1;

	public static M_HalfHalf GoldenCutLeftLarge(ModifierInterface left,
			ModifierInterface right) {
		return new M_HalfHalf(left, right, 38, 61);
	}

	public static M_HalfHalf GoldenCutRightLarge(ModifierInterface left,
			ModifierInterface right) {
		return new M_HalfHalf(left, right, 61, 38);
	}

	public M_HalfHalf(ModifierInterface left, ModifierInterface right) {
		myLeft = left;
		myRight = right;
	}

	/**
	 * @param left
	 * @param right
	 * @param weightOfLeft
	 *            pass 2
	 * @param weightOfRight
	 */
	public M_HalfHalf(ModifierInterface left, ModifierInterface right,
			float weightOfLeft, float weightOfRight) {
		this(left, right);
		setWeightOfLeft(weightOfLeft);
		setWeightOfRight(weightOfRight);

	}

	public ModifierInterface getMyLeft() {
		return myLeft;
	}

	public ModifierInterface getMyRight() {
		return myRight;
	}

	public void setMyRight(ModifierInterface myRight) {
		this.myRight = myRight;
	}

	public void setMyLeft(ModifierInterface myLeft) {
		this.myLeft = myLeft;
	}

	public M_HalfHalf(ModifierInterface left, ModifierInterface right,
			int minimumLineHeigthInDIP, boolean bothViewsSameHeigth) {
		this(left, right);
		this.minimumHeigthInDip = minimumLineHeigthInDIP;
		this.bothViewsSameHeigth = bothViewsSameHeigth;
	}

	public void setWeightOfLeft(float weightOfLeft) {
		this.weightOfLeft = weightOfLeft;
	}

	public void setWeightOfRight(float weightOfRight) {
		this.weightOfRight = weightOfRight;
	}

	@Override
	public View getView(Context context) {

		int f = 2;
		LinearLayout l = new LinearLayout(context);
		l.setPadding(l.getPaddingLeft() + f, l.getPaddingTop() + f,
				l.getPaddingRight() + f, l.getPaddingBottom() + f);
		l.setGravity(Gravity.CENTER_VERTICAL);

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

		View left = null;
		if (myLeft != null) {
			left = myLeft.getView(context);
		} else {
			left = new LinearLayout(context);
		}
		View right = null;
		if (myRight != null) {
			right = myRight.getView(context);
		} else {
			right = new LinearLayout(context);
		}

		l.addView(left);
		l.addView(right);

		if (bothViewsSameHeigth) {
			LayoutParams lparams = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,
					weightOfLeft);
			LayoutParams rparams = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,
					weightOfRight);
			left.setLayoutParams(lparams);
			right.setLayoutParams(rparams);
			if (minimumHeigthInDip != null) {
				int h = (int) ImageTransform.dipToPixels(
						context.getResources(), minimumHeigthInDip);
				left.setMinimumHeight(h);
				right.setMinimumHeight(h);
			}
		} else {
			// TODO or always use FILL_PARENT for height?
			LayoutParams lparams = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					weightOfLeft);
			LayoutParams rparams = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					weightOfRight);
			left.setLayoutParams(lparams);
			right.setLayoutParams(rparams);
		}

		if (myDecorator != null) {
			myDecorator.setCurrentLevel(myDecorator.getCurrentLevel() - 1);
		}

		return l;
	}

	@Override
	public boolean save() {
		if (myLeft == null) {
			return myRight.save();
		}
		if (myRight == null) {
			return myLeft.save();
		}
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
