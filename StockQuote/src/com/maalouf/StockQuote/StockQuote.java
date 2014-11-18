package com.maalouf.StockQuote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.maalouf.StockQuote.R;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StockQuote extends Activity {
	
	EditText searchInput;
	Button searchButton;
	WebView webView;
	TextView textView;
	Timer timer = new Timer();
	
	
	final Handler loadContent = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
		
			try {
				JSONObject jObject = new JSONObject(String.valueOf(msg.obj));
				Log.i("10 SEC?", String.valueOf(msg.obj));

				//webView.loadData(String.valueOf(msg.obj), "text/html", "UTF-8");
				JSONObject jArray = jObject.getJSONObject("list");
				Log.i("YES", jArray.toString());
				JSONArray resources = jArray.getJSONArray("resources");
				JSONObject resourcesObject = resources.getJSONObject(0);
				JSONObject resource = resourcesObject.getJSONObject("resource");
				JSONObject fields = resource.getJSONObject("fields");
				
				
				String name = fields.getString("name");
				String price = fields.getString("price");
				String symbol = fields.getString("symbol");
				String ts = fields.getString("ts");
				String type = fields.getString("type");
				String utctime = fields.getString("utctime");
				String volume = fields.getString("volume");
	
				
				textView.setText("name:\n" + name +
						"\n\nprice:\n" + price + 
						"\n\nsymbol:\n" + symbol +
						"\n\nTS:\n" + ts +
						"\n\ntype:\n" + type +
						"\n\nutctime:\n" + utctime +
						"\n\nvolume:\n" + volume);
						

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	      searchInput = (EditText) findViewById(R.id.searchbar); 
	        searchButton = (Button) findViewById(R.id.search);
	        textView = (TextView) findViewById(R.id.textView);


		searchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				timer = new Timer();

				timer.scheduleAtFixedRate(new TimerTask() {
					public void run() {

						try {
							if (isNetworkActive()) {

								try {
									String stockSymbol = searchInput.getText()
											.toString();
									URL url = new URL(
											"http://finance.yahoo.com/webservice/v1/symbols/"
													+ stockSymbol
													+ "/quote?format=json");

									try {

										BufferedReader in = new BufferedReader(
												new InputStreamReader(url
														.openStream()));
										String data = "";
										String inputLine = "";
										while ((inputLine = in.readLine()) != null)
											data += inputLine;
										in.close();

										JSONObject jsonResponse = new JSONObject(
												data);

										Message msg = new Message();
										msg.obj = jsonResponse;

										loadContent.sendMessage(msg);

									} catch (Exception e) {
										Log.e("Read Error", e.toString());
									}

								} catch (Exception e) {
									Log.e("Read Error", e.toString());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}, 0, 10000);
			}
		});
	}
	
	
   

    public boolean isNetworkActive(){
    		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    		if (networkInfo != null && networkInfo.isConnected()) {
    			return true;
    		} else {
    			return false;
    		}
    }
}
