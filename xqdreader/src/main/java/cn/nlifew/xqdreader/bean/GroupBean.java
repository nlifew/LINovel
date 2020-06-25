package cn.nlifew.xqdreader.bean;

public class GroupBean extends BeanSupport {

    public int Result;
    public String Message;
    public DataType Data;

    public static final class DataType {
        public String ActionText;
        public String ActionUrl;
        public int Format;
        public int FrontType;
        public int Id;
        public String StatId;
        public String Title;
        public SubDataType Data;
    }

    public static final class SubDataType {
        public int TotalCount;
        public SquareBean.BookDataType[] Items;
    }
}
