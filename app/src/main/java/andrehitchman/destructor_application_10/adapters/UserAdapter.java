package andrehitchman.destructor_application_10.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

import andrehitchman.destructor_application_10.R;
import andrehitchman.destructor_application_10.utils.ParseConstants;

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
            //holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            convertView.setTag(holder); // to allow users to scroll through the list of items, without an error
        }
        else {
            holder = (ViewHolder)convertView.getTag(); // get ViewHolder already created
        }

        ParseUser user = mUsers.get(position);

        /*if(user.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
            holder.iconImageView.setImageResource(R.drawable.ic_picture);
        }
        else {
            holder.iconImageView.setImageResource(R.drawable.ic_video);
        }*/
        holder.nameLabel.setText(user.getUsername());

        return convertView;
    }

    private  static class ViewHolder {
        //ImageView iconImageView;
        TextView nameLabel;
    }

    public void refill(List<ParseUser> users) {
        mUsers.clear(); // clear current data first
        mUsers.addAll(users); // then add all new ones
        notifyDataSetChanged();
    }
}
