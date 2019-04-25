package trrakee.pnavw.viewModel;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import trrakee.pnavw.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity mContext;
    private UserModel mUserModel;

    CustomInfoWindowAdapter(Activity context, UserModel userModel) {
        mContext = context;
        mUserModel = userModel;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = mContext.getLayoutInflater().inflate(R.layout.custom_info_window_layout, null);

        ImageView userImage = view.findViewById(R.id.custom_info_window_layout_image);
        TextView userName = view.findViewById(R.id.custom_info_window_layout_name);

        userImage.setImageResource(mUserModel.getImage());
        userName.setText(mUserModel.getName());


        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
