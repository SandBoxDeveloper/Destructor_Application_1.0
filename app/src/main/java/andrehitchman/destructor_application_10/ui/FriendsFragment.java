package andrehitchman.destructor_application_10.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

import andrehitchman.destructor_application_10.R;
import andrehitchman.destructor_application_10.adapters.UserAdapter;
import andrehitchman.destructor_application_10.utils.ParseConstants;


/**
 * Created by Zeus on 03/11/2014.
 */
public class FriendsFragment extends Fragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    protected GridView mGridView;

    //called when the layout is drawn for th first time
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid, container, false);

        mGridView = (GridView)rootView.findViewById(R.id.friendsGrid);
        // for when the GridView is empty
        TextView emptyTextView = (TextView)rootView.findViewById(android.R.id.empty);
        // attach this as the empty TextView for the GridView
        mGridView.setEmptyView(emptyTextView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        getActivity().setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
               getActivity().setProgressBarIndeterminateVisibility(false);
               if (e ==  null) {


                   mFriends = friends;

                   String[] usernames = new String[mFriends.size()];
                   int i = 0;
                   for (ParseUser user : mFriends) {
                       usernames[i] = user.getUsername();
                       i++;
                   }
                   if (mGridView.getAdapter() == null) {
                       // if null then create
                       UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                       mGridView.setAdapter(adapter);
                   }
                   else {
                        // refill
                       ((UserAdapter)mGridView.getAdapter()).refill(mFriends);
                   }
               }
               else {
                   // failed
                   Log.e(TAG, e.getMessage());
                   AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                   // set message title and text for the button
                   builder.setMessage(e.getMessage())
                           .setTitle(R.string.error_title)
                           .setPositiveButton(android.R.string.ok, null); // button to dismiss the dialog
                   // create a dialog, then show it
                   AlertDialog dialog = builder.create();
                   dialog.show();
               }




            }
        });
    }

}
