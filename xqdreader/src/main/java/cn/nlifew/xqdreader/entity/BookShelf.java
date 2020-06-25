package cn.nlifew.xqdreader.entity;


import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

import cn.nlifew.xqdreader.database.CommonDatabase;

@Entity
public class BookShelf {

    public static void saveAll(BookShelf[] books) {
        CommonDatabase db = CommonDatabase.getInstance();
        Helper helper = db.getBookShiftHelper();
        helper.deleteAll();
        helper.saveAll(books);
    }

    public static BookShelf findById(int id) {
        CommonDatabase db = CommonDatabase.getInstance();
        Helper helper = db.getBookShiftHelper();
        return helper.findBookById(id);
    }

    public long Adid;
    public String Author;
    public int AuthorId;

    @PrimaryKey
    public int BookId;

    public int BookLevel;
    public int BookMode;
    public String BookName;
    public String BookStatus;
    public int CategoryId;
    public String CategoryName;
    public int CheckLevelStatus;
    public int FreeType;
    public boolean IsFirstPublish;
    public int IsJingPai;
    public int IsMemberBook;
    public int IsPublication;
    public int IsTop;
    public int IsVip;
    public long LastChapterUpdateTime;
    public int LastUpdateChapterID;
    public String LastUpdateChapterName;
    public long LastVipChapterUpdateTime;
    public int LastVipUpdateChapterId;
    public String LastVipUpdateChapterName;
    public int Sid;
    public int SourceBookId;
    public int SubCategoryId;
    public String SubCategoryName;
    public int WholeSale;


    @Dao
    public interface Helper {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void saveAll(BookShelf[] books);

        @Query("DELETE FROM BookShelf")
        void deleteAll();

        @Query("SELECT * FROM BookShelf")
        List<BookShelf> findAll();

        @Query("SELECT * FROM BookShelf WHERE BookId = :id")
        BookShelf findBookById(int id);

        @Query("DELETE FROM BookShelf WHERE BookId = :id")
        void deleteById(int id);
    }
}
