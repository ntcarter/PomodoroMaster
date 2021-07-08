package natec.androidapp.masterpomodoro.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import natec.androidapp.masterpomodoro.databinding.EditTimerDialogBinding
import natec.androidapp.masterpomodoro.ui.viewmodels.AddTimerViewModel
import natec.androidapp.masterpomodoro.ui.viewmodels.AddTimerViewModelFactory
import natec.androidapp.masterpomodoro.util.convertToHHMMSS
import top.defaults.colorpicker.ColorPickerPopup
import javax.inject.Inject

private const val TAG = "EditTimerDialog"

@AndroidEntryPoint
class EditTimerDialogFragment() : DialogFragment() {

    private var _binding: EditTimerDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddTimerViewModel
    @Inject lateinit var factory: AddTimerViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // scope the viewModel to the activity so it uses the same instance other fragments use
        viewModel = ViewModelProvider(requireActivity(), factory).get(AddTimerViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // inflate the layout with viewBinding
        _binding = EditTimerDialogBinding.inflate(LayoutInflater.from(context))

        // Time is stored in seconds. Convert it to HH/MM/SS
        val convertedTotalTime = convertToHHMMSS(viewModel.activeEditTimer!!.totalTime)
        val convertedBreakTime = convertToHHMMSS(viewModel.activeEditTimer!!.breakTotalTime)

        binding.apply {
            etEditName.setText(viewModel.activeEditTimer!!.name)
            etEditTaskHour.setText(convertedTotalTime.first.toString())
            etEditTaskMin.setText(convertedTotalTime.second.toString())
            etEditTaskSecond.setText(convertedTotalTime.third.toString())
            etEditBreakHour.setText(convertedBreakTime.first.toString())
            etEditBreakMin.setText(convertedBreakTime.second.toString())
            etEditBreakSecond.setText(convertedBreakTime.third.toString())

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
                            etEditTaskHour.text.toString(),
                            etEditTaskMin.text.toString(),
                            etEditTaskSecond.text.toString(),
                            etEditBreakHour.text.toString(),
                            etEditBreakMin.text.toString(),
                            etEditBreakSecond.text.toString(),
                            (tvEditBgColor.background as ColorDrawable).color,
                            (tvEditTextColor.background as ColorDrawable).color
                        )
                    }
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
        Log.d(TAG, "openColorPicker: Color passed in: $color")
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