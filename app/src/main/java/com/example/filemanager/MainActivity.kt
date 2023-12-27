package com.example.filemanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.AdapterView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.filemanager.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // request authorities
        if (Build.VERSION.SDK_INT < 30) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                Log.v("TAG", "Requesting Permission < 30")
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1234)
            } else
                Log.v("TAG", "Permission is already OK")
        } else {
            if (!Environment.isExternalStorageManager()) {
                Log.v("TAG", "Requesting Permission >= 30")
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            } else {
                Log.v("TAG", "Permission is already OK")
            }
        }

        // read all
        val path = Environment.getExternalStorageDirectory().path


        bind.button.setOnClickListener {
            val pathContent: Array<File> = this.readPath(path)!!;
            setPath(bind, path, pathContent);
        }
    }

    private fun setPath(bind: ActivityMainBinding, path: String, pathContent: Array<File>) {
        bind.filePath.text = path;
        bind.fileList.adapter = FileAdapter(pathContent, this);

        bind.fileList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            run {
                val selected = pathContent[position];
                if (selected.isDirectory) {
                    val newPath = selected.absolutePath;
                    val newPathContent = this.readPath(newPath)!!;

                    setPath(bind, newPath, newPathContent);
                } else {
                    bind.textView.text = "";
                    bind.imageView.setImageResource(0);

                    // check file type
                    when (selected.extension) {
                        "txt" -> {
                            val reader = selected.inputStream().reader();
                            val text = reader.readText();
                            reader.close()
                            Log.v("TAG", text)
                            bind.textView.text = text;
                        }
                        in arrayOf("BMP", "JPG", "PNG") -> {
                            loadAndDisplayImage(selected, bind.imageView)
                        }
                    }
                }
            }
        }
    }

    private fun loadAndDisplayImage(file: File, imageView: ImageView) {
        Glide.with(this)
            .load(file)
            .into(imageView)
    }

    private fun readPath(path: String): Array<File>? {
        val openedPath = File(path);
        return openedPath.listFiles();
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("TAG", "Permission Granted")
        } else {
            Log.v("TAG", "Permission Denied")
        }
    }
}