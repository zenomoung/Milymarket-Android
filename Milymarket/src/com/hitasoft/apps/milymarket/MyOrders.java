package com.hitasoft.apps.milymarket;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.etsy.android.grid.StaggeredGridView;
import com.hitasoft.apps.milymarket.util.ConstantValues;
import com.hitasoft.apps.milymarket.util.GetSet;
import com.hitasoft.apps.milymarket.util.JSONParser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class MyOrders extends SherlockFragment implements OnItemClickListener,
		OnClickListener {

	private ImageLoader homeImageLoader;
	private StaggeredGridView gridView;
	private ArrayList<HashMap<String, String>> HomePageItems;
	private HashMap<Integer, ArrayList<HashMap<String, String>>> commentsMap = new HashMap<Integer, ArrayList<HashMap<String, String>>>();;
	private int currentPage = 0;
	AdapterForHdpi hdpiAdapter;
	RelativeLayout LoagingLayout;
	private static final int LDPI_PORTRAID = 1;
	private static final int LDPI_LANDSCAPE = 2;
	private static final int MDPI_PORTRAID = 3;
	private static final int MDPI_LANDSCAPE = 4;
	private static final int HDPI_PORTRAID = 5;
	private static final int HDPI_LANDSCAPE = 6;
	private static final int LARGE_MDPI_PORTRAID = 7;
	private static final int LARGE_MDPI_LANDSCAPE = 8;
	private static final int XHDPI_PORTRAID = 9;
	private static final int XHDPI_LANDSCAPE = 10;
	private static int device = 0;
	private static int layout = 0;
	LinearLayout bottomHome;
	RelativeLayout main;
	TextView centerText;
	private ImageButton home, near, cart, alert, menu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		homeImageLoader = ImageLoader.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.home_grid_layout, container,
				false);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		home = (ImageButton) getView().findViewById(R.id.btn_home);
		near = (ImageButton) getView().findViewById(R.id.btn_near);
		cart = (ImageButton) getView().findViewById(R.id.btn_cart);
		alert = (ImageButton) getView().findViewById(R.id.btn_alert);
		menu = (ImageButton) getView().findViewById(R.id.btn_menu);

		home.setOnClickListener(this);
		near.setOnClickListener(this);
		cart.setOnClickListener(this);
		alert.setOnClickListener(this);
		menu.setOnClickListener(this);

		centerText = (TextView) getView().findViewById(R.id.homenulltext2);
		centerText.setVisibility(View.INVISIBLE);
		bottomHome = (LinearLayout) getView().findViewById(R.id.bottomhome);
		main = (RelativeLayout) getView().findViewById(R.id.main);
		LoagingLayout = (RelativeLayout) (getView()
				.findViewById(R.id.loadImageLayout));
		LoagingLayout.setVisibility(View.GONE);
		gridView = (StaggeredGridView) (getView().findViewById(R.id.homePage_gallery));
		gridView.setSmoothScrollbarEnabled(true);
		gridView.setOnItemClickListener(this);
		((FragmentChangeActivity) getActivity()).getSupportActionBar()
				.setTitle(
						getActivity().getResources().getString(
								R.string.myorders));
		setColumns();
		int colorBlack = getResources().getColor(R.color.black);
		String text = getString(R.string.myorders);
		SpannableString spannable = new SpannableString(text);
		spannable.setSpan(new ForegroundColorSpan(colorBlack), 0,
				text.length(), 0);

		((FragmentChangeActivity) getActivity()).getSupportActionBar()
				.setTitle(spannable);
		if (GetSet.isLogged()) {
			loadData();
		} else {
			Toast.makeText(getActivity(), "Please Login to Continue",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	private void setColumns() {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int screenWidth = display.getWidth();
		if (screenWidth < 280
				&& getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			device = LDPI_PORTRAID;
			//gridView.setNumColumns(1);
			layout = R.layout.home_page_grid_item_port_for_ldpi;
		} else if (screenWidth < 350
				&& getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			device = LDPI_LANDSCAPE;
			layout = R.layout.home_page_grid_item_port_for_ldpi;
		} else if (screenWidth < 350
				&& getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			device = MDPI_PORTRAID;
			//gridView.setNumColumns(1);
			layout = R.layout.home_page_grid_item_port_for_mdpi;
		} else if (screenWidth < 500
				&& getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			device = MDPI_LANDSCAPE;
			layout = R.layout.home_page_grid_item_port_for_mdpi;
		} else if (screenWidth < 500
				&& getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			device = HDPI_PORTRAID;
			//gridView.setNumColumns(1);
			layout = R.layout.home_page_grid_item_port_for_hdpi;
		} else if (screenWidth < 900
				&& getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			device = HDPI_LANDSCAPE;
			layout = R.layout.home_page_grid_item_port_for_mdpi;
		} else if (screenWidth < 700
				&& getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			device = LARGE_MDPI_PORTRAID;
			layout = R.layout.home_page_grid_item_port_for_mdpi;
		} else if (screenWidth < 1000
				&& getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			device = LARGE_MDPI_LANDSCAPE;
			layout = R.layout.home_page_grid_item_port_for_mdpi;
		} else if (screenWidth < 800
				&& getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			device = XHDPI_PORTRAID;
			boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
			if (!tabletSize) {
			//	gridView.setNumColumns(1);
			}
			layout = R.layout.home_page_grid_item_port_for_hdpi;
		} else if (screenWidth < 1400
				&& getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			device = XHDPI_LANDSCAPE;
			layout = R.layout.home_page_grid_item_port_for_hdpi;
		} else {
			device = HDPI_PORTRAID;
			//gridView.setNumColumns(1);
			layout = R.layout.home_page_grid_item_port_for_mdpi;
		}

	}

	private void loadData() {
		HomePageItems = new ArrayList<HashMap<String, String>>();
		try {
			new homePageLoadImages().execute(0);
		} catch (Exception e) {
			Log.d("doInBackground", e.toString());
		}
		setAdapter();

	}

	private void setAdapter() {

		setColumns();
		hdpiAdapter = new AdapterForHdpi(getActivity());
		gridView.setAdapter(hdpiAdapter);
	}

	class homePageLoadImages extends AsyncTask<Integer, Void, Void> {
		ProgressDialog dialog = new ProgressDialog(MyOrders.this.getActivity());

		@Override
		protected Void doInBackground(Integer... params) {
			try {
				parsing(params[0]);
			} catch (Exception e) {

			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			LoagingLayout.setVisibility(View.GONE);
			if (currentPage == 0) {
				main.setVisibility(View.INVISIBLE);
				bottomHome.setVisibility(View.VISIBLE);
			}

		}

		@Override
		protected void onPostExecute(Void unused) {
			try {
				if (currentPage == 0) {
					main.setVisibility(View.VISIBLE);
					bottomHome.setVisibility(View.INVISIBLE);
				}
				LoagingLayout.setVisibility(View.GONE);
				hdpiAdapter.notifyDataSetChanged();
				if (HomePageItems.size() == 0) {
					centerText.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
				Log.d("doIndb", e.toString());
			}

		}

	}

	private void parsing(Integer page) {

		JSONArray items;
		HashMap<String, String> map;
		String urlAddr = ConstantValues.MyOrders + GetSet.getUserId();
		JSONParser jParser = new JSONParser();
		JSONObject json = jParser.getJSONFromUrl(urlAddr.trim());
		try {
			String response = json.getString(ConstantValues.TAG_STATUS);
			if (response.equalsIgnoreCase("true")) {
				JSONObject result = json
						.getJSONObject(ConstantValues.TAG_RESULT);
				items = result.getJSONArray(ConstantValues.TAG_ITEMS);
				for (int i = 0; i < items.length(); i++) {
					map = new HashMap<String, String>();
					String item_url_main_70 = null, item_url_main_350 = null, item_url_main_original = null;
					String comment_id = null, comment = null, user_id = null, user_img = null, username = null;
					JSONObject temp = items.getJSONObject(i);
					String id = temp.getString(ConstantValues.TAG_ID);
					String item_title = temp
							.getString(ConstantValues.TAG_TITLE);
					String item_description = temp
							.getString(ConstantValues.TAG_DESC);

					String price = temp.getString(ConstantValues.TAG_PRICE);
					String quantity = temp
							.getString(ConstantValues.TAG_QUANTITY);
					String favorites = temp
							.getString(ConstantValues.TAG_FAVORITES);
					String liked = temp.getString(ConstantValues.TAG_LIKED);

					JSONArray photos = temp
							.getJSONArray(ConstantValues.TAG_PHOTOS);
					for (int j = 0; j < 1; j++) {
						JSONObject photosTemp = photos.getJSONObject(j);
						item_url_main_70 = photosTemp
								.getString(ConstantValues.TAG_ITEM_URL_MAIN_70);
						item_url_main_350 = photosTemp
								.getString(ConstantValues.TAG_ITEM_URL_MAIN_350);
						item_url_main_original = photosTemp
								.getString(ConstantValues.TAG_ITEM_URL_MAIN_ORIGINAL);
					}
					JSONArray comments = temp
							.getJSONArray(ConstantValues.TAG_COMMENTS);
					ArrayList<HashMap<String, String>> tmp = new ArrayList<HashMap<String, String>>();
					for (int k = 0; k < comments.length(); k++) {
						JSONObject commentsTemp = comments.getJSONObject(k);
						HashMap<String, String> tmpMap = new HashMap<String, String>();
						comment_id = commentsTemp
								.getString(ConstantValues.TAG_COMMENT_ID);
						comment = commentsTemp
								.getString(ConstantValues.TAG_COMMENT);
						user_id = commentsTemp
								.getString(ConstantValues.TAG_USER_ID);
						user_img = commentsTemp
								.getString(ConstantValues.TAG_USER_IMG);
						username = commentsTemp
								.getString(ConstantValues.TAG_USERNAME);

						tmpMap.put(ConstantValues.TAG_COMMENT_ID, comment_id);
						tmpMap.put(ConstantValues.TAG_COMMENT, comment);
						tmpMap.put(ConstantValues.TAG_USER_ID, user_id);
						tmpMap.put(ConstantValues.TAG_USER_IMG, user_img);
						tmpMap.put(ConstantValues.TAG_USERNAME, username);
						tmp.add(tmpMap);
					}
					commentsMap.put(Integer.parseInt(temp
							.getString(ConstantValues.TAG_ID)), tmp);
					map.put(ConstantValues.TAG_ID, id);
					map.put(ConstantValues.TAG_TITLE, item_title);
					map.put(ConstantValues.TAG_DESC, item_description);
					map.put(ConstantValues.TAG_PRICE, price);
					map.put(ConstantValues.TAG_QUANTITY, quantity);

					map.put(ConstantValues.TAG_FAVORITES, favorites);
					map.put(ConstantValues.TAG_LIKED, liked);
					map.put(ConstantValues.TAG_ITEM_URL_MAIN_70,
							item_url_main_70);
					map.put(ConstantValues.TAG_ITEM_URL_MAIN_350,
							item_url_main_350);
					map.put(ConstantValues.TAG_ITEM_URL_MAIN_ORIGINAL,
							item_url_main_original);

					HomePageItems.add(map);

				}
			} else {

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public class AdapterForHdpi extends BaseAdapter {

		private Context mContext;

		public AdapterForHdpi(Context ctx) {
			mContext = ctx;
		}

		@Override
		public int getCount() {
			return HomePageItems.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(layout, parent, false);
			} else {
				view = convertView;
				view.forceLayout();
			}
			HashMap<String, String> tempMap = new HashMap<String, String>();
			tempMap = HomePageItems.get(position);
			final ImageView image = (ImageView) view
					.findViewById(R.id.singleImage);
			final ProgressBar loader = (ProgressBar) view
					.findViewById(R.id.singleImageLoader);
			ArrayList<HashMap<String, String>> commentTemp = commentsMap
					.get(Integer.parseInt(tempMap.get(ConstantValues.TAG_ID)));

			TextView title = (TextView) view
					.findViewById(R.id.single_item_bottom_title);
			TextView cost = (TextView) view
					.findViewById(R.id.single_item_bottom_cost);
			TextView comments = (TextView) view
					.findViewById(R.id.single_item_bottom_comments_count);
			final TextView fancy = (TextView) view.findViewById(R.id.fancyIt);
			if (GetSet.isLogged()) {
				String liked = tempMap.get(ConstantValues.TAG_LIKED);
				if (liked.equalsIgnoreCase("yes")) {
					fancy.setText(getResources().getString(R.string.unlike));
				} else {
					fancy.setText(getResources().getString(R.string.like));
				}
			}
			final LinearLayout fantacy = (LinearLayout) view
					.findViewById(R.id.single_item_bottom_fantacy);
			fantacy.setTag(position);
			fantacy.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					int point = (Integer) fantacy.getTag();
					new SendFancy().execute(point);
				}
			});
			comments.setText(Integer.toString(commentTemp.size()));
			title.setText(tempMap.get(ConstantValues.TAG_TITLE));
			cost.setText("$ " + tempMap.get(ConstantValues.TAG_PRICE));

			homeImageLoader.loadImage(
					tempMap.get(ConstantValues.TAG_ITEM_URL_MAIN_350),
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
							image.setImageBitmap(loadedImage);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}

	public int deviceNumber() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int n = dm.densityDpi;
		return 0;

	}

	class SendFancy extends AsyncTask<Integer, Void, JSONObject> {

		String fancyUrl = ConstantValues.fantacyUrl;
		JSONParser jParser = new JSONParser();
		int itemId;
		ProgressDialog dialog = new ProgressDialog(MyOrders.this.getActivity());

		@Override
		protected JSONObject doInBackground(Integer... params) {
			itemId = params[0];
			fancyUrl = fancyUrl + 38 + "&itemId=" + 6;
			JSONObject response = jParser.getJSONFromUrl(fancyUrl);
			return response;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Un instant s'il vous plait...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			try {
				String status = result.getString(ConstantValues.status);
				if (status.equalsIgnoreCase("true")) {
					Toast.makeText(getActivity(), result.getString("message"),
							Toast.LENGTH_LONG).show();
					if (result.getString("message").equalsIgnoreCase(
							"Item Liked")) {
						LinearLayout ll = (LinearLayout) gridView
								.getChildAt(itemId);
						TextView fancy = (TextView) ll
								.findViewById(R.id.fancyIt);
						fancy.setText("Fantacy'd");

					} else {
						LinearLayout ll = (LinearLayout) gridView
								.getChildAt(itemId);
						TextView fancy = (TextView) ll
								.findViewById(R.id.fancyIt);
						fancy.setText("Fantacy");
					}
				} else {
					Toast.makeText(getActivity(),
							"Sorry, something went wrong", Toast.LENGTH_LONG)
							.show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	public void onClick(View v) {
		FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
		switch (v.getId()) {

		case R.id.btn_home:
			FragmentChangeActivity.menumap = false;
			FragmentChangeActivity.filter_icon = false;
			getActivity().supportInvalidateOptionsMenu();
			fca.switchContent(new HomeFragment());
			break;
		case R.id.btn_near:
			FragmentChangeActivity.menumap = false;
			FragmentChangeActivity.filter_icon = true;
			getActivity().supportInvalidateOptionsMenu();
			fca.switchContent(new LocationFragment());
			break;
		case R.id.btn_cart:
			FragmentChangeActivity.menumap = false;
			FragmentChangeActivity.filter_icon = false;
			getActivity().supportInvalidateOptionsMenu();
			fca.switchContent(new CartFragmentClass());
			break;
		case R.id.btn_alert:
			FragmentChangeActivity.menumap = false;
			FragmentChangeActivity.filter_icon = false;
			getActivity().supportInvalidateOptionsMenu();
			fca.switchContent(new MessagesFragment());
			break;
		case R.id.btn_menu:
			FragmentChangeActivity.menumap = false;
			FragmentChangeActivity.filter_icon = false;
			getActivity().supportInvalidateOptionsMenu();
			fca.switchContent(new MenuFragment());
			break;
		}
	}

}
