package com.etienne.glycemialog.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.etienne.glycemialog.R
import com.etienne.glycemialog.activities.MainActivity
import com.etienne.glycemialog.dao.GlycemiaDAO
import com.etienne.glycemialog.models.GlycemiaLog
import com.etienne.glycemialog.utils.FRENCH_DATETIME_FORMAT
import com.etienne.glycemialog.utils.getDPValue
import kotlinx.android.synthetic.main.item_log.view.*
import java.text.SimpleDateFormat
import java.util.*

class LogAdapter(var context: Context, var logs: ArrayList<GlycemiaLog>) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    private val mSDF = SimpleDateFormat(FRENCH_DATETIME_FORMAT, Locale.FRANCE)
    private val mCheckedPositions: HashMap<Int, Boolean> = hashMapOf()
    private var mSelectionModeEnabled = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_log, parent, false))
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    override fun onBindViewHolder(holder: LogAdapter.ViewHolder, position: Int) {
        val log = logs[position]

        holder.tvDateTime.text = mSDF.format(log.dateTime)

        holder.tvGlycemiaLevel.text = log.level.toString()

        holder.cbSelected.visibility = if(mSelectionModeEnabled) View.VISIBLE else View.GONE
        holder.cbSelected.isChecked = mCheckedPositions.containsKey(position) && mCheckedPositions[position]!!
    }

    fun addLog(log: GlycemiaLog?) {
        log?.let{
            logs.add(0, it)
            notifyItemInserted(0)
        }
    }

    fun disableSelectionMode() {
        mCheckedPositions.clear()
        mSelectionModeEnabled = false
        notifyDataSetChanged()

        (context as? MainActivity)?.setSelectionMode(false)
    }

    fun removeSelectedItems(dao: GlycemiaDAO) {
        val rmLogs: ArrayList<GlycemiaLog> = arrayListOf()

        for ((position, isChecked) in mCheckedPositions) {
            if (isChecked) {
                val log = logs[position]
                dao.deleteLog(log)
                rmLogs.add(log)
            }
        }

        logs.removeAll(rmLogs)

        disableSelectionMode()
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val tvDateTime: TextView = v.tvDateTimeLog
        val tvGlycemiaLevel: TextView = v.tvGlycemiaLevel
        val cbSelected: CheckBox = v.cbSelectLog

        init {
            tvGlycemiaLevel.typeface = Typeface.DEFAULT_BOLD

            val drop = ContextCompat.getDrawable(context, R.drawable.drop)
            drop?.let {
                val side = getDPValue(context, 24f)
                it.setBounds(0,0, side, side)

                tvGlycemiaLevel.setCompoundDrawablesRelative(null, null, drop, null)
            }

            cbSelected.setOnCheckedChangeListener { _, isChecked ->
                mCheckedPositions[adapterPosition] = isChecked

                val selectedItemsCount = getSelectedItemsCount()
                (context as? MainActivity)?.supportActionBar?.title = String.format("%d %s%s", selectedItemsCount, context.getString(R.string.item), if(selectedItemsCount > 1) "s" else "")

                if(selectedItemsCount == 0) {
                    disableSelectionMode()
                }
            }

            itemView.setOnClickListener {
                if(mSelectionModeEnabled) {
                    cbSelected.performClick()
                }
            }

            itemView.setOnLongClickListener {
                mSelectionModeEnabled = true
                mCheckedPositions[adapterPosition] = true
                notifyDataSetChanged()

                (context as? MainActivity)?.setSelectionMode(true)

                false
            }
        }
    }

    private fun getSelectedItemsCount(): Int {
        return Collections.frequency(mCheckedPositions.values, true)
    }
}