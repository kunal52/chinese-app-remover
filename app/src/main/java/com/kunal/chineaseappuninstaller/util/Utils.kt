package com.kunal.chineaseappuninstaller.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import com.kunal.chineaseappuninstaller.model.AppModel

fun getInstalledApps(
    isIncludeSystemApps: Boolean,
    packageManager: PackageManager
): ArrayList<AppModel> {
    val apps: ArrayList<AppModel> = ArrayList()
    val packs = packageManager.getInstalledPackages(0)
    for (i in packs.indices) {
        val p = packs[i]

        if (isIncludeSystemApps) {
            val appName = p.applicationInfo.loadLabel(packageManager).toString()
            val icon = p.applicationInfo.loadIcon(packageManager)
            val packages = p.applicationInfo.packageName
            apps.add(AppModel(appName, icon, packages))
            Log.d("NAME", appName)
        } else
            if (!isSystemPackage(p)) {
                val appName = p.applicationInfo.loadLabel(packageManager).toString()
                val icon = p.applicationInfo.loadIcon(packageManager)
                val packages = p.applicationInfo.packageName
                apps.add(AppModel(appName, icon, packages))
                Log.d("NAME", appName)
            }
    }
    return apps
}

private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
    return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
}