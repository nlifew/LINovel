package cn.nlifew.linovel.ui.empty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.ui.login.SMSLoginActivity;
import cn.nlifew.linovel.ui.main.MainActivity;
import cn.nlifew.linovel.ui.novel.NovelActivity;
import cn.nlifew.linovel.ui.splash.SplashActivity;
import cn.nlifew.linovel.utils.DisplayUtils;
import cn.nlifew.xqdreader.bean.BookShelfBean;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmptyActivity extends AppCompatActivity {
    private static final String TAG = "EmptyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_empty);
        findViewById(R.id.activity_empty_btn0).setOnClickListener(this::onButton0);
/*
        setContentView(R.layout.activity_empty2);

        Toolbar toolbar = findViewById(R.id.activity_space_toolbar);
        setSupportActionBar(toolbar);

        RecyclerView view = findViewById(R.id.activity_space_recycler);
        view.setAdapter(new RecyclerView.Adapter() {

            final class Holder extends RecyclerView.ViewHolder {
                Holder(@NonNull View itemView) {
                    super(itemView);
                }
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                TextView tv = new TextView(EmptyActivity.this);
                tv.setGravity(Gravity.CENTER);
                tv.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        DisplayUtils.dp2px(50)
                ));
                return new Holder(tv);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                TextView tv = (TextView) holder.itemView;
                tv.setText(String.valueOf(position));
            }

            @Override
            public int getItemCount() {
                return 50;
            }
        });
 */
    }


    private void onButton0(View view) {
        Intent intent = new Intent(this, MainActivity.class);
//        Uri uri = Uri.parse("linovel://novel?id=1017334629&title=射程之内遍地真理&outOfBook=0");
//        intent.setData(uri);

        startActivity(intent);
//        finish();
//        new Thread() {
//            @Override
//            public void run() {
//                final long t0 = System.currentTimeMillis();
//                try {
//                    runThrow();
//                } catch (Exception e) {
//                    Log.e(TAG, "run: ", e);
//                }
//                final long t1 = System.currentTimeMillis();
//                Log.d(TAG, "run: cost " + (t1 - t0) + " ms");
//            }
//        }.start();
    }

    private void runThrow() throws Exception {

    }
}
