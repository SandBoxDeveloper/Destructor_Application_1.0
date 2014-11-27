package andrehitchman.destructor_application_10.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import andrehitchman.destructor_application_10.R;
import andrehitchman.destructor_application_10.adapters.UserAdapter;
import andrehitchman.destructor_application_10.utils.ParseConstants;

public class EditFriendsActivity extends Activity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();
    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // progress bar
        setContentView(R.layout.user_grid);

        mGridView = (GridView)findViewById(R.id.friendsGrid);
        // set ListView to allow multiple items to be checked.
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        // for when the GridView is empty
        TextView emptyTextView = (TextView)findViewById(android.R.id.empty);
        // attach this as the empty TextView for the GridView
        mGridView.setEmptyView(emptyTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = ParseUser.getQuery(); // return a list of Parse User objects
        query.orderByAscending(ParseConstants.KEY_USERNAME); // sort in ascending order of user-names
        query.setLimit(1000); // limit query to 1000 users
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                setProgressBarIndeterminateVisibility(false);

                if (e == null) {
                    // was success
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    // loading the gridView, setting the adapter, then re-filling it
                    if (mGridView.getAdapter() == null) {
                        // if null then create
                        UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, mUsers);
                        mGridView.setAdapter(adapter);
                    }
                    else {
                        // refill
                        ((UserAdapter)mGridView.getAdapter()).refill(mUsers);
                    }

                    addFriendCheckmarks();
                }
                else {
                    // failed
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    // set message title and text for the button
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null); // button to dismiss the dialog
                    // create a dialog, then show it
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        }); // execute query in background
    }


    private void addFriendCheckmarks() {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    // list returned
                    for (int i = 0; i < mUsers.size(); i++) {
                        ParseUser user = mUsers.get(i);
                       // loop through list of friends that is re-turned in this done method
                        for (ParseUser friend : friends) {
                            if(friend.getObjectId().equals(user.getObjectId())) {
                                // match found, set check-box
                                mGridView.setItemChecked(i, true); // causes the adapter to update the view again
                            }
                        }
                    }
                }
                else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // show or hide the image for the checkmark overlay

            // ref to ImageView
            ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);

            if (mGridView.isItemChecked(position)) {
                // add the friend
                mFriendsRelation.add(mUsers.get(position));
                checkImageView.setVisibility(View.VISIBLE); // make checkmark visible
            }
            else {
                // remove the friend
                mFriendsRelation.remove(mUsers.get(position));
                checkImageView.setVisibility(View.INVISIBLE); // make checkmark invisible
            }


            // this gets called for both if/else statements no matter what
            // save into backend (Parse)
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });

        }
    };
}
