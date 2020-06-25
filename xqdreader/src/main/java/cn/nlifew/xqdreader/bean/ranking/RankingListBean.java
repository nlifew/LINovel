package cn.nlifew.xqdreader.bean.ranking;

import cn.nlifew.xqdreader.bean.BeanSupport;

public class RankingListBean extends BeanSupport {
    public int Result;
    public String Message;
    public DataType[] Data;

    public static final class DataType {
        public String Author;
        public int AuthorId;
        public int BookId;
        public int BookLevel;
        public String BookName;
        public String BookStatus;
        public int CategoryId;
        public String CategoryName;
        public String CoverUrl;
        public String Description;
        public int IsOutBook;
        public int PlayCount;
        public int TopNo;
        public int WordsCount;
    }
}
