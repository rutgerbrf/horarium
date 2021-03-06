package nl.viasalix.horarium.ui.main.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import nl.viasalix.horarium.databinding.WeekSelectorDialogBinding
import nl.viasalix.horarium.utils.DateUtils.dateToString
import nl.viasalix.horarium.utils.DateUtils.dayToString
import nl.viasalix.horarium.utils.DateUtils.getCurrentWeek
import nl.viasalix.horarium.utils.DateUtils.getCurrentYear
import nl.viasalix.horarium.utils.DateUtils.isLeapYear
import nl.viasalix.horarium.utils.DateUtils.startOfWeek
import java.lang.NumberFormatException
import java.util.*

class WeekSelectorDialog : BottomSheetDialogFragment() {

    private lateinit var viewModel: WeekSelectorDialogViewModel
    private var initYear: Int? = null
    private var initWeek: Int? = null
    var onResultCallback: ((Int?, Int?) -> Unit)? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = WeekSelectorDialogBinding.inflate(inflater, container, false)
        val factory = WeekSelectorDialogViewModelFactory(
                initYear ?: getCurrentYear(),
                initWeek ?: getCurrentWeek()
        )
        viewModel = ViewModelProviders.of(this, factory).get(WeekSelectorDialogViewModel::class.java)
        initWeekObserver()

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnOk.setOnClickListener {
            dismiss()
            onResultCallback?.invoke(viewModel.year.value, viewModel.week.value)
        }

        return binding.root
    }

    private fun initWeekObserver() {
        viewModel.year.observe(this, Observer { year ->
            if (year < 1970) viewModel.year.value = 1970
        })
        viewModel.week.observe(this, Observer { week ->
            if (viewModel.year.value == null) {
                viewModel.year.value = getCurrentYear()
                return@Observer
            }
            val year = viewModel.year.value ?: return@Observer
            if (week > 52) {
                if (isLeapYear(year) && week < 54) return@Observer
                viewModel.incrementYear()
                viewModel.week.value = 1
                return@Observer
            }
            if (week < 1) {
                viewModel.decrementYear()
                viewModel.week.value = when {
                    isLeapYear(year - 1) -> 53
                    year == 1970 -> 1
                    else -> 52
                }
                return@Observer
            }

            updateDate()
        })
    }

    private fun updateDate() {
        val startOfWeek = Calendar.getInstance()
        try {
            val weekValue = viewModel.week.value ?: getCurrentWeek()
            val yearValue = viewModel.year.value ?: getCurrentYear()

            startOfWeek.time = startOfWeek(weekValue, yearValue)
        } catch (e: NumberFormatException) {
            return
        }

        val dayString = dayToString(startOfWeek.time)
        val dateString = dateToString(context, startOfWeek.time)
        viewModel.dayString.value = dayString.capitalize()
        viewModel.dateString.value = dateString.capitalize()
    }

    fun setup(year: Int?, week: Int?) {
        initYear = year
        initWeek = week
    }

}
