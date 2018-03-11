package com.coursemis.view.activity;

import java.io.File;

import java.util.HashMap;
import java.util.Map;

import com.coursemis.R;
import com.coursemis.R.layout;
import com.coursemis.manage.WebService;
import com.coursemis.util.HttpUtil;
import com.coursemis.thread.FormFile;
import com.coursemis.thread.UploadThread;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageActivity extends Activity  implements OnClickListener {

	
	String imagePath=null;
	private Button selectImage, uploadImage, takeImage;
	private ImageView imageView;
	private String TAG = "ImageActivity";
	String request;
	private String picPath = null;
	Bitmap bitmap = null;
	
	private String cid=null;
	private String tid = null;
	Intent intent=null;
	private Handler handler  = new Handler()
	{
		
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case WebService.CON_STATE:
				Toast.makeText(ImageActivity.this, ""+msg.obj, Toast.LENGTH_SHORT).show();
				break;
			}
			
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.activity_image);
		 intent = getIntent();
		cid = intent.getStringExtra("cid");
		tid = intent.getStringExtra("tid");
		
		selectImage = (Button) this.findViewById(R.id.selectImage);
		uploadImage = (Button) this.findViewById(R.id.uploadImage);
		takeImage = (Button) this.findViewById(R.id.takeImage);
		
		imageView = (ImageView) this.findViewById(R.id.imageView);
		selectImage.setOnClickListener(this);
		uploadImage.setOnClickListener(this);
		takeImage.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.selectImage:
			Intent intent = new Intent();// intent????????????????????
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, 22);
			break;
		case R.id.takeImage:
			Intent intent2 = new Intent(getApplicationContext(),
					TakePhotoActivity.class);
			startActivityForResult(intent2, 23);
			// ???????????????????????????
			break;
		case R.id.uploadImage:
			if(imagePath!=null)
			{
			File file = new File(imagePath);
			uploadFile(file);
			 finish();
			}else
			{
				Toast.makeText(ImageActivity.this, "?????????��?????", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}// onclick????

	}// oncreate????

	// ??????????????????
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 22:
				Uri uri = data.getData();
				imagePath=uri.toString();
				Log.e("Upload???---L92--????????---------", "uri = " + uri);
				try {
					String[] pojo = { MediaStore.Images.Media.DATA };
					Cursor cursor = managedQuery(uri, pojo, null, null, null);
					if (cursor != null) {
						int colunm_index = cursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						String path = cursor.getString(colunm_index);
						if (path.endsWith("jpg") || path.endsWith("png")) {
							imagePath = path;
							ContentResolver cr = this.getContentResolver();
							Bitmap bitmap = BitmapFactory.decodeStream(cr
									.openInputStream(uri));
							imageView.setImageBitmap(bitmap);	
						} else {
							alert();
						}
					} else {
						alert();
					}
				} catch (Exception e) {
				}
				break;
			case 23:
				imageView.setImageBitmap(null);// ??????��?????null???,
												// ??????????????????????????????
				picPath = data.getStringExtra("photo_path");// ??SelectPicActivity.java?��?????��??
				imagePath=picPath;
				Log.i("uploadImage", "??????????��??=" + picPath);
				Bitmap bitmap = BitmapFactory.decodeFile(picPath);// ???????��???????��?
				imageView.setImageBitmap(bitmap);// ??��????????
				break;

			}// switch????
		}// if????
	}// onActivityResult????

	private void alert() {
		Dialog dialog = new AlertDialog.Builder(this).setTitle("???")
				.setMessage("???????????��????")
				.setPositiveButton("???", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						picPath = null;
					}
				}).create();
		dialog.show();
	}

	
	 public void uploadFile(File imageFile ) {
	        Log.i(TAG, "upload start");
	        try {
	            String requestUrl = HttpUtil.server_teacher_tupLoadHomework;
	            //??????????
	            Map<String, String> params = new HashMap<String, String>();
	            params.put("cid", cid);
	            params.put("tid", tid);
	            params.put("age", "23");
	            //??????,????????????struts2????????
	            FormFile formfile = new FormFile(imageFile.getName(), imageFile, "image", "application/octet-stream");
	            UploadThread uploadThread = new UploadThread(requestUrl,params,formfile);
	            
	            Thread t1 = new Thread(uploadThread);
	            t1.start();
	           
	            
	            Log.i(TAG, "upload success");
	            Toast.makeText(ImageActivity.this, "?????????...", Toast.LENGTH_LONG).show();
	           
	        } catch (Exception e) {
	            Log.i(TAG, "upload error");
	            e.printStackTrace();
	        }
	        Log.i(TAG, "upload end");
	    }
	
	
}
