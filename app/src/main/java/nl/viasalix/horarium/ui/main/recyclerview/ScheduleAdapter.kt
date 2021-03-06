package nl.viasalix.horarium.ui.main.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import kotlinx.android.synthetic.main.list_item_appointment.view.*
import nl.viasalix.horarium.databinding.ListItemAppointmentBinding
import nl.viasalix.horarium.data.zermelo.model.Appointment
import nl.viasalix.horarium.utils.DateUtils

class ScheduleAdapter(private val context: Context) :
        ListAdapter<Appointment, AppointmentViewHolder>(AppointmentDiffCallback()) {

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = getItem(position)
        val newDay = if (position > 0) {
            DateUtils.isOtherDay(appointment.start, getItem(position - 1).start)
        } else {
            true
        }

        val dayString = if (newDay) {
            DateUtils.dayToString(context, appointment.start)
        } else {
            ""
        }

        // Apply module customizations
        if (appointment.customizations.subjects != null) {
            appointment.subjects = appointment.customizations.subjects!!.toMutableList()
        }
        if (appointment.customizations.teachers != null) {
            appointment.teachers = appointment.customizations.teachers!!.toMutableList()
        }
        if (appointment.customizations.locations != null) {
            appointment.locations = appointment.customizations.locations!!.toMutableList()
        }

        holder.apply {
            holder.bind(appointment)
            itemView.tag = appointment
            if (newDay) {
                itemView.tvDay.text = dayString
                itemView.tvDay.visibility = View.VISIBLE
            }
            addBuiltinChips(context, appointment)
            if (appointment.customizations.extraChips?.isNotEmpty() ?: return@apply) {
                this.addExtraChips(context, appointment.customizations.extraChips!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        return AppointmentViewHolder(ListItemAppointmentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }
}
