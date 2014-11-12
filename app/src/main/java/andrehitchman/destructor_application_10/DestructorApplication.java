package andrehitchman.destructor_application_10;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Zeus on 29/10/2014.
 */
public class DestructorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "3omIOH3U2q7RrrIYv4tR0VOXNriFdNtQUULiPDDY", "NHYQOY7bk2KDUA8E6JfaIGeDRcdhAdORHnrOB0jO");


    }
}
