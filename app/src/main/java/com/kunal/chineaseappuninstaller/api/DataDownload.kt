package com.kunal.chineaseappuninstaller.api

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import java.io.*

class DataDownload(private val context: Context, private val listener: DownloadListener) {

    private val TAG = DataDownload::class.java.simpleName

    interface DownloadListener {
        fun onCompleted()
        fun onError()
        fun onStarted()
    }

    @SuppressLint("CheckResult")
    fun downloadSingleFile() {
        listener.onStarted()
        NetworkApi.create()
            .downloadFileWithDynamicUrlSync()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { listener.onCompleted() }
            .subscribe({
                writeResponseBodyToDisk(
                    it.body()!!,
                    getFileNameFromUrl(it.raw().request().url())
                )
            }, {
                it.printStackTrace()
                listener.onError()
            })
    }

    private fun checkFileExist(): Boolean {
        val csvFileDownload =
            File(context.filesDir, "app-list.csv")
        if (csvFileDownload.exists())
            return true
        return false
    }

    private fun getFileNameFromUrl(url: HttpUrl): String {
        val encodedPathSegments = url.encodedPathSegments()
        return encodedPathSegments[encodedPathSegments.size - 1]
    }

    private fun writeResponseBodyToDisk(body: ResponseBody, fileName: String): Boolean {
        return try {
            val csvFileDownload =
                File(context.filesDir, fileName)
            if (!csvFileDownload.exists())
                csvFileDownload.createNewFile()
            else {
                csvFileDownload.delete()
                csvFileDownload.createNewFile()
            }

            Log.d("File", csvFileDownload.path)
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(csvFileDownload)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }


}