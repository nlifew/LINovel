package cn.nlifew.xqdreader.bean;

public class CommentBean extends BeanSupport {
    public int Result;
    public String Message;
    public DataType Data;

    public static final class DataType {
        public TopicDataType[] TopicDataList;
        public int TotalCount;
    }

    public static final class TopicDataType {
        public int AuthorId;
        public String Body;
        public int BookId;
        public int BookType;
        public long CircleId;
        public int CircleType;
        public int FrameId;
        public String FrameUrl;
        public long Id;
        public Object ImgList;  // TODO: 空数组
        public int IsEssence;
        public int IsLike;
        public int IsTop;
        public int PostCount;
        public long PostDate;
        public int RankType;
        public int StarCount;
        public String StatId;
        public String Subject;
        public int SubType;
        public int TongRenType;
        public String UserHeadIcon;
        public int UserId;
        public String UserName;
    }
}
