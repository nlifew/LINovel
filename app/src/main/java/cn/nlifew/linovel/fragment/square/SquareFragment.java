package cn.nlifew.linovel.fragment.square;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import cn.nlifew.linovel.fragment.loadmore.BaseLoadMoreFragment;
import cn.nlifew.linovel.utils.DisplayUtils;
import cn.nlifew.linovel.utils.ToastUtils;
import cn.nlifew.linovel.widget.PaddingDividerDecoration;
import cn.nlifew.xqdreader.bean.SquareBean;

public class SquareFragment extends BaseLoadMoreFragment {
    private static final String TAG = "SquareFragment";

    private static final String ARGS_SQUARE_ID = "ARGS_SQUARE_ID";

    public static SquareFragment newInstance(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_SQUARE_ID, id);

        SquareFragment fragment = new SquareFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    private SquareViewModel mViewModel;
    private RecyclerAdapterImpl mRecyclerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new UnsupportedOperationException("call newInstance() to make a instance");
        }
        mViewModel = new ViewModelProvider(this).get(SquareViewModel.class);
        mViewModel.setSquareId(bundle.getString(ARGS_SQUARE_ID));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        PaddingDividerDecoration decoration = new PaddingDividerDecoration(
                getContext(), PaddingDividerDecoration.VERTICAL
        );
        decoration.setPadding(DisplayUtils.dp2px(20), 0,
                DisplayUtils.dp2px(15), 0);
        mRecyclerView.addItemDecoration(decoration);

        LifecycleOwner o = getViewLifecycleOwner();
        mViewModel.mData.observe(o, this::onRefreshSquareData);
        mViewModel.mErrorMsg.observe(o, this::onErrorMsgChanged);

        return view;
    }

    private void onErrorMsgChanged(String msg) {
        if (msg != null) {
            ToastUtils.getInstance(getContext()).show(msg);
            setLoading(false);
        }
    }

    private void onRefreshSquareData(SquareBean.DataType data) {
        if (data != null) {
            mRecyclerAdapter.updateDataSet(data);
            setLoading(false);
        }
    }

    @Override
    protected RecyclerView.Adapter newRecyclerAdapter() {
        mRecyclerAdapter = new RecyclerAdapterImpl(this);
        return mRecyclerAdapter;
    }

    @Override
    public void onRefresh() {
        setLoading(true);
        mViewModel.startLoadData();
    }
}
