package com.tripitintegration;

import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.signature.HmacSha1MessageSigner;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;

import com.tripitintegration.api.Client;
import com.tripitintegration.util.SharedPref;

public class TripItIntegrationActivity extends Dialog {

	private ProgressDialog progressDialog = null;
	private Context mContext;
	private OnVerifyListener mVerifyListener;
	
	private DefaultOAuthProvider provider;
	private DefaultOAuthConsumer consumer;
	
	private SharedPref mSharedPref;
    
	private String strRequestToken = null;
	private String strRequestTokenSecret = null;
//	private String strAccessToken = null;
//	private String strAccessTokenSecret = null;
	
	public static final String TRIPIT_CONSUMER_KEY = "b67dae42f98c3f2223172ae318135f538607c9f9";
	public static final String TRIPIT_CONSUMER_SECRET = "2064fb81888ce7066a519139c9a9d7d8770edafc";
	
	public static final String TRIPIT_OAUTH_CALLBACK_SCHEME = "x-oauthflow-tripit";
	public static final String TRIPIT_OAUTH_CALLBACK_HOST = "tripitcallback";
	public static final String TRIPIT_OAUTH_CALLBACK_URL = TRIPIT_OAUTH_CALLBACK_SCHEME + "://" + TRIPIT_OAUTH_CALLBACK_HOST;
	
	private String requestTokenUrl = Client.DEFAULT_API_URI_PREFIX + "/oauth/request_token";
	private String accessTokenUrl = Client.DEFAULT_API_URI_PREFIX + "/oauth/access_token";
	private String authorizeUrl = Client.DEFAULT_WEB_URI_PREFIX + "/oauth/authorize";
	
	/**
	 * Construct a new Tripit dialog
	 * @param context activity {@link Context}
	 * @param mProgressDialog {@link ProgressDialog}
	 * @param strAuthorizationUrl {@link String}
	 */
	public TripItIntegrationActivity(Context mContext, ProgressDialog mProgressDialog) {
		super(mContext);
		this.mContext = mContext;
		this.progressDialog = mProgressDialog;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ln_dialog);
		mSharedPref = new SharedPref(this.mContext);
		progressDialog.show();
		GetOauthAccessToken();
	}

	private void GetOauthAccessToken() {
        consumer = new DefaultOAuthConsumer(TRIPIT_CONSUMER_KEY, TRIPIT_CONSUMER_SECRET);
        consumer.setMessageSigner(new HmacSha1MessageSigner());

        provider = new DefaultOAuthProvider(requestTokenUrl, accessTokenUrl, authorizeUrl);
        System.out.println("\nfetching unauthorized request token...");

        String authUrl = null;
		try {
			authUrl = provider.retrieveRequestToken(consumer, TRIPIT_OAUTH_CALLBACK_URL);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}

		strRequestToken = consumer.getToken();
		strRequestTokenSecret = consumer.getTokenSecret();
		mSharedPref.writeString(SharedPref.TRIPIT_OAUTH_REQUEST_TOKEN, strRequestToken);
		mSharedPref.writeString(SharedPref.TRIPIT_OAUTH_REQUEST_TOKEN_SECRET, strRequestTokenSecret);
		
        System.out.println("request token key: "+strRequestToken+"\nrequest token secret: "+strRequestTokenSecret);
        
        setWebView(authUrl);
	}

	private void setWebView(String authUrl) {
		WebView mWebView = (WebView) findViewById(R.id.webkitWebView1);
		mWebView.getSettings().setJavaScriptEnabled(true);

		Log.i("Tripit integration", authUrl);

		mWebView.loadUrl(authUrl);
		mWebView.setWebViewClient(new HelloWebViewClient());
		
		mWebView.setPictureListener(new PictureListener(){
			@Override
			public void onNewPicture(WebView view, Picture picture){
				if(progressDialog != null && progressDialog.isShowing()){
					progressDialog.dismiss(); 
				}
			}
		});		
	}
	
	/**
	 * webview client for internal url loading 
	 */
	class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			System.out.println(" url === "+ url);
//			if(url.contains(OAUTH_CALLBACK_URL)) {
				
				Uri uri = Uri.parse(url);
				String verifier = uri.getQueryParameter("oauth_token");
				System.out.println(" verifier == "+ verifier);
				
		        try {
					provider.retrieveAccessToken(consumer, verifier.trim());
				} catch (OAuthMessageSignerException e) {
					e.printStackTrace();
				} catch (OAuthNotAuthorizedException e) {
					e.printStackTrace();
				} catch (OAuthExpectationFailedException e) {
					e.printStackTrace();
				} catch (OAuthCommunicationException e) {
					e.printStackTrace();
				}

//		        strAccessToken = consumer.getToken();
//		        strAccessTokenSecret = consumer.getTokenSecret();
		        mSharedPref.writeString(SharedPref.TRIPIT_OAUTH_ACCESS_TOKEN, consumer.getToken());
				mSharedPref.writeString(SharedPref.TRIPIT_OAUTH_ACCESS_TOKEN_SECRET, consumer.getTokenSecret());
		        System.out.println("access token key: " + consumer.getToken());
		        System.out.println("access token secret: " + consumer.getTokenSecret());	
				
//		        getTripData();
				cancel();
				mVerifyListener.onVerify(consumer.getToken(), consumer.getTokenSecret());
//				
//				for(OnVerifyListener d : listeners) {
//					//call listener method
//					d.onVerify(verifier);
//				}
//			} else {
//				Log.i("LinkedinSample", "url: "+url);
//				view.loadUrl(url);
//			}
			
			return true;
		}
	}
	
//	private void getTripData() {
//		credentials = new OAuthCredential(TRIPIT_CONSUMER_KEY, TRIPIT_CONSUMER_SECRET, 
//				strAccessToken, strAccessTokenSecret);
//        Client client = new Client(credentials, Client.DEFAULT_API_URI_PREFIX);
//        Map<String, String> requestParameterMap = new HashMap<String, String>();
//        Action a = null;
//        Type type = null;
//		try {
//			a = Action.get("list");
//			type = Type.get(a, "trip");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//        
//		try {
//			Response response = a.execute(client, type, requestParameterMap );
//			System.out.println(" response == "+response);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}	
	
	/**
	 * Register a callback to be invoked when authentication have finished. 
	 *@param data The callback that will run 
	 */
	public void setVerifierListener(OnVerifyListener mListener) {
		this.mVerifyListener = mListener;
//		listeners.add(data);
	}

	/**
	 * Listener for oauth_verifier.
	 */
	interface OnVerifyListener {
		/**
		 * invoked when authentication  have finished. 
		 * @param string2 
		 * @param strToken 
		 * @param verifier oauth_verifier code.
		 */
		public void onVerify(String strToken, String strTokenSecret);
	}

}
