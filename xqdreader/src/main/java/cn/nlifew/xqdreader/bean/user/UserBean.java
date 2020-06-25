package cn.nlifew.xqdreader.bean.user;

import com.google.gson.annotations.SerializedName;

import cn.nlifew.xqdreader.bean.BeanSupport;

public class UserBean extends BeanSupport {

    public int Result;
    public String Message;
    public DataType Data;

    public static final class DataType {
        public AudioType AudioInfo;
        public AuthorBookType AuthorBooks;
        public AuthorType AuthorInfo;
        public AuthorTalkType AuthorTalk;
        public AuthorTitleType AuthorTitle;
        public ChapterReviewType ChapterReview;
        public CircleReviewType CircleReview;
        public ColumnType Column;
        public DynamicType[] Dynamic;
        public HasBookType HasBookList;
        public LatestChapterType LatestChapter;
        public MyBookType MyBookList;
        public Object NewBookNotice;// TODO: 空数组
        public RoleCardType RoleCard;
        public UserType UserInfo;
        public YueLiType YueLi;
    }

    public static final class AudioType {
        public Object DataList;     // TODO: 空数组
        public int TotalCount;
    }

    public static final class AuthorBookType {
        public AuthorBookItemType[] BookList;
        public int Count;
    }

    public static final class AuthorBookItemType {
        public int AuthorId;
        public String AuthorName;
        public int BookId;
        public String BookName;
        public String BookStatus;
        public int CategoryId;
        public String CategoryName;
        public String Description;
        public int WordsCount;
    }

    public static final class AuthorType {
        public int AuthorId;
        public String AuthorLevelName;
        public String AuthorName;
        public int CanBeFollowed;
        public String Description;
        public int FansCount;
        public String HeadImage;
        public int TotalWordsCount;
        public int WriteDays;
    }

    public static final class AuthorTalkType {
        public Object AuthorTalkList;   // TODO: 空数组
        public int Count;
    }

    public static final class AuthorTitleType {
        public int AuthorId;
        public String[] HeadImages;
        public int TitleCount;
        public String TitleName;
    }

    public static final class ChapterReviewType {
        public ChapterReviewItemType[] ChapterReviewList;
        public int Count;
    }

    public static final class ChapterReviewItemType {
        public int AuthorId;
        public String AuthorName;
        public int BookId;
        public String BookName;
        public long CbId;
        public int ChapterId;
        public String ChapterName;
        public String Content;
        public long CreateTime;
        public String HeadImg;
        public int InteractionStatus;
        public int LikeCount;
        public String NickName;
        public String RefferContent;
        public long ReviewId;
        public long RoleBookId;
        public long RoleId;
        public long RootReviewId;
        public int ShowType;
        public String StatId;
        public int UserId;
    }

    public static final class CircleReviewType {
        @SerializedName(value = "CircleReviewList", alternate = {"Items"})
        public CircleReviewItemType[] CircleReviewList;

        @SerializedName(value = "Count", alternate = {"TotalCount"})
        public int Count;
    }

    public static final class CircleReviewItemType {
        public int AuthorId;
        public String AuthorName;
        public int BookId;
        public String BookName;
        public String BookStatus;
        public int BookType;
        public int CategoryId;
        public String CategoryName;
        public int CbId;
        public String CircleIcon;
        public long CircleId;
        public String CircleName;
        public int CircleType;
        public String Content;
        public long Date;
        public String DateDesc;
        public String HeadImg;
        public int LikeCount;
        public String NickName;
        public int ReplyCount;
        public long ReviewId;
        public String StatId;
        public int SubType;
        public String Title;
        public int UserId;
        public long WordsCount;
    }

    public static final class ColumnType {
        public ColumnItemType[] ColumnList;
        public int Count;
    }

    public static final class ColumnItemType {
        public int[] BookIds;
        public int ColumnId;
        public int CommentCount;
        public String Description;
        public int LikeCount;
        public String StatId;
        public String Title;
    }

    public static final class DynamicType {
        public int BookId;
        public String BookName;
        public long Date;
        public String DateDesc;
        public String Title;
    }

    public static final class HasBookType {
        public Object BookLists;            // TODO: 空数组
        public int Count;
    }

    public static final class LatestChapterType {
        public int BookId;
        public String BookName;
        public long ChapterId;
        public String ChapterName;
        public long UpdateTime;
    }

    public static final class MyBookType {
        public MyBookItemType[] BookLists;
        public int Count;
    }

    public static final class MyBookItemType {
        public String AuthorHeadImg;
        public String AuthorName;
        public int BookCount;
        public String BookListType;
        public int BookListTypeId;
        public Object Books; // TODO:
        public int CollectCount;
        public String Des;
        public int Id;
        public String Name;
        public String StatId;
        public int UserId;
    }

    public static final class RoleCardType {
        public int HasNum;
    }

    public static final class UserType {
        public String AuthorTitleActionUrl;
        public int AuthorTitleCount;
        public String BadgeActionUrl;
        public int BadgeCount;
        public int BookCount;
        public int CanBeFollowed;
        public String Description;
        public int FlowerCount;
        public int FollowCount;
        public int FollowStat;
        public int FrameId;
        public String FrameUrl;
        public String FundActionUrl;
        public String HeadImage;
        public int IsColumnMaster;
        public String NickName;
        public int PrivacyStat;
        public int RankType;
        public int UserId;
        public int UserType;
        public String UserTypeName;
    }

    public static final class YueLiType {
        public String Tips;
        public String Url;
    }
}
