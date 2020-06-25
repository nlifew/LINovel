package cn.nlifew.xqdreader.bean;

import java.util.Objects;

public class TopBookMarkBean extends BeanSupport {
    public int Result;
    public String Message;
    public BookMarkType[] TopBookMarkList;

    public static final class BookMarkType {
        public String AreaId;
        public int BookId;
        public int BookMarkId;
        public int ChapterId;
        public String ChapterName;
        public long CreateDate;
        public int InternalAreaId;
        public int MarkType;
        public int Position;
        public Object ReferContent; // todo: null
        public long UploadTime;
        public int UserId;
        public Object WordsDesc;    // todo: null
    }
}
