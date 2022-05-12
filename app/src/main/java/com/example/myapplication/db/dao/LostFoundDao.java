package com.example.myapplication.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.db.bean.LostFoundBean;

import java.util.List;


@Dao
public interface LostFoundDao {
    @Insert
    void insert(LostFoundBean lostFoundBean);


    @Update
    void update(LostFoundBean lostFoundBean);

    @Delete
    void delete(LostFoundBean lostFoundBean);
    @Query("select * from lostfoundbean")
    List<LostFoundBean> getAllData();
}
