package andrehitchman.destructor_application_10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;

    // variable will be visible to subclasses of this class
    protected TextView mSignUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // needs to be set before setContentView
        setContentView(R.layout.activity_login);

        mSignUpTextView = (TextView)findViewById(R.id.signUpText);
        // create onClickListener for signUp
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // switch to signUp activity
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });

        mUsername = (EditText)findViewById(R.id.usernameField);
        mPassword = (EditText)findViewById(R.id.passwordField);
        mLoginButton = (Button)findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                // remove whitespace
                username = username.trim();
                password = password.trim();

                // make sure non of the values are blank
                if(username.isEmpty() || password.isEmpty()) {
                    // requires user action
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    // set message title and text for the button
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null); // button to dismiss the dialog
                    // create a dialog, then show it
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else {
                    // login
                    // show progress indicator
                    setProgressBarIndeterminateVisibility(true);

                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            setProgressBarIndeterminateVisibility(false); // make progress bar invisible
                            if (e == null) {
                                // success
                                // take user to inbox
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                // so that user isn't able ot go back to the sign-up page with the back button
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // create a new class
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear navigation history
                                startActivity(intent);

                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                // set message title and text for the button
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.login_error_title)
                                        .setPositiveButton(android.R.string.ok, null); // button to dismiss the dialog
                                // create a dialog, then show it
                                AlertDialog dialog = builder.create();
                                dialog.show();

                            }
                        }
                    });
                }
            }
        });
    }

}
