package org.lp20.aikuma.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.lp20.aikuma.model.Language;
import org.lp20.aikuma.model.Recording;
import org.lp20.aikuma.model.Speaker;
import org.lp20.aikuma.R;
import org.lp20.aikuma.util.FileIO;

public class AddSpeakerActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_speaker);
		languages = FileIO.readDefaultLanguages();
	}

	@Override
	public void onResume() {
		super.onResume();

		ArrayAdapter<Language> adapter =
				new SpeakerLanguagesArrayAdapter(this, languages);
		setListAdapter(adapter);
	}

	public void onAddLanguageButton(View view) {
		Intent intent = new Intent(this, LanguageFilterList.class);
		startActivityForResult(intent, SELECT_LANGUAGE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent
			intent) {
		if (requestCode == SELECT_LANGUAGE) {
			if (resultCode == RESULT_OK) {
				languages.add((Language)
						intent.getParcelableExtra("language"));
			}
		}
	}

	public void onOkButtonPressed(View view) {
		UUID uuid = UUID.randomUUID();
		EditText textField = (EditText) findViewById(R.id.Name);
		String name = textField.getText().toString();
		Speaker newSpeaker = new Speaker(uuid, name, languages);
		Log.i("addspeaker", "newSpeaker: " + newSpeaker);
		try {
			newSpeaker.write();
		} catch (IOException e) {
			Toast.makeText(this, "Failed to write the Speaker to file",
					Toast.LENGTH_LONG).show();
		}
		this.finish();
	}

	static final int SELECT_LANGUAGE = 0;
	private List<Language> languages = new ArrayList<Language>();
}
