package cn.nlifew.xqdreader.bean.category;

import cn.nlifew.xqdreader.bean.BeanSupport;

public class CategoryBookBean extends BeanSupport {
    public int Result;
    public String Message;
    public DataType Data;

    public static final class DataType {
        public BookType[] Books;
        public int TotalCount;
    }

    public static final class BookType {
        public int ActionStatus;
        public String ActionStatusString;
        public String AuthorName;
        public int BookId;
        public int BookLevel;
        public String BookName;
        public int BookType;
        public int CategoryId;
        public String CategoryName;
        public String Description;
        public String ExtValues;
        public int InterestType;
        public int ReadAll;
        public int SectionCount;
        public int Staticscore;
        public int SubCategoryId;
        public String SubCategoryName;
        public int WordsCount;
    }
}
