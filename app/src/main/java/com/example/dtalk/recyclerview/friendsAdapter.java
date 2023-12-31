package com.example.dtalk.recyclerview;

import static com.example.dtalk.retrofit.RetrofitClient.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dtalk.R;
import com.example.dtalk.profile;
import com.example.dtalk.retrofit.friendsListCheckResponse;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class friendsAdapter extends RecyclerView.Adapter<friendsAdapter.ViewHolder> {
    private Context context;

    private ArrayList<friendsListCheckResponse.Friend> data;  // 데이터 소스

    public friendsAdapter(ArrayList<friendsListCheckResponse.Friend> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        friendsListCheckResponse.Friend item = data.get(position);
        //데이터세팅
        holder.bind(item);
        holder.profile.setOnClickListener(new View.OnClickListener() { //프로필 터치시
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, profile.class);
                //친구아이디 프로필액티비티로 넘김
                intent.putExtra("friendId",item.getFriendID());
                context.startActivity(intent);
            }
        });
        
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView user_profile_img;
        private TextView my_profile_nick;
        private TextView my_porfile_msg;
        private LinearLayout profile;

        public ViewHolder(View itemView) {
            super(itemView);
            user_profile_img = (ImageView)itemView.findViewById(R.id.user_profile_img);
            my_profile_nick = (TextView) itemView.findViewById(R.id.my_profile_nick);
            my_porfile_msg = (TextView) itemView.findViewById(R.id.my_porfile_msg);
            profile = (LinearLayout) itemView.findViewById(R.id.profile);

        }

        public void bind(friendsListCheckResponse.Friend friend) {
            my_profile_nick.setText(friend.getUserName());
            my_porfile_msg.setText(friend.getUserStatusMsg());
            Log.d("TAG", "bind: "+friend.getUserStatusMsg());
            // Glide를 사용하여 이미지 로딩
            Glide.with(itemView.getContext())
                    .load(BASE_URL+friend.getUserProfileImage()) // friend.getImageUrl()는 이미지의 URL 주소
                    .into(user_profile_img);

        }
    }
    public void itemfilter(ArrayList<friendsListCheckResponse.Friend> filterlist) {
        //여기서 변경내용 데이터에 넣어주고 새로고침
        data = filterlist;
        notifyDataSetChanged(); //새로고침
    }



}
