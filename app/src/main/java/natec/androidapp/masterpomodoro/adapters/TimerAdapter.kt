package natec.androidapp.masterpomodoro.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

    inner class TimerViewHolder(private val binding: ItemPomodoroTimerBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(timer: Timers){
            binding.apply {
                tvTimerName.text = timer.name
                tvTimerName.setTextColor(timer.textColor)
                tvTimerTime.text = timer.taskTotalTime.toString()
                tvTimerTime.setTextColor(timer.textColor)

                btnplay.setOnClickListener {
                    btnplay.visibility = View.GONE
                    btnPause.visibility = View.VISIBLE
                    // start timer by forwarding to fragment hosting the timer
                    listener.scheduleTimer(timer)
                }

                btnPause.setOnClickListener {
                    btnplay.visibility = View.VISIBLE
                    btnPause.visibility = View.GONE
                    // cancel timer by forwarding to fragment hosting the timer
                    listener.cancelTimer(timer)
                }

                btnTimerDelete.setOnLongClickListener {
                    listener.showDeletionDialog(timer)
                    true
                }
                cvTimer.setCardBackgroundColor(timer.timerColor)
                root.setOnLongClickListener {
                    Log.d(TAG, "Tapped view")
                    listener.showEditDialog(timer)
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
    }
}