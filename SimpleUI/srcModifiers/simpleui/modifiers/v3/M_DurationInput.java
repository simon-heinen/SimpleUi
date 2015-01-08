package simpleui.modifiers.v3;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import simpleui.modifiers.ModifierInterface;
import simpleui.modifiers.v1.uiDecoration.UiDecoratable;
import simpleui.modifiers.v1.uiDecoration.UiDecorator;

public abstract class M_DurationInput implements ModifierInterface,
		UiDecoratable {
	
	private Integer durationInMinutes = 0;
	
	private DurationPickerDialog pickerDialog;
	private Button editButton;
	private float weightOfDescription = 1;
	private float weightOfInputText = 1;
	private UiDecorator myDecorator;
	
	public M_DurationInput() {
		durationInMinutes = loadDurationInMinutes();
		if(durationInMinutes == null){
			durationInMinutes = 0; 
		}
	}
	
	public Integer getDurationInMinues() {
		return durationInMinutes;
	}

	public void setDurationInMinues(Integer durationInMinues) {
		this.durationInMinutes = durationInMinues;
	}

	@Override
	public boolean assignNewDecorator(UiDecorator decorator) {
		myDecorator = decorator;
		return true;
	}

	@Override
	public View getView(Context context) {
		LinearLayout l = new LinearLayout(context);
		l.setGravity(Gravity.CENTER_VERTICAL);

		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
				weightOfDescription);
		LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
				weightOfInputText);

		TextView t = new TextView(context);
		t.setLayoutParams(p);
		t.setText(this.getVarName());
		
		l.addView(t);
		
		
		editButton = new Button(context);
		editButton.setLayoutParams(p2);
		editButton.setText(loadDurationAsString());		
		
		pickerDialog = new DurationPickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				int minutes = (hourOfDay * 60) + minute;
				setDurationInMinues(minutes);
				updateEditText();
			}
		} , getHours(), getMinutes());
		
		editButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				pickerDialog.show();				
			}
		});

		l.addView(editButton);
		l.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
				DEFAULT_PADDING);

		if (myDecorator != null) {
			int level = myDecorator.getCurrentLevel();
			myDecorator.decorate(context, t, level + 1,
					UiDecorator.TYPE_INFO_TEXT);
			myDecorator.decorate(context, editButton, level + 1,
					UiDecorator.TYPE_EDIT_TEXT);
		}

		return l;
	}

	public Button getEditButton() {
		return editButton;
	}
	
	private void updateEditText(){
		editButton.setText(loadDurationAsString());
	}
	
	@Override
	public boolean save() {
		if (saveDurationInMinutes(durationInMinutes)) {
			return true;
		}		
		getEditButton().requestFocus();
		return false;
	}
	
	private String loadDurationAsString(){
		int minutes = getMinutes();
		int hours = getHours();
		if(minutes == 0){
			return hours + ":00 h";
		}
		return hours + ":" + minutes + " h";		
	}
	
	private int getHours(){
		return durationInMinutes / 60;
	}
	
	private int getMinutes() {
		return durationInMinutes % 60;
	}
	
	public abstract Integer loadDurationInMinutes();
	
	public abstract boolean saveDurationInMinutes(int durationInMinutes);
	
	public abstract String getVarName();
	
	public class DurationPickerDialog extends TimePickerDialog {

	    public DurationPickerDialog(Context context, int theme,
	            OnTimeSetListener callBack, int hour, int minute) {
	        super(context, theme, callBack, hour, minute, true);
	        updateTitle(hour, minute);
	    }

	    public DurationPickerDialog(Context context, OnTimeSetListener callBack,
	            int hour, int minute) {
	        super(context, callBack, hour, minute, true);
	        updateTitle(hour, minute);
	    }

	    @Override
	    public void onTimeChanged(TimePicker view, int hour, int minute) {
	        super.onTimeChanged(view, hour, minute);
	        updateTitle(hour, minute);
	    }

	    public void updateTitle(int hour, int minute) {
	        setTitle("Dauer: " + hour + ":" + minute);
	    }

	}

}
