package natec.androidapp.masterpomodoro.ui.fragments

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import natec.androidapp.masterpomodoro.R
import natec.androidapp.masterpomodoro.adapters.TimerAdapter
import natec.androidapp.masterpomodoro.data.db.Timers
import natec.androidapp.masterpomodoro.databinding.FragmentTimersBinding
import natec.androidapp.masterpomodoro.ui.TimerActivity
import natec.androidapp.masterpomodoro.ui.viewmodels.TimerViewModelFactory
import natec.androidapp.masterpomodoro.ui.viewmodels.TimersViewModel
import natec.androidapp.masterpomodoro.util.Constants
import natec.androidapp.masterpomodoro.util.convertToHHMMSS
import javax.inject.Inject

@AndroidEntryPoint
class TimersFragment : Fragment(R.layout.fragment_timers), TimerAdapter.OnItemClickListener {

    private val viewModel: TimersViewModel by viewModels() // hilt injected

    @Inject
    lateinit var factory: TimerViewModelFactory

    // we need to null out our binding variable (in onDestroyView)
    // or it will keep an unnecessary instance of our view hierarchy
    private var _binding: FragmentTimersBinding? = null
    private val binding get() = _binding!!

    private val activeTimers = arrayListOf<Timers>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        // observes the livedata that holds a list of the timers in the DB
        viewModel.getAllTimers().observe(viewLifecycleOwner) {
            adapter.items = it
            adapter.notifyDataSetChanged()
        }
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
                // remove the timer from the database and cancel it
                activeTimers.remove(timer)
                timer.pauseTimer()
                timer.cancelTimer(requireContext())
                deleteTimerClick(timer)
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }.create()
        dialog.show()
    }

    override fun showEditDialog(timer: Timers) {
        // set the active timer being edited so we can access its views in another fragment
        viewModel.activeEditTimer = timer

        val dialogFragment = EditTimerDialogFragment()
        dialogFragment.show(parentFragmentManager, "editTimer")
    }

    override fun scheduleTimer(timer: Timers) {
        activeTimers.add(timer)
        timer.scheduleTimer(requireContext())

        createNotification()
    }

    override fun cancelTimer(timer: Timers) {
        activeTimers.remove(timer)
        timer.cancelTimer(requireContext())
        cancelNotification()
    }

    private fun createNotification() {
        val notificationIntent = Intent(context, TimerActivity::class.java)

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, notificationIntent, 0)

        // create active alarm notification
        val builder =
            NotificationCompat.Builder(requireContext(), Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.timer_notif)
                .setContentTitle("Master Pomodoro")
                .setContentText("Timer active")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(Constants.NOTIFICATION_ID, builder.build())
        }
    }

    private fun cancelNotification() {
        // cancel active alarm notification
        val notificationManager: NotificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(Constants.NOTIFICATION_ID)
    }

    override fun restartTimer(timer: Timers) {
        timer.pauseTimer()
        cancelTimer(timer)
        timer.restartTimer()
    }

    override fun setUpObserver(
        timer: Timers,
        tvHours: TextView,
        tvMin: TextView,
        tvSeconds: TextView,
        tvColon1: TextView,
        tvColon2: TextView
    ) {
        timer.timeLeft.observe(this, {

            // calculate the HH/MM/SS and then set the text
            val convertedTime = convertToHHMMSS(it)

            // Hours visibility check
            if (convertedTime.first <= 0 && tvHours.visibility == View.VISIBLE) {
                tvHours.visibility = View.GONE
                tvColon1.visibility = View.GONE
            } else if (convertedTime.first > 0) {
                tvHours.text = convertedTime.first.toString()

                if (tvHours.visibility == View.GONE) {
                    tvHours.visibility = View.VISIBLE
                    tvColon1.visibility = View.VISIBLE
                }
            }

            // Minutes visibility check
            if (convertedTime.second <= 0 && tvMin.visibility == View.VISIBLE) {
                tvMin.visibility = View.GONE
                tvColon2.visibility = View.GONE
            } else if (convertedTime.second > 0) {
                tvMin.text = convertedTime.second.toString()
                if (tvMin.visibility == View.GONE) {
                    tvMin.visibility = View.VISIBLE
                    tvColon2.visibility = View.VISIBLE
                }
            }

            if (convertedTime.third < 0) {
                tvSeconds.text = "0"
            } else {
                tvSeconds.text = convertedTime.third.toString()
            }

            // length 1 means single digit, need to add a 0 in front of it
            if (tvSeconds.text.length == 1) {
                tvSeconds.text = "0" + tvSeconds.text
            }
        })
    }

    override fun showEditDialogToast() {
        Toast.makeText(requireContext(), "Timer Must Be Paused To Edit", Toast.LENGTH_SHORT).show()
    }
}