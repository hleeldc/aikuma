package au.edu.melbuni.boldapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class BoldActivity extends Activity {
	
	public void addToMenu(int layout) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout menu = (LinearLayout) findViewById(R.id.menu);
        menu.addView(layoutInflater.inflate(layout, menu, false));
	}
	public void setContent(int layout) {
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     	FrameLayout content = (FrameLayout) findViewById(R.id.content);
     	content.addView(layoutInflater.inflate(layout, content, false));
	}
	
	public void configureView() {
    	setContentView(R.layout.base);
    	
    	addToMenu(R.layout.user);
    	addToMenu(R.layout.help);
    	
    	// Menu behavior.
    	//
    	ImageButton userImageButton = (ImageButton) findViewById(R.id.userImageButton);
    	userImageButton.setOnClickListener(new View.OnClickListener() {
    		@Override
    		public void onClick(View view) {
    			startActivityForResult(new Intent(view.getContext(), UserSelectionActivity.class), 0);
    		}
    	});
	}
	
}
