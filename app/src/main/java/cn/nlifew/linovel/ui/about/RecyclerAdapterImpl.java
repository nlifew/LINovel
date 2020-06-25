package cn.nlifew.linovel.ui.about;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.utils.ToastUtils;

class RecyclerAdapterImpl extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerAdapterImpl";

    RecyclerAdapterImpl(Activity activity) {
        mActivity = activity;
    }

    private final Activity mActivity;
    private final SimpleBean[] mDataSet = new SimpleBean[] {
            new SimpleBean("Okhttp", "square", "https://github.com/square/okhttp", "a HTTP & HTTP/2 client for Android and Java applications"),
            new SimpleBean("Retrofit", "square", "https://github.com/square/retrofit", "Type-safe HTTP client for Android and Java by Square, Inc."),
            new SimpleBean("Gson", "Google", "https://github.com/google/gson", "A java serialization library that can convert Java Objects into JSON and back."),
            new SimpleBean("Glide", "bumptech", "https://github.com/bumptech/glide", "An image loading and caching library for Android focused on smooth scrolling."),
            new SimpleBean("CircleImageView", "hdodenhof", "https://github.com/hdodenhof/CircleImageView", "A circular ImageView for Android"),
            new SimpleBean("FABProgressCircle", "JorgeCastilloPrz", "https://github.com/JorgeCastilloPrz/FABProgressCircle", "Material progress circle around any FloatingActionButton. 100% Guidelines."),
            new SimpleBean("android-floating-action-button", "zendesk", "https://github.com/zendesk/android-floating-action-button", "Floating Action Button for Android based on Material Design specification"),
            new SimpleBean("MaterialLogin", "fanrunqi", "https://github.com/fanrunqi/MaterialLogin", "Amazing Material Login effect"),
            new SimpleBean("ScrollCollapsingLayout", "nlifew", "https://github.com/nlifew/ScrollCollapsingLayout", "基于 CollapsingToolbarLayout 的可使子view同步移动的控件"),


    };

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity)
                .inflate(R.layout.activity_about_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        SimpleBean bean = mDataSet[position];
        Holder holder = (Holder) h;

        holder.titleView.setText(bean.title);
        holder.authorView.setText(bean.author);
        holder.descView.setText(bean.description);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(bean.url));
            try {
                mActivity.startActivity(intent);
            } catch (Exception e) {
                ToastUtils.getInstance(mActivity).show(e.toString());
            }
        });
    }

    private final class Holder extends RecyclerView.ViewHolder {
        Holder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.activity_about_item_title);
            authorView = itemView.findViewById(R.id.activity_about_item_author);
            descView = itemView.findViewById(R.id.activity_about_item_desc);
        }
        final TextView titleView;
        final TextView descView;
        final TextView authorView;
    }
}

