package com.svenkapudija.imagewall;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.svenkapudija.imagewall.base.ImageWallActivity;

public class ImageActivity extends ImageWallActivity {

	private ImageButton map;
	private Button tag;
	
	@Override
	public void initUI() {
		map = (ImageButton) findViewById(R.id.imageButton_map);
		tag = (Button) findViewById(R.id.button_tag);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		map.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		tag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}

}
