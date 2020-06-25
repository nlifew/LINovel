package cn.nlifew.linovel.fragment.history;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.List;

import cn.nlifew.xqdreader.database.CommonDatabase;
import cn.nlifew.xqdreader.entity.ReadingRecord;

public class HistoryViewModel extends ViewModel {
    private static final String TAG = "HistoryViewModel";

    static final class Wrapper {
        static final int TYPE_REFRESH = 1;
        static final int TYPE_LOAD_MORE = 2;

        @IntDef({TYPE_REFRESH, TYPE_LOAD_MORE})
        @Retention(RetentionPolicy.SOURCE)
        @interface Type {}

        List<ReadingRecord> list;
        @Type int type;

        Wrapper(@Type int type, List<ReadingRecord> list) {
            this.type = type;
            this.list = list;
        }
    }

    final MutableLiveData<String> mErrMsg = new MutableLiveData<>(null);
    final MutableLiveData<Wrapper> mRecordList = new MutableLiveData<>(null);

    private int mPageIndex;
    private int mTotalItemCount, mCurrentItemCount;


    void refreshReadingRecord() {
        mPageIndex = 0;
        mTotalItemCount = mCurrentItemCount = 0;

        new LoadReadingRecordTask(Wrapper.TYPE_REFRESH)
                .execute(0, 20);
    }

    void loadMoreReadingRecord() {
        if (mCurrentItemCount >= mTotalItemCount) {
            mErrMsg.postValue("没有更多数据");
            return;
        }
        new LoadReadingRecordTask(Wrapper.TYPE_LOAD_MORE)
                .execute(mPageIndex, 20);
    }

    void clearAllHistory() {

    }

    private final class LoadReadingRecordTask extends AsyncTask<Integer, Void, Object> {

        LoadReadingRecordTask(@Wrapper.Type int type) {
            mType = type;
        }

        private int mCount, mType;

        @Override
        protected Object doInBackground(Integer... ints) {
            final int pageIndex = ints[0];
            final int pageLimit = ints[1];

            try {
                CommonDatabase db = CommonDatabase.getInstance();
                ReadingRecord.Helper helper = db.getReadingRecordHelper();
                mCount = helper.getCount();
                return helper.findRecordList(pageIndex, pageLimit);
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object o) {

            if (o instanceof String) {
                mErrMsg.postValue((String) o);
                return;
            }
            @SuppressWarnings("unchecked")
            List<ReadingRecord> list = (List<ReadingRecord>) o;
            mTotalItemCount = mCount;
            mCurrentItemCount += list.size();
            mPageIndex ++;

            Wrapper wrapper = new Wrapper(mType, list);
            mRecordList.postValue(wrapper);
        }
    }

    private final class ClearAllHistoryTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "ClearAllHistoryTask";

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                CommonDatabase db = CommonDatabase.getInstance();
                ReadingRecord.Helper helper = db.getReadingRecordHelper();
                helper.deleteAll();
                mRecordList.postValue(null);
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
                mErrMsg.postValue(e.toString());
            }

            return null;
        }
    }
}
