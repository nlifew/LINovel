package cn.nlifew.linovel.ui.chapter;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.adapter.BaseFragmentPagerAdapter;
import cn.nlifew.linovel.fragment.BaseFragment;
import cn.nlifew.linovel.fragment.bookmark.BookMarkFragment;
import cn.nlifew.linovel.fragment.chapter.ChapterFragment;
import cn.nlifew.linovel.ui.BaseActivity;
import cn.nlifew.linovel.ui.main.MainAdapter;

public class ChapterActivity extends BaseActivity {
    private static final String TAG = "ChapterActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useDefaultLayout("章节");

        ViewGroup layout = findViewById(R.id.activity_base_host);
        View view = LayoutInflater.from(this)
                .inflate(R.layout.fragment_container, layout, true);

        TabLayout tab = view.findViewById(R.id.fragment_container_tab);
        ViewPager pager = view.findViewById(R.id.fragment_container_pager);
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);

        if (getIntent().getData() == null) {
            throw new UnsupportedOperationException("give me a Data");
        }
    }

    private final class FragmentAdapter extends BaseFragmentPagerAdapter {

        FragmentAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public BaseFragment createBaseFragment(int position) {
            Uri uri = getIntent().getData();
            String bookId = uri.getQueryParameter("id");

            switch (position) {
                case 0: return ChapterFragment.newInstance(bookId);
                case 1: return BookMarkFragment.newInstance(bookId);
            }
            throw new IndexOutOfBoundsException("impossible here: " + position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "章节列表";
                case 1: return "书签列表";
            }
            return "";
        }
    }
}
