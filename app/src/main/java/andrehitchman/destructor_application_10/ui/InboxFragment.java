package andrehitchman.destructor_application_10.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import andrehitchman.destructor_application_10.R;
import andrehitchman.destructor_application_10.adapters.MessageAdapter;
import andrehitchman.destructor_application_10.utils.ParseConstants;

/**
 * Created by Zeus on 03/11/2014.
 */
public class InboxFragment extends ListFragment {

    protected List<ParseObject> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    //called when the layout is drawn for th first time
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        // look at all the ID's included in this list, see if match for our specific object ID
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        // make most recent message appear at the top of the inbox
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        // run query
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                // dismiss progress indicator
                getActivity().setProgressBarIndeterminateVisibility(false);

                if(e == null) {
                    // found messages
                    mMessages = messages;
                    // display senders name
                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }
                    // create adapter once, then update its state on each refresh
                    if (getListView().getAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(
                                getListView().getContext(),
                                mMessages);
                        setListAdapter(adapter);
                    }
                    else {
                        // refill the adapter
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position); //  message at the current position
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE); // type of message
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE); // get Parse file
        Uri fileUri = Uri.parse(file.getUrl());

        // check message type
        if(messageType.equals(ParseConstants.TYPE_IMAGE)) {
            // view the image
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.setData(fileUri);
            startActivity(intent);
        }
        else {
            // view the video
            Intent intent =  new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);
        }

        // delete message
        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
        // if one recipient left, then delete whole message
        // if more than one, delete this recipient and save the change back onto backend

        if(ids.size() == 1){
            // last recipient - delete whole thing
            message.deleteInBackground();
        }
        else {
            // remove the recipient and save
            ids.remove(ParseUser.getCurrentUser().getObjectId());

            ArrayList<String> idsToRemove = new ArrayList<String>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

            message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove); // will remove all ids from this (idsToRemove) collection from the entire list of ids
            message.saveInBackground();
        }
    }

    protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {
            Toast.makeText(getActivity(), "We're refreshing!", Toast.LENGTH_SHORT).show();
        }
    };
}
