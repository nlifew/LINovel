package cn.nlifew.xqdreader.bean;


import android.app.Service;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.nlifew.xqdreader.utils.Utils;

public class SquareBean extends BeanSupport {
    public int Result;
    public String Message;
    public DataType Data;

    public static final class DataType {
        public SquareType Square;
        public GroupType[] ItemList;
    }

    public static final class SquareType {
        public int SexType;
        public int SquareType;
        public String Title;
    }

    public static final class GroupType {
        public static final int FORMAT_BANNER           =   4;
        public static final int FORMAT_TOOLBAR          =   2;
        public static final int FORMAT_BROADCAST        =   3;
        public static final int FORMAT_VERTICAL         =   12;
        public static final int FORMAT_TIMER            =   21;
        public static final int FORMAT_IMAGE_BUTTON     =   1;
        public static final int FORMAT_GRID             =   31;
        public static final int FORMAT_HORIZONTAL       =   11;
        public static final int FORMAT_TEXT_BANNER      =   5;

        public String ActionText;
        public String ActionUrl;

        /*
         * 这个值根据 Format 值的不同而不同，需要动态解析
         * 我们把这一步放在 trim() 函数中
         */
        public Object Data;

        public int Format;
        public int FrontType;
        public int Id;
        public String StatId;
        public String Title;
    }

    public static final class SimpleDataType {
        public String ActionText;
        public String ActionUrl;
        public String ImageUrl;

        @SuppressWarnings("unchecked")
        static SimpleDataType[] toArray(Object obj) {
            List<Map<String, String>> list = (List<Map<String, String>>) obj;
            SimpleDataType[] array = new SimpleDataType[list.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = new SimpleDataType(list.get(i));
            }
            return array;
        }

        private SimpleDataType(Map<String, String> map) {
            this.ActionText = map.get("ActionText");
            this.ActionUrl = map.get("ActionUrl");
            this.ImageUrl = map.get("ImageUrl");

            StringBuilder sb = Utils.obtainStringBuilder(32);
            this.ActionUrl = transActionUrl(sb, ActionText, ActionUrl);
            Utils.recycle(sb);
        }
    }

    public static final class BookDataType {
        public int AuthorId;
        public String AuthorName;
        public int BookId;
        public int BookLevel;
        public String BookName;
        public String BookStatus;
        public int BssReadTotal;
        public String CategoryName;
        public String Description;
        public int InterestType;
        public int IsOutBook;
        public int QqBookId;
        public int WordsCount;

        public BookDataType() {  }

        BookDataType(Map<String, Object> map) {
            this.AuthorId = (int) ((Double) map.get("AuthorId")).doubleValue();
            this.AuthorName = (String) map.get("AuthorName");
            this.BookId = (int) ((Double) map.get("BookId")).doubleValue();
            this.BookLevel = (int) ((Double) map.get("BookLevel")).doubleValue();
            this.BookName = (String) map.get("BookName");
            this.BookStatus = (String) map.get("BookStatus");
            this.BssReadTotal = (int) ((Double) map.get("BssReadTotal")).doubleValue();
            this.CategoryName = (String) map.get("CategoryName");
            this.Description = (String) map.get("Description");
            this.InterestType = (int) ((Double) map.get("InterestType")).doubleValue();
            this.IsOutBook = (int) ((Double) map.get("IsOutBook")).doubleValue();
            this.QqBookId = (int) ((Double) map.get("QqBookId")).doubleValue();
            this.WordsCount = (int) ((Double) map.get("WordsCount")).doubleValue();
        }

        @SuppressWarnings("unchecked")
        static BookDataType[] toArray(Object obj) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
            BookDataType[] array = new BookDataType[list.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = new BookDataType(list.get(i));
            }
            return array;
        }
    }

    public static final class LimitedTimeDataType {
        public BookDataType[] BookInfos;
        public String EndDate;
        public long EndTime;
        public String StartDate;

        public LimitedTimeDataType() {  }

        @SuppressWarnings("unchecked")
        LimitedTimeDataType(Object obj) {
            Map<String, Object> map = (Map<String, Object>) obj;
            this.EndDate = (String) map.get("EndDate");
            this.StartDate = (String) map.get("StartDate");
            this.EndTime = (long) ((Double) map.get("EndTime")).doubleValue();
            this.BookInfos = BookDataType.toArray(map.get("BookInfos"));
        }
    }

    @Override
    public void trim() {
        if (Data == null || Data.ItemList == null) {
            return;
        }
        StringBuilder sb = Utils.obtainStringBuilder(64);

        for (GroupType g : Data.ItemList) {
            switch (g.Format) {
                case GroupType.FORMAT_BANNER:
                case GroupType.FORMAT_TOOLBAR:
                case GroupType.FORMAT_BROADCAST:
                case GroupType.FORMAT_IMAGE_BUTTON:
                case GroupType.FORMAT_TEXT_BANNER: {
                    g.Data = SimpleDataType.toArray(g.Data);
                    break;
                }
                case GroupType.FORMAT_VERTICAL:
                case GroupType.FORMAT_GRID:
                case GroupType.FORMAT_HORIZONTAL: {
                    g.Data = BookDataType.toArray(g.Data);
                    break;
                }
                case GroupType.FORMAT_TIMER: {
                    g.Data = new LimitedTimeDataType(g.Data);
                    break;
                }
            }
            g.ActionUrl = transActionUrl(sb, g.Title, g.ActionUrl);
        }
        Utils.recycle(sb);
    }

    private static String transActionUrl(StringBuilder sb, String text, String url) {
        sb.setLength(0);

        if (url.startsWith("QDReader://app/BookDetail?query={")) {
            sb.append("linovel://novel?id=");
            Utils.appendQueryParam(url, 33, "bookId", sb);
            sb.append("&title=").append(text.trim()).append("&outOfBook=0");
            return sb.toString();
        }
        if (url.startsWith("QDReader://app/BookList?query={")) {
            sb.append("linovel://group?id=");
            Utils.appendQueryParam(url, 31, "itemId", sb);
            sb.append("&title=");
            Utils.appendQueryParam(url, 42, "itemName", sb);
            return sb.toString();
        }
        if (url.startsWith("QDReader://app/BookStore?query={")) {
            sb.append("linovel://square?id=");
            Utils.appendQueryParam(url, 32, "pageId", sb);
            sb.append("&title=").append(text);
            return sb.toString();
        }
        return url;
    }
}
