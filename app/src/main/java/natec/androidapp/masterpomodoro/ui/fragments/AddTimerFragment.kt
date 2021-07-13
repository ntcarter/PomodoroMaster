package natec.androidapp.masterpomodoro.ui.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import natec.androidapp.masterpomodoro.R
import natec.androidapp.masterpomodoro.databinding.FragmentAddTimerBinding
import natec.androidapp.masterpomodoro.ui.viewmodels.AddTimerViewModel
import natec.androidapp.masterpomodoro.ui.viewmodels.AddTimerViewModelFactory
import top.defaults.colorpicker.ColorPickerPopup
import javax.inject.Inject

private const val TAG = "AddTimerFragment"

@AndroidEntryPoint
class AddTimerFragment : Fragment(R.layout.fragment_add_timer) {

    private lateinit var viewModel: AddTimerViewModel

    @Inject
    lateinit var factory: AddTimerViewModelFactory

    // need to null out our binding variable (in onDestroyView)
    // or it will keep an unnecessary instance of our view hierarchy
    private var _binding: FragmentAddTimerBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // AddTimerViewModel is shard among multiple fragments so scope it to the activity hosting the fragments
        viewModel = ViewModelProvider(requireActivity(), factory).get(AddTimerViewModel::class.java)

        _binding = FragmentAddTimerBinding.bind(view)

        binding.apply {
            taskHourPicker.maxValue = 24
            taskHourPicker.minValue = 0
            taskMinutePicker.maxValue = 60
            taskMinutePicker.minValue = 0
            taskSecondPicker.maxValue = 60
            taskSecondPicker.minValue = 0
            breakHourPicker.maxValue = 24
            breakHourPicker.minValue = 0
            breakMinutePicker.maxValue = 60
            breakMinutePicker.minValue = 0
            breakSecondPicker.maxValue = 60
            breakSecondPicker.minValue = 0
            btnSave.setOnClickListener {

                // add the timer to the DB
                // get all data and forward it to somewhere it can be saved (ViewModel)
                viewModel.getTimerReadyForInsert(
                    etName.text.toString(),
                    taskHourPicker.value.toString(),
                    taskMinutePicker.value.toString(),
                    taskSecondPicker.value.toString(),
                    breakHourPicker.value.toString(),
                    breakMinutePicker.value.toString(),
                    breakSecondPicker.value.toString(),
                    (tvBgColor.background as ColorDrawable).color,
                    (tvTextColor.background as ColorDrawable).color
                )
                navigateToTimerFragment()
            }

            btnCancel.setOnClickListener {
                navigateToTimerFragment()
            }

            tvBgColor.setOnClickListener {
                val colorIdInt = (tvBgColor.background as ColorDrawable).color
                openColorPicker(colorIdInt, tvBgColor)
            }

            tvTextColor.setOnClickListener {
                val colorIdInt = (tvTextColor.background as ColorDrawable).color
                openColorPicker(colorIdInt, tvTextColor)
            }
        }
    }

    private fun navigateToTimerFragment() {
        val action = AddTimerFragmentDirections.actionAddTimerFragmentToTimersFragment()
        findNavController().navigate(action)
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
            .show(view, object : ColorPickerPopup.ColorPickerObserver(){
                override fun onColorPicked(color: Int) {
                    view.setBackgroundColor(color)
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}