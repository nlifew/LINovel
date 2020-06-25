package cn.nlifew.xqdreader.bean;

import cn.nlifew.xqdreader.entity.BookShelf;

public class BookShelfBean extends BeanSupport {
    public int Result;
    public String Message;
    public DataType Data;

    public static final class DataType {
        public long ServerTime;
        public Object CaseMap;      // todo 空数组
        public ServerCaseType ServerCase;
    }

    public static final class ServerCaseType {
        public Object AudioInfo;
        public BookShelf[] BookInfo;
        public Object CateInfo;
        public Object ComicInfo;
        public int[] DelBookId;
    }
}
