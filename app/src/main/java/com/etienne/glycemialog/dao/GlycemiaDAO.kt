package com.etienne.glycemialog.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.etienne.glycemialog.dao.DatabaseHandler.Companion.GLYCEMIA_LOG_DATETIME
import com.etienne.glycemialog.dao.DatabaseHandler.Companion.GLYCEMIA_LOG_ID
import com.etienne.glycemialog.dao.DatabaseHandler.Companion.GLYCEMIA_LOG_LEVEL
import com.etienne.glycemialog.dao.DatabaseHandler.Companion.GLYCEMIA_LOG_TABLE_NAME
import com.etienne.glycemialog.models.GlycemiaLog
import com.etienne.glycemialog.utils.FRENCH_DATETIME_FORMAT
import java.text.SimpleDateFormat
import java.util.*

class GlycemiaDAO(context: Context) : DAOBase(context) {

    fun getLogs() : ArrayList<GlycemiaLog> {
        val logs: ArrayList<GlycemiaLog> = arrayListOf()

        getDB()?.let {

            val c: Cursor = it.query(GLYCEMIA_LOG_TABLE_NAME, null, null, null, null, null, "$GLYCEMIA_LOG_DATETIME DESC, $GLYCEMIA_LOG_ID DESC")

            val sdf = SimpleDateFormat(FRENCH_DATETIME_FORMAT, Locale.FRANCE)

            while (c.moveToNext()) {
                logs.add(
                    GlycemiaLog(
                        c.getInt(c.getColumnIndexOrThrow(GLYCEMIA_LOG_ID)),
                        c.getFloat(c.getColumnIndexOrThrow(GLYCEMIA_LOG_LEVEL)),
                        sdf.parse(c.getString(c.getColumnIndexOrThrow(GLYCEMIA_LOG_DATETIME))))
                )
            }
            c.close()
        }

        return logs
    }

    fun getLastLog() : GlycemiaLog? {

        var log: GlycemiaLog? = null

        getDB()?.let {
            val c: Cursor = it.query(GLYCEMIA_LOG_TABLE_NAME, null, null, null, null, null, "$GLYCEMIA_LOG_ID DESC", "1")

            if(c.moveToFirst()) {

                val sdf = SimpleDateFormat(FRENCH_DATETIME_FORMAT, Locale.FRANCE)

                log = GlycemiaLog(
                    c.getInt(c.getColumnIndexOrThrow(GLYCEMIA_LOG_ID)),
                    c.getFloat(c.getColumnIndexOrThrow(GLYCEMIA_LOG_LEVEL)),
                    sdf.parse(c.getString(c.getColumnIndexOrThrow(GLYCEMIA_LOG_DATETIME)))
                )
            }

            c.close()
        }

        return log
    }

    fun insertLog(level: Float, dateTime: String) {

        val values = ContentValues().apply {
            put(GLYCEMIA_LOG_LEVEL, level)
            put(GLYCEMIA_LOG_DATETIME, dateTime)
        }

        getDB()?.insert(GLYCEMIA_LOG_TABLE_NAME, null, values)
    }

    fun deleteLog(log: GlycemiaLog) {
        getDB()?.delete(GLYCEMIA_LOG_TABLE_NAME, "$GLYCEMIA_LOG_ID = ?", arrayOf(log.id.toString()))
    }
}