package natec.androidapp.masterpomodoro.util


fun convertToHHMMSS(time: Int): Triple<Int, Int, Int> {
    val hours: Int
    val minutes: Int
    val seconds: Int

    var timeLeft = time

    hours = timeLeft / 3600
    timeLeft -= hours * 3600

    minutes = timeLeft / 60
    timeLeft -= minutes * 60

    seconds = timeLeft
    return Triple(hours, minutes, seconds)
}

fun convertToSeconds(hours: Int, minutes: Int, seconds: Int): Int {
    var totalTaskTime = 0
    totalTaskTime += hours * 3600
    totalTaskTime += minutes * 60
    totalTaskTime += seconds

    return totalTaskTime
}
