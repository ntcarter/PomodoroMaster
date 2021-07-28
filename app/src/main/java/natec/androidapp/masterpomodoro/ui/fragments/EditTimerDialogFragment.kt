package natec.androidapp.masterpomodoro.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import natec.androidapp.masterpomodoro.databinding.EditTimerDialogBinding
import natec.androidapp.masterpomodoro.ui.viewmodels.TimerViewModelFactory
import natec.androidapp.masterpomodoro.ui.viewmodels.TimersViewModel
import natec.androidapp.masterpomodoro.util.convertToHHMMSS
import top.defaults.colorpicker.ColorPickerPopup
import javax.inject.Inject

private const val TAG = "EditTimerDialog"

@AndroidEntryPoint
class EditTimerDialogFragment() : DialogFragment() {

    private var _binding: EditTimerDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TimersViewModel by viewModels() // hilt injected
    @Inject lateinit var factory: TimerViewModelFactory

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // inflate the layout with viewBinding
        _binding = EditTimerDialogBinding.inflate(LayoutInflater.from(context))

        // Time is stored in seconds. Convert it to HH/MM/SS
        val convertedTotalTime = convertToHHMMSS(viewModel.activeEditTimer!!.taskTotalTime)
        val convertedBreakTime = convertToHHMMSS(viewModel.activeEditTimer!!.breakTotalTime)

        binding.apply {
            etEditName.setText(viewModel.activeEditTimer!!.name)
            pickerEditTaskH.maxValue = 24
            pickerEditTaskH.minValue = 0
            pickerEditTaskM.maxValue = 60
            pickerEditTaskM.minValue = 0
            pickerEditTaskS.maxValue = 60
            pickerEditTaskS.minValue = 0
            pickerEditBreakH.maxValue = 24
            pickerEditBreakH.minValue = 0
            pickerEditBreakM.maxValue = 60
            pickerEditBreakM.minValue = 0
            pickerEditBreakS.maxValue = 60
            pickerEditBreakS.minValue = 0
            pickerEditTaskH.value = convertedTotalTime.first
            pickerEditTaskM.value = convertedTotalTime.second
            pickerEditTaskS.value = convertedTotalTime.third
            pickerEditBreakH.value = convertedBreakTime.first
            pickerEditBreakM.value = convertedBreakTime.second
            pickerEditBreakS.value = convertedBreakTime.third

            tvEditBgColor.setBackgroundColor(viewModel.activeEditTimer!!.timerColor)
            tvEditTextColor.setBackgroundColor(viewModel.activeEditTimer!!.textColor)

            tvEditBgColor.setOnClickListener {
                val colorIdInt = (tvEditBgColor.background as ColorDrawable).color
                openColorPicker(colorIdInt, tvEditBgColor)
            }

            tvEditTextColor.setOnClickListener {
                val colorIdInt = (tvEditTextColor.background as ColorDrawable).color
                openColorPicker(colorIdInt, tvEditTextColor)

            }
        }

        return activity?.let {

            //Since we are using viewBinding we need the context from the activity this fragment is hosted in
            val builder = AlertDialog.Builder(requireActivity())

            // pass the root view of our binding to our builder
            builder.setView(binding.root)
                .setPositiveButton("Save") { _, _ ->
                    // update the Timer with new data
                    binding.apply {
                        viewModel.getTimerReadyForUpdate(
                            etEditName.text.toString(),
                            pickerEditTaskH.value.toString(),
                            pickerEditTaskM.value.toString(),
                            pickerEditTaskS.value.toString(),
                            pickerEditBreakH.value.toString(),
                            pickerEditTaskM.value.toString(),
                            pickerEditTaskS.value.toString(),
                            (tvEditBgColor.background as ColorDrawable).color,
                            (tvEditTextColor.background as ColorDrawable).color
                        )
                    }
                    viewModel.activeEditTimer = null
                }
                .setNegativeButton("Cancel") { dialogInterface, _ ->
                    viewModel.activeEditTimer = null
                    dialogInterface.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity Cannot be null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openColorPicker(color: Int, view: TextView) {
        ColorPickerPopup.Builder(requireContext())
            .initialColor(color)
            .enableBrightness(true)
            .okTitle("Save")
            .cancelTitle("Cancel")
            .showIndicator(true)
            .showValue(false)
            .build()
            .show(view, object : ColorPickerPopup.ColorPickerObserver() {
                override fun onColorPicked(color: Int) {
                    view.setBackgroundColor(color)
                }
            })
    }
}