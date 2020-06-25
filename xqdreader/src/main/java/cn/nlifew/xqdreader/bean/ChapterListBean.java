package cn.nlifew.xqdreader.bean;

import java.util.Objects;

public class ChapterListBean extends BeanSupport {
    public int Result;
    public String Message;
    public DataType Data;

    public static final class DataType {
        public String Author;
        public long BookCoverImgHashCode;
        public int BookId;
        public String BookName;
        public String BookStatus;
        public int CategoryId;
        public String CategoryName;
        public ChapterType[] Chapters;
        public String DeletedChapters;
        public int EnableBookUnitBuy;
        public int EnableBookUnitLease;
        public int EnableDownloadAll;
        public long EndTime;
        public int FirstChapterId;
        public int ImageStatus;
        public int IscanPraise;
        public int IsFreeLimit;
        public String IsFreeLimitMsg;
        public int IsJingPai;
        public int IsMemberBook;
        public int IsOffline;
        public int IsPublication;
        public int IsQin;
        public int isReload;
        public long LastChapterUpdateTime;
        public int LastUpdateChapterID;
        public String LastUpdateChapterName;
        public long LastVipChapterUpdateTime;
        public int LastVipUpdateChapterId;
        public String LastVipUpdateChapterName;
        public MChapterType[] MChapters;
        public String Msg;
        public int RealWholeSale;
        public int RemainingTime;
        public int Ret;
        public String SignStatus;
        public int SourceBookId;
        public int SubCategoryId;
        public String SubCategoryName;
        public int TotalPrice;
        public VolumeType[] Volumes;
        public int WholeSale;
        public int WordsCount;
    }

    public static final class ChapterType {
        public int C;
        public int Cci;
        public int Ccs;
        public long DisplayTime;
        public int Fl;
        public String N;
        public int O;
        public int P;
        public long T;
        public int Ui;
        public int V;
        public int Vc;
        public int W;
    }

    public static final class MChapterType {
        public int C;
        public long DisplayTime;
        public long T;
    }

    public static final class VolumeType {
        public int VolumeCode;
        public String VolumeName;
    }
}
