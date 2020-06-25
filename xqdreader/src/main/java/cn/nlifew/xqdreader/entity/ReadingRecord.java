package cn.nlifew.xqdreader.entity;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cn.nlifew.xqdreader.database.CommonDatabase;

@Entity
public class ReadingRecord {


    @PrimaryKey(autoGenerate = true)
    public long id;

    public int bookId;
    public String bookName;

    public int chapterId;
    public String chapterName;

    public long time;

    public void save() {
        CommonDatabase db = CommonDatabase.getInstance();
        Helper helper = db.getReadingRecordHelper();
        helper.insert(this);
    }

    public void delete() {
        CommonDatabase db = CommonDatabase.getInstance();
        Helper helper = db.getReadingRecordHelper();
        helper.delete(this);
    }

    @Dao
    public interface Helper {

        @Query("SELECT * FROM ReadingRecord WHERE bookId = :bookId")
        List<ReadingRecord> findRecordByBookId(int bookId);

        @Query("SELECT * FROM ReadingRecord ORDER BY time DESC LIMIT :pageIndex,:pageLimit")
        List<ReadingRecord> findRecordList(int pageIndex, int pageLimit);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(ReadingRecord record);

        @Delete
        void delete(ReadingRecord record);

        @Query("DELETE FROM ReadingRecord")
        void deleteAll();

        @Query("SELECT COUNT (*) FROM ReadingRecord")
        int getCount();
    }
}
