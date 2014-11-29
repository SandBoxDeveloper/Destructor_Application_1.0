package andrehitchman.destructor_application_10;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

import andrehitchman.destructor_application_10.ui.MainActivity;
import andrehitchman.destructor_application_10.utils.ParseConstants;
import andrehitchman.destructor_application_10.utils.Receiver;

/**
 * Created by Zeus on 29/10/2014.
 */
public class DestructorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "3omIOH3U2q7RrrIYv4tR0VOXNriFdNtQUULiPDDY", "NHYQOY7bk2KDUA8E6JfaIGeDRcdhAdORHnrOB0jO");

        //PushService.setDefaultPushCallback(this, MainActivity.class);
        //PushService.setDefaultPushCallback(this, MainActivity.class, R.drawable.ic_stat_ic_launcher);
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        // save this installation to Parse
        installation.saveInBackground();
    }
}
