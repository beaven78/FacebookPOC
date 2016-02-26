package fitconsultants.facebookpoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import java.io.InputStream;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    //private static final String TWITTER_KEY = "k5CLLWhCeJRQTf63HkRsZk8NM";
    //private static final String TWITTER_SECRET = "KuhsPb0DUmLyYlOH901CD8K7eDyyKqLefCXy9ezD4R4CnbfW8T";

    private static final String TWITTER_KEY = "jG6tfk6NiTZzJQS1giSk7G34l";
    private static final String TWITTER_SECRET = "amN2fp0d8rOzulRWrtTz3Un0CbDwECfFL0wqTYNtQNaCpA3qsC";

    private TextView info;
    private LoginButton loginButton;
    private ImageView imageView;

    private CallbackManager callbackManager;

    private TwitterLoginButton twitterLoginButton;
    private TextView status;

    private LoginResult loginResult;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        twitterLoginButton = (TwitterLoginButton)findViewById(R.id.twitter_login_button);

        twitterLoginButton.setCallback(new LoginHandler());

        System.out.println("#####" + twitterLoginButton.getCallback());

        status = (TextView)findViewById(R.id.status);
        status.setText("Status: Ready");



        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        //Facebook login success.
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginRes) {

                GetUserProfilePicture(loginRes.getAccessToken());

                info.setText(
                        "User ID: "
                                + loginRes.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginRes.getAccessToken().getToken()
                );
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
                e.printStackTrace();
            }
        });




    }

    private void GetUserProfilePicture(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        System.out.println("######");
                        System.out.println(response.getRawResponse());
                        System.out.println("######");
                        System.out.println("######");
                        System.out.println("######");
                        System.out.println("######");
                        System.out.println("######");

                        ProfilePictureView profilePictureView;

                        profilePictureView = (ProfilePictureView) findViewById(R.id.imageProfilePic);

                        try {
                            String userId = object.getString("id");

                            profilePictureView.setProfileId(userId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,picture.width(400).height(400)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    private class LoginHandler extends Callback<TwitterSession> {
        @Override
        public void success(Result<TwitterSession> result) {

            System.out.println("##### success #####");

            String output = "Status: " +
                    "Your login was successful " +
                    result.data.getUserName() +
                    "\nAuth Token Received: " +
                    result.data.getAuthToken().token;
            System.out.println("#####" + output);

            status.setText(output);
        }

        @Override
        public void failure(TwitterException e) {
            status.setText("Status: Login Failed");
        }
    }

    public MainActivity() {
        super();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

        GetUserProfilePicture(AccessToken.getCurrentAccessToken());

        twitterLoginButton = (TwitterLoginButton)findViewById(R.id.twitter_login_button);

        twitterLoginButton.setCallback(new LoginHandler());


        imageView = (ImageView)findViewById(R.id.imageView);

        TwitterAuthToken authToken = Twitter.getSessionManager().getActiveSession().getAuthToken();



        System.out.println("#### Twitter url: " + "https://twitter.com/" + Twitter.getSessionManager().getActiveSession().getUserName() + "/profile_image?size=original");


        Twitter.getApiClient().getAccountService().verifyCredentials(true, false, new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {

                System.out.println("#### image url: " + userResult.data.profileImageUrl);

                new LoadImage().execute(userResult.data.profileImageUrl.replace("_normal",""));
            }

            @Override
            public void failure(TwitterException e) {
            }
        });

    }


    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            System.out.println("##### fetching image");
        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

                System.out.println("###### Height: " + bitmap.getHeight());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {


            if(image != null){
                imageView.setImageBitmap(image);


            }
        }
    }
}
