package com.kunal.chineaseappuninstaller

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kunal.chineaseappuninstaller.adpater.AppListAdapter
import com.kunal.chineaseappuninstaller.api.DataDownload
import com.kunal.chineaseappuninstaller.api.DataDownload.DownloadListener
import com.kunal.chineaseappuninstaller.model.AppModel
import com.kunal.chineaseappuninstaller.util.ONE_HOUR_IN_MILLISECOND
import com.kunal.chineaseappuninstaller.util.SharedPreferenceUtil
import com.kunal.chineaseappuninstaller.util.getInstalledApps
import com.opencsv.CSVReader
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileReader
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.simpleName

    lateinit var appListAdapter: AppListAdapter
    lateinit var uninstallReceiver: BroadcastReceiver
    var recyclerViewAppList = ArrayList<AppModel>()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progress_bar.show()
        if (!checkFileExist() || SharedPreferenceUtil.getLastUpdatedTime(this) + ONE_HOUR_IN_MILLISECOND < System.currentTimeMillis())
            DataDownload(this, object : DownloadListener {
                override fun onCompleted() {
                    progress_bar.hide()
                    SharedPreferenceUtil.saveLastUpdatedTime(
                        this@MainActivity,
                        System.currentTimeMillis()
                    )
                }

                override fun onError() {
                    progress_bar.hide()
                }

                override fun onStarted() {
                    progress_bar.show()
                }
            }).downloadSingleFile()

        appListAdapter = AppListAdapter(this, ArrayList())
        appListRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = appListAdapter
        }

        Observable.just(1)
            .map {
                getInstalledApps(false, packageManager)
            }
            .doOnSubscribe { progress_bar.show() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                recyclerViewAppList = it
                appListAdapter.listChanged(it)
                progress_bar.hide()
            }

        uninstallReceiver = UninstallerReceiver(appListAdapter, no_chinese_app_textView)
        registerReceiver(uninstallReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        })

        scanButton.setOnClickListener {
            val chineseInstalledList = ArrayList<AppModel>()
            val chineseAppsList = readingCSV()
            Log.d("Apps", chineseAppsList.toString())
            val allInstalledApps = getInstalledApps(false, packageManager)
            allInstalledApps.forEach {
                if (chineseAppsList.contains(it.packages))
                    chineseInstalledList.add(it)
            }



            Observable.just(1)
                .delay(2, TimeUnit.SECONDS)
                .doOnSubscribe { progress_bar.show() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    recyclerViewAppList = chineseInstalledList
                    appListAdapter.listChanged(recyclerViewAppList)
                    progress_bar.hide()
                    if (chineseInstalledList.size == 0)
                        no_chinese_app_textView.visibility = View.VISIBLE
                    else
                        no_chinese_app_textView.visibility = View.GONE
                }
        }
    }


    class UninstallerReceiver(var appListAdapter: AppListAdapter, var showTextMessage: View) :
        BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            appListAdapter.removeItem(intent!!.dataString!!)
            if (appListAdapter.appList.size == 0)
                showTextMessage.visibility = View.VISIBLE
            else
                showTextMessage.visibility = View.GONE
        }
    }

    private fun readingCSV(): ArrayList<String> {
        val chinesePackageList = ArrayList<String>()
        try {
            val csvFileDownload =
                File(filesDir, "app-list.csv")
            val inputStreamReader = FileReader(csvFileDownload)
            val reader = CSVReader(inputStreamReader)
            reader.skip(1)
            var nextLine: Array<String>
            while (reader.readNext().also { nextLine = it } != null) {
                chinesePackageList.add(nextLine[1])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return chinesePackageList
    }


    private fun checkFileExist(): Boolean {
        val csvFileDownload =
            File(filesDir, "app-list.csv")
        if (csvFileDownload.exists())
            return true
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(uninstallReceiver)
    }
}
