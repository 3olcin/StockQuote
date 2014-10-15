package com.maalouf.StockQuote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

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
	
	
	final Handler loadContent = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			//Log.d("json", (String.valueOf(msg.obj)));
			//create json object
			try {
				JSONObject jObject = new JSONObject(String.valueOf(msg.obj));
				//webView.loadData(String.valueOf(msg.obj), "text/html", "UTF-8");
				JSONObject jArray = jObject.getJSONObject("list");
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
	
				//JSONObject meta = jArray.getJSONObject("meta");
				//String type = meta.getString("type");
				//webView.loadData("name:<br>" + name +
				//"<br><br>price:<br>" + price + 
				//"<br><br>symbol:<br>" + symbol +
				//"<br><br>TS:<br>" + ts +
				//"<br><br>type:<br>" + type +
				//"<br><br>utctime:<br>" + utctime +
				//"<br><br>volume:<br>" + volume);
				//"<br><br>volume:<br>" + volume, "text/html", "UTF-8");
					
				textView.setText("name:\n" + name +
						"\n\nprice:\n" + price + 
						"\n\nsymbol:\n" + symbol +
						"\n\nTS:\n" + ts +
						"\n\ntype:\n" + type +
						"\n\nutctime:\n" + utctime +
						"\n\nvolume:\n" + volume);
						//"<br><br>volume:<br>" + volume, "text/html", "UTF-8");
				//Log.d("json", name);

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
        //webView = (WebView) findViewById(R.id.webView);
        textView = (TextView) findViewById(R.id.textView);
        
        
        searchButton.setOnClickListener(new View.OnClickListener() {
       
			@Override
			public void onClick(View v) {
				
				Thread getURL = new Thread(){
				
			    @Override
				public void run(){
			
					if (isNetworkActive()){
						
						URL url = null;
						
						try {
							String stockSymbol = searchInput.getText().toString().toUpperCase();
							String stockURL = "http://finance.yahoo.com/webservice/v1/symbols/" + stockSymbol + "/quote?format=json";
							url = new URL(stockURL);
							BufferedReader reader = new BufferedReader(
									new InputStreamReader(
											url.openStream()));
							
							String response = "";
							String tmpResponse = "";
							
							tmpResponse = reader.readLine();
							while (tmpResponse != null){
								response = response + tmpResponse;
								tmpResponse = reader.readLine();
							}
							
							Message message = loadContent.obtainMessage();
							
						
							message.obj = response;
							
							loadContent.sendMessage(message);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}
			};
			
			getURL.start();
			
				
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
