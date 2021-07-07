package natec.androidapp.masterpomodoro.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import natec.androidapp.masterpomodoro.databinding.EditTimerDialogBinding
import natec.androidapp.masterpomodoro.ui.viewmodels.AddTimerViewModel
import natec.androidapp.masterpomodoro.ui.viewmodels.AddTimerViewModelFactory
import natec.androidapp.masterpomodoro.util.convertToHHMMSS
import javax.inject.Inject

private const val TAG = "EditTimerDialog"

@AndroidEntryPoint
class EditTimerDialogFragment() : DialogFragment() {

    private var _binding: EditTimerDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddTimerViewModel
    @Inject
    lateinit var factory: AddTimerViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        }

        return activity?.let {

            Log.d(TAG, "onCreateDialog: activeTimer: ${viewModel.activeEditTimer}")
            //Since we are using viewBinding we need the context from the activity this fragment is hosted in
            val builder = AlertDialog.Builder(requireActivity())

            // pass the root view of our binding to our builder
            builder.setView(binding.root)
                .setPositiveButton("Save") { _, _ ->
                    // update the Timer with new data
                    viewModel.getTimerReadyForUpdate(
                        binding.etEditName.text.toString(),
                        binding.etEditTaskHour.text.toString(),
                        binding.etEditTaskMin.text.toString(),
                        binding.etEditTaskSecond.text.toString(),
                        binding.etEditBreakHour.text.toString(),
                        binding.etEditBreakMin.text.toString(),
                        binding.etEditBreakSecond.text.toString()
                    )
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
}