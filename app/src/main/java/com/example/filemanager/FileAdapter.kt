package com.example.filemanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.io.File

class FileAdapter(
    private val files: Array<File>,
    private val context: Context
) : BaseAdapter() {
    override fun getCount(): Int {
        return files.size
    }

    override fun getItem(position: Int): Any {
        return files[position];
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView
            ?: LayoutInflater
                .from(context)
                .inflate(R.layout.file_in_main, parent, false)

        val type = (if (files[position].isFile) "File: " else "Folder: ") + files[position].name
        view.findViewById<TextView>(R.id.textView).text = type;

        return view;
    }
}