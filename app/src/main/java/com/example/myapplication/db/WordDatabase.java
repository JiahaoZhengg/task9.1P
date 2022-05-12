package com.example.myapplication.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.myapplication.db.bean.LostFoundBean;
import com.example.myapplication.db.dao.LostFoundDao;


@Database(entities = {LostFoundBean.class}, version = 2, exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {

    public abstract LostFoundDao getLostFoundDao();




}
