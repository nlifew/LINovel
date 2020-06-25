package cn.nlifew.xqdreader.network;

import androidx.annotation.IntDef;

import java.util.Map;

import cn.nlifew.xqdreader.bean.AddBookMarkBean;
import cn.nlifew.xqdreader.bean.BookShelfBean;
import cn.nlifew.xqdreader.bean.ChapterListBean;
import cn.nlifew.xqdreader.bean.CommentBean;
import cn.nlifew.xqdreader.bean.GroupBean;
import cn.nlifew.xqdreader.bean.category.CategoryBean;
import cn.nlifew.xqdreader.bean.category.CategoryBookBean;
import cn.nlifew.xqdreader.bean.login.LoginBean;
import cn.nlifew.xqdreader.bean.login.LoginBean_V2;
import cn.nlifew.xqdreader.bean.NovelBean;
import cn.nlifew.xqdreader.bean.ranking.RankingCategoryBean;
import cn.nlifew.xqdreader.bean.ranking.RankingGroupBean;
import cn.nlifew.xqdreader.bean.ranking.RankingListBean;
import cn.nlifew.xqdreader.bean.login.SmsBean;
import cn.nlifew.xqdreader.bean.SquareBean;
import cn.nlifew.xqdreader.bean.TopBookMarkBean;
import cn.nlifew.xqdreader.bean.user.UserBean;
import cn.nlifew.xqdreader.bean.user.UserBooksBean;
import cn.nlifew.xqdreader.bean.user.UserChapterReviewBean;
import cn.nlifew.xqdreader.bean.user.UserCircleReviewBean;
import cn.nlifew.xqdreader.utils.HeaderInterceptor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IRequest {

    @GET("/argus/api/v1/booksquare/getsquarepage")
    Call<SquareBean> getSquarePage(@Query("pageId") String id,
                                   @Query("siteType") int type);


    @GET("/argus/api/v2/bookdetail/get")
    Call<NovelBean> getNovelById(@Query("bookId") String bookId,
                                 @Query("cbid") int cbid,
                                 @Query("isOutBook") String isOutBook);

    @GET("/argus/api/v1/circle/getcirclepostlist")
    Call<CommentBean> getNovelComment(@Query("circleId") long circleId,
                                      @Query("sortType") int sortType,
                                      @Query("postCategoryId") int postCategoryId,
                                      @Query("bookId") String bookId,
                                      @Query("bookType") int bookType,
                                      @Query("pg") int pg,
                                      @Query("pz") int pz);

    @GET("/argus/api/v1/booksquare/getbookitemlist")
    Call<GroupBean> getGroupById(@Query("itemId") String id);

    @GET("/argus/api/v1/booksquare/getbookitemlist")
    Call<GroupBean> getGroupById(@Query("itemId") String id, @Query("pageIndex") int pageId);

    @FormUrlEncoded
    @POST("https://ptlogin.qidian.com/sdk/staticlogin")
    @Headers({  HeaderInterceptor.IGNORE_ADD_HEADER + ": 1",
                "User-Agent: okhttp/3.12.6",
                "referer: http://android.qidian.com"})
    @Deprecated
    Call<LoginBean> loginByPassword(@FieldMap Map<String, String> body);


    @FormUrlEncoded
    @POST("https://ptlogin.qidian.com/sdk/sendphonecode")
    @Headers({
            HeaderInterceptor.IGNORE_ADD_HEADER + ": 1",
            "User-Agent: okhttp/3.12.6",
            "referer: http://android.qidian.com"
    })
    Call<SmsBean> sendSmsCode(@FieldMap Map<String, String> body);


    @FormUrlEncoded
    @POST("https://ptlogin.qidian.com/sdk/phonecodelogin")
    @Headers({
            HeaderInterceptor.IGNORE_ADD_HEADER + ": 1",
            "User-Agent: okhttp/3.12.6",
            "referer: http://android.qidian.com"
    })
    Call<LoginBean> loginBySms(@FieldMap Map<String, String> body);


    @FormUrlEncoded
    @POST("/Atom.axd/Api/User/LoginValidate")
    Call<LoginBean_V2> getLoginBean_V2(@FieldMap Map<String, String> body);


    @GET("/argus/api/v1/chapterlist/chapterlist")
    Call<ChapterListBean> getChapterList(@Query("bookId") String bookId,
                                         @Query("timeStamp") long timeStamp,
                                         @Query("requestSource") int requestSource,
                                         @Query("md5Signature") String md5Signature);


    @FormUrlEncoded
    @POST("/Atom.axd/Api/BookMark/GetTopList")
    Call<TopBookMarkBean> getTopBookMarkList(@Field("bookId") String bookId);

    @FormUrlEncoded
    @POST("/Atom.axd/Api/BookMark/Add")
    Call<AddBookMarkBean> addBookMark(@FieldMap Map<String, String> body);


    @GET("/argus/api/v1/bookcontent/safegetcontent")
    Call<ResponseBody> getChapterContent(@Query("bookId") String bookId,
                                         @Query("ChapterId") int chapterId,
                                         @Query("fineLayout") int fineLayout);



    @GET("/argus/api/v1/rankCategory/get")
    Call<RankingCategoryBean> getRankingCategory();


    @GET("/argus/api/v1/rankGroup/get")
    Call<RankingGroupBean> getRankingGroup(@Query("site") int siteId,
                                           @Query("categoryId") int categoryId);

    @GET("/argus/api/v1/topBooks/get")
    Call<RankingListBean> getRankingList(@Query("site") int siteId,
                                         @Query("topId") int groupId,
                                         @Query("categoryId") int categoryId,
                                         @Query("pageIndex") int pageIndex,
                                         @Query("pageSize") int pageSize);

    @GET("/argus/api/v3/user/getuserpageinfo")
    Call<UserBean> getUserPageInfo(@Query("userId") String userId,
                                   @Query("authorId") String authorId);

    @GET("/argus/api/v1/author/getauthorbooks")
    Call<UserBooksBean> getUserBooks(@Query("authorId") String authorId);


    @GET("/argus/api/v1/chapterreview/getuserchapterreview")
    Call<UserChapterReviewBean> getUserChapterReview(@Query("userId") String userId,
                                                     @Query("pageIndex") int pageIndex);

    @GET("/argus/api/v2/circle/getusercirclereviewlist")
    Call<UserCircleReviewBean> getUserCircleReview(@Query("userId") String userId,
                                                   @Query("pg") int page,
                                                   @Query("pz") int count);

    @GET("/argus/api/v1/bookstore/getbookstoreconf")
    Call<CategoryBean> getCategory(@Query("siteId") String siteId);


    @GET("/argus/api/v1/bookstore/getbooks")
    Call<CategoryBookBean> getCategoryBooks(@Query("pageIndex") int pageIndex,
                                            @Query("pageSize") int pageSize,
                                            @Query("siteId") String siteId,
                                            @Query("filters") String filters,
                                            @Query("order") String order);


    @FormUrlEncoded
    @POST("/argus/api/v1/bookshelf/refresh")
    Call<BookShelfBean> refreshBookShelf(@Field("caseinfo") String info, @Field("lastsynctime") long lastSyncTime);
}
