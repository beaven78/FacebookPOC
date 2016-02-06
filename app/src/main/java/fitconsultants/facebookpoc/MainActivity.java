package fitconsultants.facebookpoc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

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

    private CallbackManager callbackManager;

    private TwitterLoginButton twitterLoginButton;
    private TextView status;

    private LoginResult loginResult2;

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
            public void onSuccess(LoginResult loginResult) {

                GetUserProfilePicture(loginResult);

                info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
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

    private void GetUserProfilePicture(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
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

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
