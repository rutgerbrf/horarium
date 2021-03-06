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

package nl.viasalix.horarium.module.calvijncollege.cup.method

import nl.viasalix.horarium.module.calvijncollege.cup.AspFieldMode
import nl.viasalix.horarium.module.calvijncollege.cup.CUPClient
import nl.viasalix.horarium.module.calvijncollege.cup.CUPMethod
import nl.viasalix.horarium.module.calvijncollege.cup.data.cup.model.Appointment
import nl.viasalix.horarium.module.calvijncollege.cup.data.cup.model.Choice
import nl.viasalix.horarium.module.calvijncollege.cup.data.cup.model.Option
import org.jsoup.Jsoup
import java.text.ParseException
import java.util.*

/**
 * @param result List of available [Appointment]s.
 */
class Appointments(override val success: Boolean, override val result: List<Appointment> = emptyList()) :
        CUPMethod<List<Appointment>>() {
    companion object {
        fun execute(cupClient: CUPClient): Appointments {
            if (!cupClient.checkSession()) return Appointments(false).also { it.failReason = "E_CupClient_SessionExpired" }

            // TODO: Check if successfully signed in

            val call = cupClient.createCall("RoosterForm.aspx", "GET", aspFieldMode = AspFieldMode.NONE)
            val response = call.execute()

            if (response?.body() == null) return Appointments(false).also { it.failReason = "E_Appointments_BodyNull" }

            val doc = Jsoup.parse(response.body()!!.string())
            cupClient.session.extractAspFields(doc)

            val appointments: MutableList<Appointment> = ArrayList()
            doc.select("#rooster > tbody > tr.cls0, #rooster > tbody > tr.cls1").forEach { domAppointment ->
                var startDate: Calendar? = null
                var endDate: Calendar? = null
                var slot = 0
                var selectedOption: Option? = null
                var fixed = false
                val choices: MutableList<Choice> = ArrayList()

                domAppointment.select("> td").forEachIndexed { column, domAppointmentColumn ->
                    val columnStringContent = domAppointmentColumn.text().trim()

                    when (column) {
                        0 -> {
                            // Primary start dateString
                            try {
                                startDate = Calendar.getInstance()
                                startDate!!.time = CUPClient.appointmentDateFormatter.parse(columnStringContent)
                            } catch (pe: ParseException) {
                            }
                        }
                        1 -> {
                            // Slot
                            slot = columnStringContent.substring(2).toInt()
                            // Start and end time
                            if (startDate != null) {
                                val fromToRaw = domAppointmentColumn
                                        .selectFirst("> p")
                                        .attr("onmouseover")
                                        .replace("showHelpText(\"", "")
                                        .replace(",event);", "")
                                        .split('-')
                                if (fromToRaw.size == 2) {
                                    try {
                                        val from = CUPClient.appointmentTimeFormatter.parse(fromToRaw[0])
                                        val fromCal = Calendar.getInstance().also {
                                            it.time = from
                                        }

                                        startDate?.apply {
                                            set(Calendar.HOUR, fromCal.get(Calendar.HOUR))
                                            set(Calendar.MINUTE, fromCal.get(Calendar.MINUTE))
                                        }
                                    } catch (pe: ParseException) {
                                    }
                                    try {
                                        val to = CUPClient.appointmentTimeFormatter.parse(fromToRaw[1])
                                        val toCal = Calendar.getInstance().also {
                                            it.time = to
                                        }
                                        endDate = if (startDate == null) null else {
                                            Calendar.getInstance().also {
                                                it.time = startDate?.time
                                            }
                                        }
                                        endDate?.apply {
                                            set(Calendar.HOUR, toCal.get(Calendar.HOUR))
                                            set(Calendar.MINUTE, toCal.get(Calendar.MINUTE))
                                        }
                                    } catch (pe: ParseException) {
                                    }
                                }
                            }
                        }
                        3 -> {
                            // Check if is fixed
                            fixed = domAppointmentColumn.hasClass("fixedLesson")
                            // Selected option
                            selectedOption = Option.parse(domAppointmentColumn, Option.Pattern.Appointments)
                        }
                        4 -> {
                            // "prekolom", not used, only in the Choose method
                        }
                        5 -> {
                            // Choices
                            domAppointmentColumn.select(".choiceItem > .clsButton").forEach { domChoice ->
                                val full = !domChoice.hasAttr("onmouseover") && !domChoice.hasAttr("onmouseout")
                                val onclick = domChoice.attr("onclick").replace("keuzelesClicked(", "")
                                val internalId = onclick.substring(0, onclick.indexOf(',')).toInt()
                                val option = Option.parse(domChoice, Option.Pattern.Appointments)

                                if (option != null)
                                    choices += Choice(option, full, internalId)
                            }
                        }
                    }
                }

                if (startDate == null) return Appointments(false).also {
                    it.failReason = "E_Appointments_StartDateNull"
                }

                appointments += Appointment(startDate!!.time, endDate!!.time, slot, selectedOption, fixed, choices)
            }

            return Appointments(true, appointments)
        }
    }
}
