package cn.nlifew.linovel.fragment.novel;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.adapter.FragmentViewHolder;
import cn.nlifew.xqdreader.bean.NovelBean;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import cn.nlifew.xqdreader.utils.Utils;

class AlikeViewHolder extends FragmentViewHolder<NovelBean.DataType> {

    AlikeViewHolder(Fragment fragment, ViewGroup parent) {
        super(fragment, R.layout.fragment_home_horizontal, parent);

        TextView tv = itemView.findViewById(R.id.fragment_home_horizontal_title);
        tv.setTextAppearance(R.style.NovelItemKeyTextAppearance);
        tv.setText("猜你喜欢");

        mHolders = new Holder[] {
                new Holder(itemView.findViewById(R.id.fragment_home_horizontal_item_1)),
                new Holder(itemView.findViewById(R.id.fragment_home_horizontal_item_2)),
                new Holder(itemView.findViewById(R.id.fragment_home_horizontal_item_3)),
                new Holder(itemView.findViewById(R.id.fragment_home_horizontal_item_4)),
        };
    }

    private final Holder[] mHolders;

    @Override
    public void onBindViewHolder(NovelBean.DataType data) {
        StringBuilder sb = Utils.obtainStringBuilder(16);
        for (int i = 0; i < mHolders.length; i++) {
            Holder holder = mHolders[i];
            NovelBean.BookFriendType book = data.BookFriendsRecommend[i];

            sb.setLength(0);
            sb.append(book.AlsoReadPercent).append("%还看过");
            String cover = NetworkUtils.novelCoverImage(book.BookId);

            holder.mTitleView.setText(book.BookName);
            holder.mAboutView.setText(sb);

            Glide.with(mFragment).asBitmap().load(cover).into(holder.mCoverView);
        }
        Utils.recycle(sb);
    }

    private static final class Holder {
        final ImageView mCoverView;
        final TextView mTitleView;
        final TextView mAboutView;

        Holder(View view) {
            mCoverView = view.findViewById(R.id.fragment_home_horizontal_item_cover);
            mTitleView = view.findViewById(R.id.fragment_home_horizontal_item_title);
            mAboutView = view.findViewById(R.id.fragment_home_horizontal_item_about);
        }
    }
}
