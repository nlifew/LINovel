package cn.nlifew.linovel.ui.reading;

import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cn.nlifew.linovel.app.ThisApp;
import cn.nlifew.xqdreader.XQDReader;
import cn.nlifew.xqdreader.entity.Account;
import cn.nlifew.xqdreader.network.IRequest;
import cn.nlifew.xqdreader.utils.NetworkUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static cn.nlifew.linovel.utils.IOUtils.readLEInt;
import static cn.nlifew.linovel.utils.IOUtils.dupStream;

class Helper {
    private static final String TAG = "Helper";


    static String parseChapterText(String bookId, int chapterId) throws IOException {
        IRequest request = NetworkUtils.create(IRequest.class);
        Call<ResponseBody> call = request.getChapterContent(bookId, chapterId, 0);

        Response<ResponseBody> resp = call.execute();
        try (ResponseBody body = resp.body()) {
            InputStream is = body.byteStream();
            String s = parseChapterText(is, bookId, chapterId);
            is.close();
            return s;
        }
    }

    static String parseChapterText(InputStream is, String bookId, int chapterId) throws IOException {
        File file = new File(ThisApp.currentApplication.getExternalCacheDir(),
                bookId + "_" + chapterId + ".qd");
        is = dupStream(is, file);
        byte[][] bytesArr = format(is);
        is.close();

        if (bytesArr[0].length != 0) {
            // 可能是加密方法不对 ?
            return "";
        }

        try {
            Account account = Account.currentAccount();
            long userId = account == null ? 0 : Long.parseLong(account.getUserId());

            byte[] bytes = a.b.b(Long.parseLong(bookId),
                    chapterId, bytesArr[1], userId, XQDReader.IMEI);

            String s = new String(bytes, 0, bytes.length, StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(s);

            if (chapterId == -10000) {
                // 需要展示版权说明等乱七八糟的
                JSONObject content = json.optJSONObject("Content");
                if (content != null) {
                    s = content.optString("AuthorWords", s);
                    return s;
                }
            }
            s = json.optString("Content", s);
            return s;
        } catch (Exception e) {
            Log.e(TAG, "loadBookChapter: ", e);
            return "";
        }
    }

    private static byte[][] format(InputStream is) throws IOException {
        byte[][] result = new byte[4][];
        int count;

        for (int i = 0; i < 4; i++) {
            int len = readLEInt(is);
            result[i] = new byte[len];
            if ((count = is.read(result[i])) != len) {
                throw new IOException("expected " + len + " bytes, actually " + count + " bytes");
            }
        }
        return result;
    }

    static List<CharSequence> split(TextView tv) {
        int height = tv.getHeight() - tv.getPaddingTop() - tv.getPaddingBottom();

        // 我们这里需要对 TextView 的 height 做一下调整
        // 经过 Helper 的分页之后，每一页的最后一行仍然有可能显示不全
        // 即：这一行的上一半显示出来，但下一半被屏幕遮挡的现象
        // 所以要单独留出一行的空间做缓冲
        height -= tv.getLineHeight();

        CharSequence text = tv.getText();
        Layout layout = tv.getLayout();

        // 考虑到 CharSequence 并不一定就是 String
        // 每行的高度都不一定相等，因此在计算一页有多少行时，
        // 并不能单纯的通过 每页的高度 / 每行的高度 来计算。
        // 因此我们只能遍历每一行的高度，直到大于等于页的高度

        List<CharSequence> list = new ArrayList<>(20);

        int startLineTop = 0, startLineIndex = 0;
        int n = layout.getLineCount();

        for (int i = 0; i < n; i++) {
            if (layout.getLineTop(i) - startLineTop >= height) {
                int start = layout.getLineStart(startLineIndex);
                int end = layout.getLineStart(i);
                list.add(text.subSequence(start, end));

                startLineIndex = i;
                startLineTop = layout.getLineTop(i);
            }
        }

        // 把剩下的内容作为最后一页
        int start = layout.getLineStart(startLineIndex);
        int end = text.length();
        list.add(text.subSequence(start, end));

        Log.d(TAG, "split: text.length() == " + text.length() + " list.size() == " + list.size());
        return list;
    }

    static int getPageIndexByPosition(List<CharSequence> list, int position) {
        int sum = 0;
        for (int i = 0, n = list.size(); i < n; i++) {
            CharSequence s = list.get(i);
            if (sum + s.length() >= position) {
                return i;
            }
            sum += s.length();
        }
        return 0;
    }

    static int getPositionByPageIndex(List<CharSequence> list, int pageIndex) {
        int sum = 0;
        for (int i = 0; i <= pageIndex; i++) {
            sum += list.get(i).length();
        }
        return sum;
    }

    static CharSequence append(CharSequence title, CharSequence text) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(title).append("\n\n");
        sb.setSpan(DP20, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(BOLD, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        sb.append(text);
        return sb;
    }

    private static final AbsoluteSizeSpan DP20 = new AbsoluteSizeSpan(28, true);
    private static final StyleSpan BOLD = new StyleSpan(Typeface.BOLD);
}
