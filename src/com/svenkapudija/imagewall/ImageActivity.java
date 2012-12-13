package com.svenkapudija.imagewall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		ImageButton newImage = (ImageButton) findViewById(R.id.imageButton_newImage);
		newImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ImageActivity.this, NewImageActivity.class);
				startActivity(i);
			}
		});
		
		
		
	}

}
