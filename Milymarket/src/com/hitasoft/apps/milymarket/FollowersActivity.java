package com.hitasoft.apps.milymarket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.hitasoft.apps.adapters.RoundedCornerBitmap;
import com.hitasoft.apps.milymarket.R;
import com.hitasoft.apps.milymarket.util.ConstantValues;
import com.hitasoft.apps.milymarket.util.GetSet;
import com.hitasoft.apps.milymarket.util.JSONParser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class FollowersActivity extends Activity implements OnItemClickListener,OnClickListener {

	String userid = null;
	ListView lv;
	ArrayList<HashMap<String, String>> followersdatas = new ArrayList<HashMap<String, String>>();
	ImageLoader profileLoader;
	TextView userName, type, count;
	private StaggeredGridView gridView = null;
	public static Context context;
	ArrayList<HashMap<String, String>> followingdatas = new ArrayList<HashMap<String, String>>();
	AdapterForHdpis hdpiAdapterprofile;
	TextView username, fullname;
	ImageView userimage;
	Button statususer;
	private ImageButton home, near, shop, alert, menu;
	AlertDialog adialog;
	HashMap<String, String> tempMap;
	String uId;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.followers_following_common_page);
		
		home = (ImageButton) findViewById(R.id.btn_home);
		near = (ImageButton) findViewById(R.id.btn_near);
		shop = (ImageButton) findViewById(R.id.btn_shop);
		alert = (ImageButton) findViewById(R.id.btn_alert);
		menu = (ImageButton) findViewById(R.id.btn_menu);
		

		home.setOnClickListener(this);
		near.setOnClickListener(this);
		shop.setOnClickListener(this);
		alert.setOnClickListener(this);
		menu.setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.userNameTitle);
		title.setText("Aboonements");
		profileLoader = ImageLoader.getInstance();
		gridView = (StaggeredGridView) findViewById(R.id.gridView1);
		ImageButton back = (ImageButton) findViewById(R.id.smenu);
		adialog=new AlertDialog.Builder(FollowersActivity.this).create();
		adialog.setTitle("Alert");
		adialog.setMessage("S'il vous plaît connecter Pour continuer!!!");
		adialog.setButton("OK",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.v("alert","alert");
				
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FragmentChangeActivity.rsprofile = true;
				FragmentChangeActivity.menumap = true;
				FragmentChangeActivity.filter_icon=false;
				invalidateOptionsMenu();
				startActivity(new Intent(FollowersActivity.this, FragmentChangeActivity.class));
				//FollowersActivity.this.finish();
			}
		});
		try {
		    uId = ConstantValues.pref.getString("userprefid", "");
			new GetFollowerDetail().execute(Integer.parseInt(uId));

		} catch (Exception e) {
			Log.v("bug", e.toString());
		}
	}

	private void setAdapter() {
		try {
			hdpiAdapterprofile = new AdapterForHdpis(getApplicationContext());
			// setColumns();
			gridView.setAdapter(hdpiAdapterprofile);
		} catch (Exception e) {

		}
	}

	class GetFollowerDetail extends AsyncTask<Integer, Void, Void> {
		String url = ConstantValues.follwersUrl;
		ProgressDialog dialog = new ProgressDialog(FollowersActivity.this);

		@Override
		protected Void doInBackground(Integer... params) {
			int UserId = params[0];
			JSONParser parser = new JSONParser();
			JSONObject result = parser.getJSONFromUrl(url + UserId);
			try {
				String status = result.getString(ConstantValues.status);
				if (status.equalsIgnoreCase("true")) {
					JSONObject followers = result.getJSONObject("result");
					JSONArray followersArray = followers
							.getJSONArray("followers");
					for (int i = 0; i < followersArray.length(); i++) {
						HashMap<String, String> map = new HashMap<String, String>();
						JSONObject value = followersArray.getJSONObject(i);
						String userid = value.getString("userId");
						String userName = value.getString("userName");
						String fullname = value
								.getString(ConstantValues.TAG_FULLNAME);
						String profileImage = value.getString("profileImage");
						String userstatus = value
								.getString(ConstantValues.TAG_STATUS);
						map.put("userId", userid);
						map.put(ConstantValues.TAG_USERNAME, userName);
						map.put(ConstantValues.TAG_FULLNAME, fullname);
						map.put("profileImage", profileImage);
						map.put(ConstantValues.TAG_STATUS, userstatus);

						followingdatas.add(map);
					}
				} else {

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Chargement...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			setAdapter();
		}

	}

	public class AdapterForHdpis extends BaseAdapter {
		private Context mContext;

		public AdapterForHdpis(Context ctx) {
			this.mContext = ctx;
		}

		@Override
		public int getCount() {
			return followingdatas.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view;

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.followinginflate, parent,
						false);

			} else {
				view = convertView;
				view.forceLayout();
			}

			 tempMap = new HashMap<String, String>();

			tempMap = followingdatas.get(position);

			final ImageView image = (ImageView) view
					.findViewById(R.id.followingimage);

			final TextView username = (TextView) view
					.findViewById(R.id.username);

			final TextView fullname = (TextView) view
					.findViewById(R.id.fullname);
			final Button userstatus = (Button) view.findViewById(R.id.button1);
			final ProgressBar loader = (ProgressBar) view
					.findViewById(R.id.progressBar1);
			final String followuserid;
			image.setTag(position);

			username.setText(tempMap.get("username"));
			String ustatus=tempMap.get(ConstantValues.TAG_STATUS);
			if(ustatus.equals("follow")){
				userstatus.setText("S'abonner");
			}
			else{
				userstatus.setText("Se désabonner");
			}
			
			if (userstatus.getText().toString().equals("S'abonner")) {
				userstatus.setBackgroundColor(android.graphics.Color.RED);
			} else {
				userstatus.setBackgroundColor(android.graphics.Color.GRAY);
			}
			followuserid = tempMap.get("userId");
			if (followuserid.equals(GetSet.getUserId())) {
				userstatus.setVisibility(View.GONE);
			}
			fullname.setText(tempMap.get(ConstantValues.TAG_FULLNAME));
			userstatus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if(GetSet.isLogged()==true){
					Log.v("status clicked", "");
					if(GetSet.getUserId()==uId){
					if (userstatus.getText().equals("S'abonner")) {
						follow(followuserid);
						userstatus.setBackgroundColor(android.graphics.Color.GRAY);
						userstatus.setText("Se désabonner");
					} else if (userstatus.getText().equals("Se désabonner")) {
						Log.v("if u want unfollow click here", "slksjflksajfld");
						unfollow(followuserid);
						userstatus.setBackgroundColor(android.graphics.Color.RED);
						userstatus.setText("S'abonner");
					}
					}
					else{
						
					}
					}
					else{
						adialog.show();
					}

				}

			});
			image.setOnClickListener(new OnClickListener()
			{

				@SuppressLint("NewApi")
				@Override
				public void onClick(View v) {
					ConstantValues.editor.clear();
					ConstantValues.editor.putString("userprefid",followingdatas.get(position).get("userId"));
					ConstantValues.editor.commit();
					FragmentChangeActivity.rsprofile = true;
					FragmentChangeActivity.menumap = true;
					FragmentChangeActivity.filter_icon=false;
					invalidateOptionsMenu();
					startActivity(new Intent(FollowersActivity.this, FragmentChangeActivity.class));
			
					
				}
				
			});

			profileLoader.loadImage(tempMap.get("profileImage"),
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {

						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							loader.setVisibility(View.VISIBLE);

						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							//Bitmap rdbitmap = RoundedCornerBitmap
							//		.getRoundedCornerBitmap(loadedImage, 10);
							image.setImageBitmap(loadedImage);

							HashMap<String, String> map = followingdatas
									.get(Integer.parseInt(image.getTag()
											.toString()));

							loader.setVisibility(View.INVISIBLE);

						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {

						}
					});

			return view;
		}
	}

	public void follow(String followid) {
		new FollowResults().execute(followid);
	}

	public void unfollow(String followid) {
		new UnFollowResults().execute(followid);
	}

	class FollowResults extends AsyncTask<String, Void, String> {

		private ProgressDialog dialog = new ProgressDialog(
				FollowersActivity.this);

		@Override
		protected String doInBackground(String... param) {
			String result = postData(param[0]);
			return result;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.dialog.setMessage("S'il vous plaît attendre...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			this.dialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			try {
				JSONObject jonj = new JSONObject(result);
				if (jonj.getString(ConstantValues.status).equalsIgnoreCase(
						"true")) {
					Log.v("response", jonj.toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	public String postData(String followid) {
		String urlAddr = ConstantValues.Follows;
		if (GetSet.getUserId() != null) {
			urlAddr = urlAddr + "?userId=" + GetSet.getUserId() + "&followId="
					+ followid;
			Log.v("userid", "" + GetSet.getUserId());
			Log.v("followid", "" + followid);
			Log.v("in", urlAddr.trim());
		}
		String result = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(urlAddr.trim());
		try {

			HttpResponse response = httpclient.execute(httppost);
			int status = response.getStatusLine().getStatusCode();
			if (status == 200) {
				result = EntityUtils.toString(response.getEntity());
				Log.v("response", result.toString());
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {

		}
		return result;
	}

	class UnFollowResults extends AsyncTask<String, Void, String> {

		private ProgressDialog dialog = new ProgressDialog(
				FollowersActivity.this);

		@Override
		protected String doInBackground(String... param) {
			String result = postUnFollowData(param[0]);
			return result;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.dialog.setMessage("S'il vous plaît attendre...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			this.dialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			try {
				JSONObject jonj = new JSONObject(result);
				if (jonj.getString(ConstantValues.status).equalsIgnoreCase(
						"true")) {
					Log.v("response", jonj.toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	public String postUnFollowData(String followid) {
		String urlAddr = ConstantValues.Unfollows;
		if (GetSet.getUserId() != null) {
			urlAddr = urlAddr + "?userId=" + GetSet.getUserId() + "&followId="
					+ followid;
			Log.v("userid", "" + GetSet.getUserId());
			Log.v("followid", "" + followid);
			Log.v("in", urlAddr.trim());
		}
		String result = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(urlAddr.trim());
		try {

			HttpResponse response = httpclient.execute(httppost);
			int status = response.getStatusLine().getStatusCode();
			if (status == 200) {
				result = EntityUtils.toString(response.getEntity());
				Log.v("response", result.toString());
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {

		}
		return result;
	}
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_home:
			FragmentChangeActivity.rshome = true;
			startActivity(new Intent(this, FragmentChangeActivity.class));
			break;
		case R.id.btn_near:
			FragmentChangeActivity.rsnear = true;
			startActivity(new Intent(this, FragmentChangeActivity.class));
			break;
		case R.id.btn_shop:
			FragmentChangeActivity.rsshop = true;
			startActivity(new Intent(this, FragmentChangeActivity.class));
			break;
		case R.id.btn_alert:
			if(GetSet.isLogged()==true){
			FragmentChangeActivity.rsnote = true;
			startActivity(new Intent(this, FragmentChangeActivity.class));
			}
			else{
				Intent i=new Intent(FollowersActivity.this,LoginActivity.class);
				startActivity(i);
			}
			break;
		case R.id.btn_menu:
			if(GetSet.isLogged()==true){
			FragmentChangeActivity.rsmenu = true;
			FragmentChangeActivity.menumap = true;
			FragmentChangeActivity.filter_icon=false;
			invalidateOptionsMenu();
			startActivity(new Intent(this, FragmentChangeActivity.class));
			}
			else{
				Intent i=new Intent(FollowersActivity.this,LoginActivity.class);
				startActivity(i);
			}
			break;

		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	/*
	 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
	 * arg2, long arg3) { HashMap<String, String> tempMap =
	 * followersdatas.get(arg2); //String userId =
	 * tempMap.get(ConstantValues.userid); try { adapter.clear();
	 * adapter.notifyDataSetChanged(); Intent i = new
	 * Intent(FollowersActivity.this, ProfileTabHolder.class); //
	 * i.putExtra("userId", userId); FollowersActivity.this.finish();
	 * startActivity(i); } catch (Exception e) { }
	 * 
	 * }
	 */

	/*
	 * class CustomListViewAdapter extends ArrayAdapter<HashMap<String, String>>
	 * {
	 * 
	 * Context context;
	 * 
	 * public CustomListViewAdapter(Context context, int resourceId,
	 * ArrayList<HashMap<String, String>> items) { super(context, resourceId,
	 * items); this.context = context; }
	 * 
	 * private view holder class private class ViewHolder { ImageView imageView;
	 * TextView txtTitle; TextView txtDesc; }
	 * 
	 * public View getView(int position, View convertView, ViewGroup parent) {
	 * ViewHolder holder = null; HashMap<String, String> tempMap = new
	 * HashMap<String, String>(); tempMap = followersdatas.get(position);
	 * LayoutInflater mInflater = (LayoutInflater) context
	 * .getSystemService(Activity.LAYOUT_INFLATER_SERVICE); if (convertView ==
	 * null) { convertView = mInflater.inflate( R.layout.similar_user_list_item,
	 * null); holder = new ViewHolder(); holder.txtDesc = (TextView)
	 * convertView.findViewById(R.id.desc); holder.txtTitle = (TextView)
	 * convertView .findViewById(R.id.title); holder.imageView = (ImageView)
	 * convertView .findViewById(R.id.icon); convertView.setTag(holder); } else
	 * holder = (ViewHolder) convertView.getTag();
	 * 
	 * holder.txtDesc.setText(tempMap
	 * .get(ConstantValues.TAG_PROFILE_FIRSTNAME)); //
	 * holder.txtTitle.setText("@" + tempMap.get(ConstantValues.username));
	 * profileLoader.displayImage(tempMap.get("profileImage"),
	 * holder.imageView);
	 * 
	 * return convertView; } }
	 * 
	 * @Override public void onClick(View v) { switch(v.getId()){
	 * 
	 * case R.id.btn_home: FragmentChangeActivity.rshome = true;
	 * startActivity(new Intent(this, FragmentChangeActivity.class)); break;
	 * case R.id.btn_near: FragmentChangeActivity.rsnear = true;
	 * startActivity(new Intent(this, FragmentChangeActivity.class)); break;
	 * case R.id.btn_shop: FragmentChangeActivity.rsshop = true;
	 * startActivity(new Intent(this, FragmentChangeActivity.class)); break;
	 * case R.id.btn_alert: FragmentChangeActivity.rsnote = true;
	 * startActivity(new Intent(this, FragmentChangeActivity.class)); break;
	 * case R.id.btn_profile: Intent i = new Intent(FollowersActivity.this,
	 * ProfileTabHolder.class); i.putExtra("userId", GetSet.getUserId());
	 * startActivity(i); break;
	 * 
	 * } //}
	 */
}
