package com.app.smartcar.viewmodel

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import com.app.smartcar.MainActivity
import com.app.smartcar.R
import com.app.smartcar.RowData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalTime

class RowViewModel(
    // TODO: Find another way to do this
    val context: Context
) : ViewModel() {
    private val _rowData = MutableStateFlow<List<RowData>>(emptyList())
    val rowData: StateFlow<List<RowData>> get() = _rowData

    private val dbRef = FirebaseDatabase.getInstance().getReference()

    private val carDataListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // Get values of Realtime Database
            val lightVal = snapshot.child("test").child("den").value!! as Long
            val acVal = snapshot.child("test").child("dieuhoa").value!! as Long
            val wiperVal = snapshot.child("test").child("gatmua").value!! as Long
            val indicatorsVal = snapshot.child("test").child("sinhan").value!! as Long
            val runningVal = snapshot.child("test").child("running").value!! as Long

            // Handle headlight logic
            checkHeadlight(lightVal, runningVal)

            val leftAlertVal = snapshot.child("test").child("canhbaosinhantrai").value!! as Long
            val rightAlertVal = snapshot.child("test").child("canhbaosinhanphai").value!! as Long
            checkIndicatorAlert(leftAlertVal, rightAlertVal)



            // TODO: Add more values
            /*
            val cosVal = snapshot.child("test").child("cos").value!! as Long
             */

            val dataList = listOf(
                RowData(
                    iconRes = R.drawable.light,
                    currentValue = lightVal,
                    statusIconMap = mapOf(
                        1L to R.drawable.green_check,
                        0L to R.drawable.red_no
                    ),
                    title = "Headlight",
                    description = "This is the headlight status of the car"
                ),
                RowData(
                    iconRes = R.drawable.ac,
                    currentValue = acVal,
                    statusIconMap = mapOf(
                        1L to R.drawable.green_check,
                        0L to R.drawable.red_no
                    ),
                    title = "Air Conditioner",
                    description = "This is the air conditioner status of the car"
                ),
                RowData(
                    iconRes = R.drawable.windshield,
                    currentValue = wiperVal,
                    statusIconMap = mapOf(
                        1L to R.drawable.green_check,
                        0L to R.drawable.red_no
                    ),
                    title = "Wiper",
                    description = "This is the wiper status of the car"
                ),
                RowData(
                    iconRes = R.drawable.car_running,
                    currentValue = runningVal,
                    statusIconMap = mapOf(
                        1L to R.drawable.green_check,
                        0L to R.drawable.red_no
                    ),
                    title = "Car Running",
                    description = "This is the running status of the car"
                ),
                RowData(
                    iconRes = R.drawable.indicators,
                    currentValue = indicatorsVal,
                    statusIconMap = mapOf(
                        1L to R.drawable.green_check,
                        0L to R.drawable.red_no
                    ),
                    title = "Indicators (Left or Right)",
                    description = "This is the indicators status of the car"
                )
            )

            _rowData.value = dataList
        }

        override fun onCancelled(error: DatabaseError) {
            println("Update data error: $error")
        }
    }

    fun getRealtimeUpdate() {
        dbRef.addValueEventListener(carDataListener)
    }

    override fun onCleared() {
        super.onCleared()
        dbRef.removeEventListener(carDataListener)
    }

    private fun checkHeadlight(lightVal: Long, runningVal: Long) {
        val channelId = "super_car_app_noti_id_3"
        val predefinedTime = LocalTime.of(18, 0) // 6:00 PM
        val currentTime = LocalTime.now()

        if (currentTime.isAfter(predefinedTime) && lightVal != 1L && runningVal == 1L)  {
            showAlertNotification(context, "It's getting darker, please turn on the light", "Current time is 18PM, but your light is not turned on. It's better if you can turn on the light!", channelId)
        }
    }

    private fun checkIndicatorAlert(leftAlertVal: Long, rightAlertVal: Long) {
        val leftIndiChannelId = "super_car_app_indi_left_noti_id_1"
        if (leftAlertVal == 1L) {
            showAlertNotification(context, "Please turn on the left indicator", "You are turning left without turning on the left indicator, please turn on!", leftIndiChannelId)
        }

        val rightIndiChannelId = "super_car_app_indi_right_noti_id_1"
        if (rightAlertVal == 1L) {
            showAlertNotification(context, "Please turn on the right indicator", "You are turning right without turning on the right indicator, please turn on!", rightIndiChannelId)
        }
    }

    private fun showAlertNotification(context: Context, title: String, message: String, channelId: String) {
        // Create an intent to open an activity when the notification is clicked
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app's icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationManager.IMPORTANCE_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Dismiss notification when clicked
            .build()

        // Show the notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification) // Use a unique ID for each notification
    }
}