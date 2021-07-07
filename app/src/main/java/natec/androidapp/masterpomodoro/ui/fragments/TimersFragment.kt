package natec.androidapp.masterpomodoro.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import natec.androidapp.masterpomodoro.R
import natec.androidapp.masterpomodoro.adapters.TimerAdapter
import natec.androidapp.masterpomodoro.data.db.Timers
import natec.androidapp.masterpomodoro.databinding.FragmentTimersBinding
import natec.androidapp.masterpomodoro.ui.viewmodels.AddTimerViewModel
import natec.androidapp.masterpomodoro.ui.viewmodels.AddTimerViewModelFactory
import javax.inject.Inject

private const val TAG = "TimersFragment"

@AndroidEntryPoint
class TimersFragment : Fragment(R.layout.fragment_timers), TimerAdapter.OnItemClickListener {

    private lateinit var viewModel: AddTimerViewModel
    @Inject lateinit var factory: AddTimerViewModelFactory

    // we need to null out our binding variable (in onDestroyView)
    // or it will keep an unnecessary instance of our view hierarchy
    private var _binding: FragmentTimersBinding? = null
    private val binding get() =  _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), factory).get(AddTimerViewModel::class.java)

        _binding = FragmentTimersBinding.bind(view)

        val adapter = TimerAdapter(listOf(), this)

        binding.apply {
            fabAddTimer.setOnClickListener {
                val action = TimersFragmentDirections.actionTimersFragmentToAddTimerFragment()
                findNavController().navigate(action)
            }
            rvTimers.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                this.adapter = adapter
            }
        }

        viewModel.getAllTimers().observe(viewLifecycleOwner, {
            Log.d(TAG, "onViewCreated: OBSERVING: $it")
            adapter.items = it
            adapter.notifyDataSetChanged()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun deleteTimerClick(timer: Timers) {
        viewModel.delete(timer)
    }

    override fun showDeletionDialog(timer: Timers) {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Delete This Timer?")
            .setMessage("Are you Sure?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes") { _, _ ->
                // remove the timer from the database
                deleteTimerClick(timer)
            }
            .setNegativeButton("No") {dialogInterface, _ ->
                dialogInterface.cancel()
            }.create()
        dialog.show()
    }

    override fun showEditDialog(timer: Timers) {
        viewModel.activeEditTimer = timer
        // Create the dialog and pass it information about the selected timer
        val dialogFragment = EditTimerDialogFragment()
        dialogFragment.show(parentFragmentManager, "editTimer")
    }
}