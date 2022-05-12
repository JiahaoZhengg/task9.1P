package com.example.myapplication;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.myapplication.db.WordDatabase;


public class MyApp extends Application {
    private static MyApp instance;
    private WordDatabase wordDatabase;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);

    }

    public synchronized WordDatabase getWordDatabase() {
        if (wordDatabase == null) {
            wordDatabase = Room.databaseBuilder(this, WordDatabase.class, "word_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build();

        }
        return wordDatabase;
    }

    public static MyApp getInstance() {
        return instance;
    }
}
