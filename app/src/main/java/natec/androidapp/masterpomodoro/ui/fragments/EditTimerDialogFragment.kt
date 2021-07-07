package natec.androidapp.masterpomodoro.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import natec.androidapp.masterpomodoro.databinding.EditTimerDialogBinding

class EditTimerDialogFragment(savedInstanceState: Bundle?) : DialogFragment() {

    private var _binding: EditTimerDialogBinding? = null
    private val binding get() =  _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // inflate the layout with viewBinding
        _binding = EditTimerDialogBinding.inflate(LayoutInflater.from(context))
        return activity?.let {
            val arg = arguments

            val builder = AlertDialog.Builder(requireActivity())

            // pass the root view of our binding to our builder
            builder.setView(binding.root)
                .setPositiveButton("Save") {_, _ ->

                }
                .setNegativeButton("Cancel") {dialogInterface, _ ->
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