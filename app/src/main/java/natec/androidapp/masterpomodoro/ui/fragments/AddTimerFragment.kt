package natec.androidapp.masterpomodoro.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import natec.androidapp.masterpomodoro.R
import natec.androidapp.masterpomodoro.databinding.FragmentAddTimerBinding
import natec.androidapp.masterpomodoro.ui.viewmodels.AddTimerViewModel
import natec.androidapp.masterpomodoro.ui.viewmodels.AddTimerViewModelFactory
import javax.inject.Inject

private const val TAG = "AddTimerFragment"

@AndroidEntryPoint
class AddTimerFragment: Fragment(R.layout.fragment_add_timer) {

    private lateinit var viewModel: AddTimerViewModel
    @Inject lateinit var factory: AddTimerViewModelFactory

    // we need to null out our binding variable (in onDestroyView)
    // or it will keep an unnecessary instance of our view hierarchy
    private var _binding: FragmentAddTimerBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // AddTimerViewModel is shard among multiple fragments so scope it to the activity hosting the fragments
        viewModel = ViewModelProvider(requireActivity(), factory).get(AddTimerViewModel::class.java)

        _binding = FragmentAddTimerBinding.bind(view)

        binding.apply {
            btnSave.setOnClickListener {

                // add the timer to the DB
                // get all data and forward it to somewhere it can be saved (ViewModel)
                Log.d(TAG, "onViewCreated values: name: ${etName.text} , ${etTaskHour.text}")
                viewModel.getTimerReadyForInsert(
                    etName.text.toString(),
                    etTaskHour.text.toString(),
                    etTaskMin.text.toString(),
                    etTaskSecond.text.toString(),
                    etBreakHour.text.toString(),
                    etBreakMin.text.toString(),
                    etBreakSecond.text.toString()
                )
                val action = AddTimerFragmentDirections.actionAddTimerFragmentToTimersFragment()
                findNavController().navigate(action)
            }
        }
    }
}