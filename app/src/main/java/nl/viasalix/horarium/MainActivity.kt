/*
 * Copyright 2018 Rutger Broekhoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.viasalix.horarium

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.material.bottomappbar.BottomAppBar
import nl.viasalix.horarium.events.UserEvents
import nl.viasalix.horarium.module.ModuleManager
import nl.viasalix.horarium.ui.drawer.BottomDrawer
import nl.viasalix.horarium.ui.main.ScheduleFragment
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.doAsync
import android.app.AlarmManager
import androidx.core.content.ContextCompat.getSystemService
import android.app.PendingIntent



class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "Horarium/Main"
    }

    private lateinit var userSp: SharedPreferences
    private val userEvents = UserEvents()

    override fun onCreate(savedInstanceState: Bundle?) {
        val currentUser = defaultSharedPreferences.getString(getString(R.string.SP_KEY_CURRENT_USER), null)

        if (!defaultSharedPreferences.contains(getString(R.string.SP_KEY_USERS)) || currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))

            finish()
            super.onCreate(savedInstanceState)
            return
        }

        userSp = getSharedPreferences(currentUser, Context.MODE_PRIVATE)

        Log.d(TAG, "Checking modules state...")

        var installationPrompted = false
        if (ModuleManager.mustPromptModuleInstallation(this, userSp)) {
            val availableModules = ModuleManager.listAvailableModules(this, userSp)
            val activeModules = ModuleManager.listActiveModules(this, userSp)

            startActivity(
                Intent(this, ModuleInstallationActivity::class.java)
                    .putStringArrayListExtra("availableModules", ArrayList(availableModules))
                    .putStringArrayListExtra("activeModules", ArrayList(activeModules))
                    .putExtra("userSpName", currentUser)
            )

            installationPrompted = true
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ScheduleFragment.newInstance(userEvents))
                .commitNow()
        }

        setSupportActionBar(findViewById(R.id.bottomAppBar))

        if (!installationPrompted) {
            initializeModuleAsync()
        }

        findViewById<BottomAppBar>(R.id.bottomAppBar).setNavigationOnClickListener { _ ->
            BottomDrawer().showNow(supportFragmentManager, "bottom_drawer")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()

        val installationState = userSp.getInt(getString(R.string.SP_KEY_MODULE_INSTALLATION_STATE), -1)

        if (installationState > -1)
            userSp.edit (commit = true) {
                putBoolean(getString(R.string.SP_KEY_MODULES_PROMPTED), true)
                putInt(getString(R.string.SP_KEY_MODULE_INSTALLATION_STATE), -1)
            }

        when (installationState) {
            ModuleInstallationActivity.STATE_SKIPPED -> {
                // Skipped
            }
            ModuleInstallationActivity.STATE_DONE_NOTHING_DOWNLOADED -> {
                initializeModuleAsync()
            }
            ModuleInstallationActivity.STATE_DONE_MODULES_DOWNLOADED -> {
                HorariumApplication.restart(this)
            }
        }
    }

    private fun initializeModuleAsync() {
        doAsync {
            val act = this.weakRef.get()
            if (act != null)
                ModuleManager.initializeModules(act, userSp, userEvents)
        }
    }
}
