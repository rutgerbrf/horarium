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

package nl.viasalix.horarium.ui.module.installation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ModuleInstallationViewModel : ViewModel() {
    val proceedButtonText = MutableLiveData<String>()
    val progress = MutableLiveData<Int>().also { it.value = 0 }
    val progressIndeterminate = MutableLiveData<Boolean>().also { it.value = true }
}