/*
 * Copyright 2018 Jochem Broekhoff
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

package nl.viasalix.horarium.module

import android.app.Activity
import android.content.SharedPreferences
import nl.viasalix.horarium.events.UserEvents

open class HorariumUserModule {

    /**
     * Initialize the module, performed upon module instantiation.
     * Before this method is called, the user storage container will be ready.
     *
     * Called every time the user session loads. It is recommended that you bind the hooks here.
     *
     * Note: this function will be called from a background thread. UI operations are not safe (and
     * not recommended in the [init] method anyway).
     *
     * @param moduleSp [SharedPreferences] container for this module to store settings.
     */
    open fun init(moduleSp: SharedPreferences, eventsProvider: UserEvents) {}

    /**
     * Provide the [Activity] class for the setup activity.
     *
     * The started intent will contain two keys set as extras:
     * 1. `moduleSharedPreferencesKey`: (String) Name of the shared preferences container which you can use. It is
     * specific to the user for which the [Activity] is being launched. You should commit changes to this container
     * before finishing the activity.
     * 2. `setupCompleteId`: (String) Identifier to be used to invoke the setup complete method. When the setup is
     * complete, you should call [ModuleManager.completeSetup] with the value of this extra as parameter.
     *
     * @return Return `null` to indicate that no setup activity should be started.
     */
    open fun provideSetupActivityClass(): Class<out Activity>? = null

    /**
     * Stop the module gracefully. Must not reset anything.
     *
     * Called when the user switches to a different account.
     */
    open fun stop() {}

    /**
     * Perform a cleanup. Called when an account is removed (e.g. upon sign out).
     */
    open fun cleanup() {}
}