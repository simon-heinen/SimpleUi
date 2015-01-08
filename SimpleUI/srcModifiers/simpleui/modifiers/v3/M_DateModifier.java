package simpleui.modifiers.v3;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import simpleui.modifiers.ModifierInterface;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;

public abstract class M_DateModifier implements ModifierInterface,
		OnDateSetListener {

	private Date d;
	private Button b;

	@Override
	public View getView(Context context) {
		Date d = loadDate();
		b = new Button(context);
		if (d != null) {
			b.setText(getTextFor(d,
					new SimpleDateFormat("dd.MM.yyyy").format(d)));
		} else {
			b.setText("...");
		}
		if (d == null) {
			d = new Date();
		}
		final Calendar c = Calendar.getInstance();
		c.setTime(d);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		final DatePickerDialog p = new DatePickerDialog(context, this, year,
				month, day);

		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				p.show();
			}
		});
		return b;
	}

	public Date getDateFor(int day, int month, int year) {
		final Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day);
		return cal.getTime();
	}

	public abstract Date loadDate();

	@Override
	public boolean save() {
		if (d != null) {
			return save(d);
		}
		return true;
	}

	public abstract boolean save(Date newSelectedDate);

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		final Calendar c = Calendar.getInstance();
		c.set(year, monthOfYear, dayOfMonth);
		d = c.getTime();
		b.setText(getTextFor(d, new SimpleDateFormat("dd.MM.yyyy").format(d)));
	}

	public abstract String getTextFor(Date d, String dateSting);

}
