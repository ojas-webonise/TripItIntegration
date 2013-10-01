package com.tripitintegration;

import java.util.HashMap;
import java.util.Map;

import com.tripitintegration.TripItIntegrationActivity.OnVerifyListener;
import com.tripitintegration.api.Action;
import com.tripitintegration.api.Client;
import com.tripitintegration.api.Response;
import com.tripitintegration.api.Type;
import com.tripitintegration.auth.OAuthCredential;
import com.tripitintegration.util.SharedPref;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private SharedPref mSharedPref;
	private String strAccessToken = null;
	private String strAccessTokenSecret = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trip_it_layout);
		mSharedPref = new SharedPref(getApplicationContext());
	}
	
	public void onClickLogin(View view) {
		if (isUserAuthenticated()) {
			Toast.makeText(getApplicationContext(), "Already logged in ...", Toast.LENGTH_SHORT).show();
		} else {
			tripItLogin();				
		}
	}
	
	private boolean isUserAuthenticated() {
		strAccessToken = mSharedPref.readString(SharedPref.TRIPIT_OAUTH_ACCESS_TOKEN, null);
		strAccessTokenSecret = mSharedPref.readString(SharedPref.TRIPIT_OAUTH_ACCESS_TOKEN_SECRET, null);
		System.out.println("strAccessToken="+strAccessToken+"===strAccessTokenSecret="+strAccessTokenSecret+"==");
		if (strAccessToken == null && strAccessTokenSecret == null)
			return false;
		else 
			return true;
	}

	public void onClickGetTripItData(View view) {
		getTripData();
	}
	
	/**
	 * Connect IceBreaker with linkedIn.
	 * i.e. send  linkedIn access token to IceBreaker server.
	 */
	private void tripItLogin()
	{
		ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);//.show(LinkedInSampleActivity.this, null, "Loadong...");
		//set progress dialog 
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(true);
		
		TripItIntegrationActivity dialog = new TripItIntegrationActivity(MainActivity.this, progressDialog);
		//set call back listener to get oauth_verifier value
		dialog.setVerifierListener(new OnVerifyListener() {
			@Override
			public void onVerify(String strToken, String strTokenSecret) {
				try	{
					Log.i("TripItSample", "verified");
					strAccessToken = strToken;
					strAccessTokenSecret = strTokenSecret;
					getTripData();
				} catch (Exception e) {
					Log.i("TripItSample", "error to get verifier");
					e.printStackTrace();
				}
			}
		});
		
		dialog.show();

	}

	private void getTripData() {
		OAuthCredential credentials = new OAuthCredential(TripItIntegrationActivity.TRIPIT_CONSUMER_KEY, 
				TripItIntegrationActivity.TRIPIT_CONSUMER_SECRET, strAccessToken, strAccessTokenSecret);
        Client client = new Client(credentials, Client.DEFAULT_API_URI_PREFIX);
        Map<String, String> requestParameterMap = new HashMap<String, String>();
        Action a = null;
        Type type = null;
		try {
			a = Action.get("list");
			type = Type.get(a, "trip");
		} catch (Exception e) {
			e.printStackTrace();
		} 
        
		try {
			Response response = a.execute(client, type, requestParameterMap );
			System.out.println(" response == "+response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

}
