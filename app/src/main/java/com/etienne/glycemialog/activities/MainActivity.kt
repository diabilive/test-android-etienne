package com.etienne.glycemialog.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.etienne.glycemialog.R
import com.etienne.glycemialog.adapters.LogAdapter
import com.etienne.glycemialog.dao.GlycemiaDAO
import com.etienne.glycemialog.utils.FRENCH_DATETIME_FORMAT
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_log_glycemia.view.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val mDao = GlycemiaDAO(this)

    private var mCalendar: Calendar? = null

    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0

    private var mIsDatePickerActive = false

    private val mSDF: SimpleDateFormat = SimpleDateFormat(FRENCH_DATETIME_FORMAT, Locale.FRANCE)

    private var mSelectionModeActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initActionBar()
        initComponents()
    }

    private fun initActionBar() {
        setSupportActionBar(tbLogList)

        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(true)
            it.setDisplayUseLogoEnabled(false)
            it.title = getString(R.string.title_glycemia_logs)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(mSelectionModeActive) {
            menuInflater.inflate(R.menu.menu_selection, menu)
        } else {
            menuInflater.inflate(R.menu.menu_empty, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if(item?.itemId == R.id.action_delete) {
            (rvLogs.adapter as LogAdapter).removeSelectedItems(mDao)
        } else if(item?.itemId == android.R.id.home) {
            (rvLogs.adapter as LogAdapter).disableSelectionMode()
        }

        return super.onOptionsItemSelected(item)
    }

    fun setSelectionMode(enabled: Boolean) {
        mSelectionModeActive = enabled

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(enabled)
            it.title = getString(R.string.title_glycemia_logs)
        }

        invalidateOptionsMenu()
    }

    private fun initComponents() {

        val itemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.divider_recycler_view)?.let {
            itemDecoration.setDrawable(it)
        }

        rvLogs.layoutManager = LinearLayoutManager(this)
        rvLogs.addItemDecoration(itemDecoration)
        rvLogs.adapter = LogAdapter(this, mDao.getLogs())

        rvLogs.addOnScrollListener(OnScrollListener())

        fabAddGlycemia.setOnClickListener {
            promptUserForInput()
        }
    }

    private fun promptUserForInput() {
        val dialog = AlertDialog.Builder(this).create()

        val v = View.inflate(this, R.layout.dialog_log_glycemia, null)
        dialog.setView(v)

        v.tvTitleInputLog.typeface = Typeface.DEFAULT_BOLD

        v.tvDateTimeInputLog.text = mSDF.format(Date())
        v.tvDateTimeInputLog.setOnClickListener {
            if(!mIsDatePickerActive) {
                showDatePicker(it)
            }
        }

        v.btCancelInputLog.setOnClickListener {
            dialog.dismiss()
        }

        v.btSaveInputLog.setOnClickListener {
            if(validateForm(v)) {
                mDao.insertLog(v.etInputLog.text.toString().toFloat(), v.tvDateTimeInputLog.text.toString())
                refreshLogList()
                dialog.dismiss()
            }
        }

        dialog.show()

        dialog.window?.let {
            it.setBackgroundDrawableResource(android.R.color.transparent)
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    private fun showDatePicker(viewToUpdate: View) {
        mIsDatePickerActive = true

        mCalendar = Calendar.getInstance()
        mCalendar?.let {

            mYear = it.get(Calendar.YEAR)
            mMonth = it.get(Calendar.MONTH)
            mDay = it.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    mYear = year
                    mMonth = monthOfYear
                    mDay = dayOfMonth
                    showTimePicker(viewToUpdate)
                }, mYear, mMonth, mDay
            )

            datePickerDialog.show()

            datePickerDialog.setOnDismissListener {
                mIsDatePickerActive = false
            }
        }
    }

    private fun showTimePicker(viewToUpdate: View) {

        mCalendar?.let {
            mHour = it.get(Calendar.HOUR_OF_DAY)
            mMinute = it.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    mHour = hourOfDay
                    mMinute = minute

                    val calendar = Calendar.getInstance()
                    calendar.set(mYear, mMonth, mDay, mHour, mMinute)

                    (viewToUpdate as? TextView)?.text = mSDF.format(calendar.time)

                }, mHour, mMinute, true
            )
            timePickerDialog.show()
        }
    }


    private fun refreshLogList() {
        (rvLogs.adapter as LogAdapter).addLog(mDao.getLastLog())
        rvLogs.scrollToPosition(0)
    }

    private fun validateForm(v: View): Boolean {
        var valid = true

        val text = v.etInputLog.text.toString()

        if(text.toFloatOrNull() == null) {
            valid = false
            v.tvErrorInputLog.visibility = View.VISIBLE
        }

        return valid
    }

    private inner class OnScrollListener : RecyclerView.OnScrollListener() {
        @SuppressLint("RestrictedApi")
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            fabAddGlycemia.visibility = when(newState) {
                RecyclerView.SCROLL_STATE_IDLE -> {
                    View.VISIBLE
                }
                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    View.GONE
                }
                else -> {
                    fabAddGlycemia.visibility
                }
            }
        }
    }
}
