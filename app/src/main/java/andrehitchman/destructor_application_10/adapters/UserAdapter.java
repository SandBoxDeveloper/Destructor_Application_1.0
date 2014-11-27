package andrehitchman.destructor_application_10.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import andrehitchman.destructor_application_10.R;
import andrehitchman.destructor_application_10.utils.MD5Util;

/**
 * Created by Zeus on 20/11/14.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {

    protected Context mContext;
    protected List<ParseUser> mUsers;

    public UserAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.message_item, users); // call parent constructor
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) { // views are recycled for the list view. Recycles the views if they already exists
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            holder.checkImageView = (ImageView)convertView.findViewById(R.id.checkImageView);
            convertView.setTag(holder); // to allow users to scroll through the list of items, without an error
        }
        else {
            holder = (ViewHolder)convertView.getTag(); // get ViewHolder already created
        }

        ParseUser user = mUsers.get(position);
        // get user's email address
        String email = user.getEmail().toLowerCase();
        // check for empty string
        if (email.equals("")) {
            // if empty, use default image
            holder.userImageView.setImageResource(R.drawable.avatar_empty);
        }
        else {
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
            Picasso.with(mContext)
                    .load(gravatarUrl)
                    .placeholder(R.drawable.avatar_empty) // if HTTP response code 404 returned, display default
                    .into(holder.userImageView);
        }
        holder.nameLabel.setText(user.getUsername());

        // ref to the GridView
        GridView gridView = (GridView)parent;
        // check if the item being tapped on, is tapped.
        if (gridView.isItemChecked(position)) {
            // show checkmark
            holder.checkImageView.setVisibility(View.VISIBLE);
        }
        else {
            // if not checked, make invisible
            holder.checkImageView.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private  static class ViewHolder {
        ImageView userImageView;
        ImageView checkImageView;
        TextView nameLabel;
    }

    public void refill(List<ParseUser> users) {
        mUsers.clear(); // clear current data first
        mUsers.addAll(users); // then add all new ones
        notifyDataSetChanged();
    }
}
