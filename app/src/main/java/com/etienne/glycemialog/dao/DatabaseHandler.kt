package com.etienne.glycemialog.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(GLYCEMIA_LOG_TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    companion object {
        const val GLYCEMIA_LOG_TABLE_NAME = "GLYCEMYA_LOG"

        const val GLYCEMIA_LOG_ID = "GL_ID"
        const val GLYCEMIA_LOG_LEVEL = "GL_LEVEL"
        const val GLYCEMIA_LOG_DATETIME = "GL_DATETIME"

        private const val GLYCEMIA_LOG_TABLE_CREATE =
                """CREATE TABLE IF NOT EXISTS $GLYCEMIA_LOG_TABLE_NAME (
                        $GLYCEMIA_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        $GLYCEMIA_LOG_LEVEL NUMERIC NOT NULL,
                        $GLYCEMIA_LOG_DATETIME TEXT NOT NULL);"""
    }
}