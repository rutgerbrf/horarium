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

package nl.viasalix.horarium.module.calvijncollege.cup

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PlainCookieJar : CookieJar {

    var cookies: MutableList<Cookie> = ArrayList()

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        this.cookies = cookies
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        return ArrayList()
    }
}