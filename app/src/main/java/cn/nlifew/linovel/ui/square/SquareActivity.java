package cn.nlifew.linovel.ui.square;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.fragment.square.SquareFragment;
import cn.nlifew.linovel.ui.BaseActivity;

public class SquareActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        if (data == null) {
            throw new UnsupportedOperationException("give me a Uri data");
        }

        String id = data.getQueryParameter("id");
        String title = data.getQueryParameter("title");

        useDefaultLayout(title);

        SquareFragment fragment = SquareFragment.newInstance(id);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_base_host, fragment)
                .commit();
    }
}
