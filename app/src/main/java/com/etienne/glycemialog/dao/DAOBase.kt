package com.etienne.glycemialog.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase

abstract class DAOBase(context: Context) {

    private var mDatabase: SQLiteDatabase? = null
    private var mDatabaseHandler: DatabaseHandler? = null

    init {
        mDatabaseHandler = DatabaseHandler(context, DB_NAME, DB_VERSION)
    }

    private fun open() {
        mDatabase = mDatabaseHandler?.writableDatabase
    }

    protected fun getDB() : SQLiteDatabase? {
        if(mDatabase == null) {
            open()
        }

        return mDatabase
    }

    companion object {
        const val DB_VERSION: Int = 1
        const val DB_NAME: String = "db_glycemialog.db"
    }
}