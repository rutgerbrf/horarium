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

package nl.viasalix.horarium.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nl.viasalix.horarium.data.AppointmentCustomizations
import java.util.*

class RoomTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun stringToStringList(data: String?): List<String> {
        if (data == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun stringListToString(data: List<String>?): String? {
        return gson.toJson(data)
    }

    @TypeConverter
    fun dateToLong(date: Date): Long =
            date.time

    @TypeConverter
    fun longToDate(date: Long) =
            Date(date)

    @TypeConverter
    fun appointmentCustomizationsToString(appointmentCustomizations: AppointmentCustomizations): String {
        return gson.toJson(appointmentCustomizations)
    }

    @TypeConverter
    fun stringToAppointmentCustomizations(appointmentCustomizations: String): AppointmentCustomizations {
        return gson.fromJson(appointmentCustomizations, AppointmentCustomizations::class.java)
    }
}
