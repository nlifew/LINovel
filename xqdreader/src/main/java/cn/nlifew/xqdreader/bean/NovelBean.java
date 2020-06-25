package cn.nlifew.xqdreader.bean;

public class NovelBean extends BeanSupport {
    public int Result;
    public String Message;
    public DataType Data;

    public static final class DataType {
        public long Adid;
        public String AuthCopyRight;
        public String Author;
        public int AuthorId;
        public AuthorInfoType AuthorInfo;
        public String AuthorPostCount;
        public String AuthorRecommendFlag;
        public String AuthorRecommendTitle;
        public int AuthorUserId;
        public String BookFansDesc;
        public String BookFansRankName;
        public String BookFansUserName;
        public int BookForumCount;
        public BookFriendType[] BookFriendsRecommend;
        public BookHonorType[] BookHonor;
        public int BookId;
        public int BookLevel;
        public String BookLevelActionUrl;
        public int BookMode;
        public String BookName;
        public BookPartType BookPartInfo;
        public String BookReviewCircleHelpUrl;
        public BookReviewType[] BookReviewList;
        public int BookStar;
        public String BookStatus;
        public int BssReadTotal;
        public int BssRecomTotal;
        public int CategoryId;
        public String CategoryName;
        public long Cbid;
        public int ChargeType;
        public String DescCopyRight;
        public String Description;
        public int EnableDonate;
        public int EnableVoteMonth;
        public FanType[] FansList;
        public FreshManType FreshManSimple;
        public int HasUserAdmin;
        public int HonorTotalCount;
        public String InterestInfo;
        public int IsJingPai;
        public int IsLimit;
        public int IsMemberBook;
        public int IsOffline;
        public int IsOutBook;
        public int IsPublication;
        public int IsVip;
        public String JoinTimeCopyRight;
        public long LastChapterUpdateTime;
        public String LastUpdateChapter;
        public int LastUpdateChapterID;
        public String LastUpdateChapterName;
        public long LastVipChapterUpdateTime;
        public int LastVipUpdateChapterId;
        public String LastVipUpdateChapterName;
        public int LimitEnd;
        public int LimitStart;
        public int MonthTicketCount;
        public int NewBookInvestCount;
        public int RecommendAll;
        public String RecommendWord;
        public RoleLikeType RoleLikeInfo;
        public RoleType[] RoleList;
        public int SourceBookId;
        public String SubCategoryName;
        public int TotalChapterCount;
        public int TotalFansCount;
        public int TotalRoleCount;
        public Object UserAdmins;   // todo: 空数组
        public int WordsCnt;
    }

    public static final class AuthorInfoType {
        public int AuthorId;
        public String AuthorLevel;
        public int AuthorLevelId;
        public int BookCount;
        public String RealImageUrl;
    }

    public static final class BookFriendType {
        public String AlgInfo;
        public int BookId;
        public String BookName;
        public int BssReadTotal;
        public String StatId;
        public int AlsoReadPercent;
        public int BookLevel;
        public int WordsCount;
    }

    public static final class BookHonorType {
        public String HonorType;
        public String Honors;
        public long Time;
    }

    public static final class BookPartType {
        public Object BookPartList; // todo: 空数组
        public int Position;
    }

    public static final class FanType {
        public String NickName;
        public String RealImageUrl;
        public int UserId;
    }

    public static final class FreshManType {
        public int IsInBlackList;
        public int IsNew;
        public int Status;
        public int WelfareBook;
    }

    public static final class BookReviewType {
        public int AuthorId;
        public String Body;
        public int BookType;
        public long CircleId;
        public int CircleType;
        public long Id;
        public int IsEssence;
        public int IsLike;
        public int IsTop;
        public int PostCount;
        public long PostDate;
        public int RankType;
        public int StarCount;
        public String Subject;
        public String UserHeadIcon;
        public int UserId;
        public String UserName;
    }

    public static final class RoleType {
        public String Position;
        public long RoleId;
        public String RoleName;
        public String LikeIconAfter;
        public String LikeIconBefore;
        public int LikeIconStatus;
        public int LikeStatus;
        public int Likes;
        public Object TagList;  // todo: 空数组
    }

    public static final class RoleLikeType {
        public int IsDouble;
        public String Text;
        public String ToastDesc;
        public String ToastTitle;
        public String VideoFail;
        public String VideoFinish;
        public String VideoSucc;
    }

    @Override
    public void trim() {
    }
}










