package cn.nlifew.linovel.ui.login;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

class CountDown {
    private static final String TAG = "CountDown";

    private MutableLiveData<Integer> mClock = new MutableLiveData<>(0);


    @SuppressLint("HandlerLeak")
    private final Handler mH = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.d(TAG, "handleMessage: " + msg.what);

            mClock.setValue(msg.what);
            if (msg.what > 0) {
                Message newMsg = Message.obtain();
                newMsg.what = msg.what - 1;
                sendMessageDelayed(newMsg, 1000);
            }
        }
    };

    @SuppressWarnings("all")    // fuck null pointer
    void startCountingDown(int second) {
        if (second < 0) {
            throw new UnsupportedOperationException("second < 0");
        }

        Integer time = mClock.getValue();
        if (time.intValue() != 0) {
            Log.w(TAG, "startCountingDown: I am working !");
            return;
        }
        Message msg = Message.obtain();
        msg.what = second;
        mH.sendMessage(msg);
    }

    @SuppressWarnings("all")    // fuck null pointer
    int getCurrentSecond() {
        return mClock.getValue();
    }


    void cancelCountingDown() {
        mH.removeCallbacksAndMessages(null);
        mClock.setValue(0);
    }

    void observe(LifecycleOwner owner, Observer<Integer> observer) {
        mClock.observe(owner, observer);
    }

    void removeObserver(Observer<Integer> observer) {
        mClock.removeObserver(observer);
    }
}
