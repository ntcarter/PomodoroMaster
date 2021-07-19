package natec.androidapp.masterpomodoro.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import natec.androidapp.masterpomodoro.R
import natec.androidapp.masterpomodoro.data.db.Timers
import natec.androidapp.masterpomodoro.databinding.ItemPomodoroTimerBinding

private const val TAG = "TimerAdapter"

class TimerAdapter(
    var items: List<Timers>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TimerAdapter.TimerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val binding =
            ItemPomodoroTimerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class TimerViewHolder(private val binding: ItemPomodoroTimerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(timer: Timers) {
            binding.apply {
                tvTimerName.text = timer.name
                tvTimerName.setTextColor(timer.textColor)
                listener.setUpObserver(timer, tvTimerH, tvTimerM, tvTimerS, tvColon1, tvColon2)
                tvTimerH.setTextColor(timer.textColor)
                tvTimerM.setTextColor(timer.textColor)
                tvTimerS.setTextColor(timer.textColor)
                tvColon1.setTextColor(timer.textColor)
                tvColon2.setTextColor(timer.textColor)

                btnplay.setOnClickListener {
                    btnplay.visibility = View.GONE
                    btnPause.visibility = View.VISIBLE
                    // start timer by forwarding to fragment hosting the timer
                    listener.scheduleTimer(timer)
                    timer.activateTimer()
                }

                btnPause.setOnClickListener {
                    btnplay.visibility = View.VISIBLE
                    btnPause.visibility = View.GONE
                    // cancel timer by forwarding to fragment hosting the timer
                    listener.cancelTimer(timer)
                    timer.pauseTimer()
                }

                btnTimerDelete.setOnLongClickListener {
                    listener.showDeletionDialog(timer)
                    btnTimerDelete.background = TRY TO GET THE DRAWABLE TO CHANGE ON CLICK HERE
                     MIGHT BE ABLE TO CHANGE PLAY PAUSE BUTTON  CHANGED TO IMAMGE BUTTON AND FORWARD LOGIC TO FRAGMENT
                    true
                }

                btnTimerRestart.setOnLongClickListener {
                    listener.restartTimer(timer)
                    true
                }

                cvTimer.setCardBackgroundColor(timer.timerColor)
                root.setOnLongClickListener {
                    if (btnPause.isVisible){
                        // Timer is active don't allow editing
                        listener.showEditDialogToast()
                    }else {
                        listener.showEditDialog(timer)
                    }
                    true
                }
            }
        }
    }

    // interface for forwarding click handling to a fragment
    interface OnItemClickListener {
        fun showDeletionDialog(timer: Timers)
        fun showEditDialog(timer: Timers)
        fun scheduleTimer(timer: Timers)
        fun cancelTimer(timer: Timers)
        fun setUpObserver(timer: Timers, tvHours: TextView, tvMin: TextView, tvSeconds: TextView, tvColon1: TextView, tvColon2: TextView)
        fun showEditDialogToast()
        fun restartTimer(timer: Timers)
    }
}