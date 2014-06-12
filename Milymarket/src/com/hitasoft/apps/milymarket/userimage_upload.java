package com.hitasoft.apps.milymarket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hitasoft.apps.milymarket.util.ConstantValues;
import com.hitasoft.apps.milymarket.util.GetSet;
import com.hitasoft.apps.milymarket.util.JSONParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class userimage_upload extends Activity{
	private static final int PICK_IMAGE = 1;
	private static final int PICK_Camera_IMAGE = 2;
	RelativeLayout uploadimg;
	ImageView imgview;
	Button btnup, btncan;
	private Bitmap bitmap;
	ProgressDialog pgdialog;
	Uri imageUri;
	ImageButton back;
	MediaPlayer mp = new MediaPlayer();
	TextView addtext;
	private static String picturePath;
	HttpURLConnection conn = null;
	DataOutputStream dos = null;
	DataInputStream inStream = null;
	StringBuilder builder = new StringBuilder();

	// Is this the place are you doing something wrong.
	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";
	int bytesRead, bytesAvailable, bufferSize;
	byte[] buffer;
	int maxBufferSize = 1 * 1024 * 1024;
	String urlString = ConstantValues.uploadInfoPhoto;
	//String urlString = ConstantValues.userimage_upload;
	String uId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_upload);

		uploadimg = (RelativeLayout) findViewById(R.id.image_upload);
		imgview = (ImageView) findViewById(R.id.imgview1);
		addtext=(TextView) findViewById(R.id.image_txt);
		imgview.setScaleType(ScaleType.FIT_XY);
		btnup = (Button) findViewById(R.id.Upload1);
		btncan = (Button) findViewById(R.id.Cancel1);
		back = (ImageButton) findViewById(R.id.home);
		uId = ConstantValues.pref.getString("userprefid", "");

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FragmentChangeActivity.rssetg = true;
				FragmentChangeActivity.menumap = false;
				FragmentChangeActivity.filter_icon=false;
				invalidateOptionsMenu();
				startActivity(new Intent(userimage_upload.this, FragmentChangeActivity.class));
				
				//userimage_upload.this.finish();
			}
		});
		btnup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (bitmap == null) {
					Toast.makeText(getApplicationContext(),
							"Please select image", Toast.LENGTH_SHORT).show();
				} else {
					new ImageUploadTask().execute();
				}
			}

		});
		btncan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userimage_upload.this.finish();

			}

		});
		uploadimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addtext.setVisibility(View.INVISIBLE);
				AlertDialog.Builder dialog = new AlertDialog.Builder(
					userimage_upload.this);
				dialog.setTitle("Options");
				dialog.setCancelable(true);
				dialog.setMessage("Select your option");
				dialog.setPositiveButton("Camera",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								Toast.makeText(getApplicationContext(),
										"Camera", Toast.LENGTH_SHORT).show();
								String fileName = "new-photo-name.jpg";
								ContentValues values = new ContentValues();
								values.put(MediaStore.Images.Media.TITLE,
										fileName);
								values.put(MediaStore.Images.Media.DESCRIPTION,
										"Image capture by camera");
								imageUri = getContentResolver()
										.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
												values);
								Intent intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										imageUri);
								intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,
										1);
								startActivityForResult(intent,
										PICK_Camera_IMAGE);
							}
						});
				dialog.setNegativeButton("Browse",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									selectImageFromGallery();
								} catch (Exception e) {
									Toast.makeText(getApplicationContext(),
											e.getMessage(), Toast.LENGTH_LONG)
											.show();
									Log.e(e.getClass().getName(),
											e.getMessage(), e);
								}

								Toast.makeText(getApplicationContext(),
										"browse", Toast.LENGTH_SHORT).show();

							}
						});

				dialog.create();
				dialog.show();
			}
		});

	}

	public void selectImageFromGallery() {
		Intent gintent = new Intent();
		gintent.setType("image/*");
		gintent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(gintent, "Select Picture"),
				PICK_IMAGE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			picturePath = cursor.getString(columnIndex);
			cursor.close();
			decodeFile(picturePath);

		}
	}

	private void decodeFile(String filepath) {

		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filepath, o);
		final int RequiredSize = 1024;
		int width_tmp = o.outWidth;
		int height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp < RequiredSize && height_tmp < RequiredSize)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;

		}

		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		bitmap = BitmapFactory.decodeFile(filepath, o2);
		imgview.setImageBitmap(bitmap);
	}

	class ImageUploadTask extends AsyncTask<Void, Void, String> {

		ProgressDialog pgdialog = new ProgressDialog(userimage_upload.this);
		JSONObject jsonobject = null;
		String Json = "";
		String picname;
		String result;
		String currentstatus;
		JSONParser jParser = new JSONParser();
		JSONObject response;

		@SuppressWarnings("deprecation")
		protected String doInBackground(Void... unsued) {
			try {
				String exsistingFileName = picturePath;
				String userid=GetSet.getUserId();
				Log.v(" exsistingFileName", exsistingFileName);
				FileInputStream fileInputStream = new FileInputStream(new File(
						exsistingFileName));
				URL url = new URL(urlString+"?userId="+userid+"&userImg="+exsistingFileName);
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				// Allow Outputs
				conn.setDoOutput(true);
				// Don't use a cached copy.
				conn.setUseCaches(false);
				// Use a post method.
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				dos = new DataOutputStream(conn.getOutputStream());
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"images\";filename=\""
						+ exsistingFileName + "\"" + lineEnd);
				dos.writeBytes(lineEnd);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				while (bytesRead > 0) {
					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null)
					builder.append(inputLine);
				Log.v("builder",""+builder);
				fileInputStream.close();
				Json = builder.toString();
				Log.v("json",""+Json);
				dos.flush();
				dos.close();

			} catch (MalformedURLException ex) {
				Log.e("MediaPlayer", "error: " + ex.getMessage(), ex);
			} catch (IOException ioe) {
				Log.e("MediaPlayer", "error: " + ioe.getMessage(), ioe);
			}

			try {
				if (pgdialog.isShowing())
					pgdialog.dismiss();
				jsonobject = new JSONObject(Json);
				JSONObject image = jsonobject.getJSONObject("Image");
				JSONArray list = image.getJSONArray("list");
				for (int i = 0; i < list.length(); i++) {
					JSONObject values = list.getJSONObject(i);
					String msg = values.getString("Message");
					picname = values.getString("Name");
					Log.v("msg",""+ msg+ picname);
				}

				//String url = ConstantValues.add_photo;
				String url=ConstantValues.userimage_upload;
				//String userId = GetSet.getUserId();
				
				//String urlAddr = url+"?userId="+userId+"&userImg=";
				
				HttpClient httpclient = new DefaultHttpClient();
              //  httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

                HttpPost httppost = new HttpPost(url);
                try{
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("userId", GetSet
    					.getUserId()));
                nameValuePairs.add(new BasicNameValuePair("userImg", picname));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
    			if (status == 200) {
    				result = EntityUtils.toString(response.getEntity());
    				currentstatus=result.toString();
    				Log.v("response", result.toString());
    			}
                }
                catch (ClientProtocolException e) {
        		} catch (IOException e) {
        		} catch (Exception e) {

        		}
                
               // File file = new File(picname);

               // MultipartEntity mpEntity = new MultipartEntity();
               // ContentBody cbFile = new FileBody(file, "image/jpeg");
                //mpEntity.addPart("userfile", cbFile);
       
               // httppost.setEntity(mpEntity);
               // Log.v("executing request ","" + httppost.getRequestLine());
                //HttpResponse response = httpclient.execute(httppost);
              /*  HttpEntity resEntity = response.getEntity();
                Log.v("response",""+response.getStatusLine());
                if (resEntity != null) {
                          Log.v("ent",""+EntityUtils.toString(resEntity));
                          currentstatus=EntityUtils.toString(resEntity);
                }
                if (resEntity != null) {
                          resEntity.consumeContent();
                }
                httpclient.getConnectionManager().shutdown();
			*/	
				//response = jParser.getJSONFromUrl(urlAddr);
				//currentstatus = response.getString(ConstantValues.status);
				//Log.v("rsponse",""+response.getString(ConstantValues.status));
			} catch (Exception e) {

				Log.v(e.getClass().getName(), e.getMessage(), e);
			}

			try {
				inStream = new DataInputStream(conn.getInputStream());
				String str;
				while ((str = inStream.readLine()) != null) {
					Log.e("MediaPlayer", "Server Response" + str);
				}
				inStream.close();
			} catch (IOException ioex) {
				Log.e("MediaPlayer", "error: " + ioex.getMessage(), ioex);
			}

			return null;

		}

		@Override
		protected void onPreExecute() {
			pgdialog.setMessage("Uploading...");
			pgdialog.setCancelable(false);
			pgdialog.setCanceledOnTouchOutside(false);
			pgdialog.setIndeterminate(true);
			pgdialog.show();

		}

		protected void onPostExecute(String sResponse) {
			try {
				if (pgdialog.isShowing())
					pgdialog.dismiss();
			
					
					Toast.makeText(getApplicationContext(), "uploaded succesfully",
							Toast.LENGTH_SHORT).show();
			
					
			} catch (Exception e) {
				Log.v("error", "in post" + e.getMessage());
			}
		}

	}

}
