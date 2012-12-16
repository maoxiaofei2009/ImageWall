package com.svenkapudija.imagewall;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.svenkapudija.imagewall.api.ImageWallApi.BitmapListener;
import com.svenkapudija.imagewall.api.ImageWallApi.ImageSizeType;
import com.svenkapudija.imagewall.base.ImageWallActivity;
import com.svenkapudija.imagewall.models.Image;

public class ImageActivity extends ImageWallActivity {

	public static final String EXTRA_IMAGE = "image";
	
	private Bitmap imageBitmap;
	private Image image;
	
	private ImageButton imageContainer;
	private ImageButton map;
	private ImageView separator;
	private Button tag;
	
	private RelativeLayout footerLayout;
	private RelativeLayout tagLayout;
	private TextView description;
	
	@Override
	public void initUI() {
		map = (ImageButton) findViewById(R.id.imageButton_map);
		separator = (ImageView) findViewById(R.id.separator);
		tag = (Button) findViewById(R.id.button_tag);
		imageContainer = (ImageButton) findViewById(R.id.imageButton_image);
		description = (TextView) findViewById(R.id.textView_description);
		
		footerLayout = (RelativeLayout) findViewById(R.id.relativeLayout_footer);
		tagLayout = (RelativeLayout) findViewById(R.id.relativeLayout_tag);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		Bundle b = getIntent().getExtras();
		if(b != null) {
			image = b.getParcelable(EXTRA_IMAGE);
			
			initLocationButton();
			initDescriptionTextView();
			initTagButton();
			
			if(getFileUtils().existsImage(image.getFileName())) {
				initImageContainer(getFileUtils().getImage(image.getFileName()));
			} else {
				fetchBitmapFromNetwork();
			}
		}
	}

	private void initTagButton() {
		if(image.getTag() != null) {
			tagLayout.setVisibility(View.VISIBLE);
			tag.setText(image.getTag().getValue());
			tag.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(ImageActivity.this, MainActivity.class);
					i.putExtra(MainActivity.EXTRA_ACTION_SEARCH_TAG, image.getTag().getValue());
					startActivity(i);
				}
			});
		}
	}

	private void initDescriptionTextView() {
		if(image.getDescription() != null) {
			footerLayout.setVisibility(View.VISIBLE);
			description.setText(image.getDescription());
		}
	}

	private void initLocationButton() {
		if(image.getLocation() != null) {
			separator.setVisibility(View.VISIBLE);
			map.setVisibility(View.VISIBLE);
			
			map.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			});
		}
	}

	private void fetchBitmapFromNetwork() {
		final ProgressDialog dialog = ProgressDialog.show(this, null, "Loading image...");
		
		getApi().getBitmap(ImageSizeType.ORIGINAL, image.getFileName(), new BitmapListener() {
			
			@Override
			public void onSuccess(Bitmap bitmap) {
				try {
					getFileUtils().writeImage(bitmap, image.getFileName());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				initImageContainer(bitmap);
				
				if(dialog != null) {
					dialog.cancel();
				}
			}

			@Override
			public void onFailure() {
				if(dialog != null) {
					dialog.cancel();
				}
				
				
			}
		});
	}
	
	private void initImageContainer(final Bitmap bitmap) {
		imageBitmap = bitmap;
		
		imageContainer.setImageBitmap(bitmap);
		imageContainer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
 				Intent intent = new Intent();
 				intent.setAction(Intent.ACTION_VIEW);
 				intent.setDataAndType(Uri.parse("file://" + getFileUtils().getImagePath(image.getFileName())), "image/*");
 				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		imageBitmap.recycle();
	}

}
