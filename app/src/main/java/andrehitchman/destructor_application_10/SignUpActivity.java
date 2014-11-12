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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mEmail;
    protected Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sign_up);

        mUsername = (EditText)findViewById(R.id.usernameField);
        mPassword = (EditText)findViewById(R.id.passwordField);
        mEmail = (EditText)findViewById(R.id.emailField);
        mSignUpButton = (Button)findViewById(R.id.signupButton);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();

                // remove whitespace
                username = username.trim();
                password = password.trim();
                email = email.trim();
                // make sure non of the values are blank
                if(username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    // requires user action
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    // set message title and text for the button
                    builder.setMessage(R.string.signup_error_message)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, null); // button to dismiss the dialog
                    // create a dialog, then show it
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else {
                    // create new user
                    setProgressBarIndeterminateVisibility(true);
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            // exception null is sign-up exceeded
                            if (e == null) {
                                // success
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                // so that user isn't able ot go back to the sign-up page with the back button
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // create a new class
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear navigation history
                                startActivity(intent);
                            }

                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                // set message title and text for the button
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.signup_error_title)
                                        .setPositiveButton(android.R.string.ok, null); // button to dismiss the dialog
                                // create a dialog, then show it
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    }); // saves the user in a background thread, and calls a special callback method when its complete
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
