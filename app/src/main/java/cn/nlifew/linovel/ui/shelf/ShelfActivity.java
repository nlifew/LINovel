package cn.nlifew.linovel.ui.shelf;

import android.os.Bundle;

import androidx.annotation.Nullable;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.fragment.shelf.ShelfFragment;
import cn.nlifew.linovel.ui.BaseActivity;

public class ShelfActivity extends BaseActivity {
    private static final String TAG = "ShelfActivity";

    private ShelfFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useDefaultLayout("我的书架");

        mFragment = new ShelfFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_base_host, mFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mFragment.isEditable()) {
            mFragment.setEditable(false);
        }
        else {
            super.onBackPressed();
        }
    }
}
