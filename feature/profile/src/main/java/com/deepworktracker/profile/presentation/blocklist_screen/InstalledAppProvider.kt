package com.deepworktracker.profile.presentation.blocklist_screen

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class InstalledApp(val packageName: String, val label: String)

@Singleton
class InstalledAppProvider @Inject constructor(
    @ApplicationContext private val context: Context
){
    /** Launchable apps only, minus ourselves. Runs on IO — this is slow (~100+ apps). */
    suspend fun loadLaunchableApps(): List<InstalledApp> = withContext(Dispatchers.IO){
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        pm.queryIntentActivities(intent, 0).mapNotNull { ri ->
            val pkg = ri.activityInfo?.packageName ?: return@mapNotNull null
            if(pkg == context.packageName) return@mapNotNull null
            InstalledApp(pkg, ri.loadLabel(pm).toString())
        }
            .distinctBy { it.packageName }
            .sortedBy { it.label.toString() }
    }
}