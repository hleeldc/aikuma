/*
	Copyright (C) 2013, The Aikuma Project
	AUTHORS: Oliver Sangyeop Lee
*/
package org.lp20.aikuma.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.lp20.aikuma.model.Language;
import org.lp20.aikuma.model.Recording;
import org.lp20.aikuma.model.Speaker;
import org.lp20.aikuma.R;
import org.lp20.aikuma.util.FileIO;
import org.lp20.aikuma.util.ImageUtils;

/**
 * @author	Oliver Adams	<oliver.adams@gmail.com>
 * @author	Florian Hanke	<florian.hanke@gmail.com>
 * @author	Sangyeop Lee	<sangl1@student.unimelb.edu.au>
 */
public class AddSpeakerActivity3 extends AikumaActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_speaker3);
		
		Intent intent = getIntent();
		name = (String) intent.getExtras().getString("name");
		selectedLanguages = intent.getParcelableArrayListExtra("languages");
		
		TextView nameView = (TextView) findViewById(R.id.nameView2);
		nameView.setText("Name: " + name);
		TextView languageView = (TextView) findViewById(R.id.languageView1);
		StringBuilder sb = new StringBuilder("Languages:\n");
		for(Language lang : selectedLanguages) {
			sb.append(lang + "\n");
		}
		languageView.setText(sb);
		
		//Lets method in superclass(AikumaAcitivity) know 
		//to ask user if they are willing to
		//discard new data on an activity transition via the menu.
		safeActivityTransition = false;
		safeActivityTransitionMessage = 
				"This will discard the new speaker's photo.";

		imageUUID = UUID.randomUUID();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent
			result) {
		if (resultCode == RESULT_OK) {
			// Move image-file to no-sync-path
			Uri imageUri = result.getData();
			try {
				ImageUtils.moveImageFileFromUri(this, 
						imageUri, this.imageUUID);
			} catch (IOException e) {
				Toast.makeText(this, 
						"Failed to write the image to file",
						Toast.LENGTH_LONG).show();
			}
			getContentResolver().delete(result.getData(), null, null);
			
			Intent lastIntent = new Intent(this, AddSpeakerActivity4.class);
			lastIntent.putExtra("name", name);
			lastIntent.putParcelableArrayListExtra("languages", selectedLanguages);
			lastIntent.putExtra("imageUUID", imageUUID.toString());
			startActivity(lastIntent);
		}
	}

	/**
	 * When the take photo button is pressed.
	 *
	 * @param	view	The take photo button.
	 */
	public void takePhoto(View view) {
		dispatchTakePictureIntent(PHOTO_REQUEST_CODE);
	}

	private void dispatchTakePictureIntent(int actionCode) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		startActivityForResult(takePictureIntent, actionCode);
	}

	static final int PHOTO_REQUEST_CODE = 1;
	
	private String name;
	private ArrayList<Language> selectedLanguages;

	private UUID imageUUID;
}