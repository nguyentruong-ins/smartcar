package com.app.smartcar

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.smartcar.ui.theme.SmartCarTheme
import com.app.smartcar.viewmodel.RowViewModel

class MainActivity : ComponentActivity() {
    private val rowViewModel = RowViewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        val context = this
        super.onCreate(savedInstanceState)
        createNotificationChannel(context)

        setContent {
            SmartCarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.crv_hero_img), // Replace with your drawable
                            contentDescription = "Hero Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )

                        IconRowListScreen(rowViewModel)
                    }
                }
            }
        }
    }
}

data class RowData(
    val iconRes: Int,
    val currentValue: Long,
    val statusIconMap: Map<Long, Int>,
    val title: String,
    val description: String
)

fun createNotificationChannel(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//    notificationManager.deleteNotificationChannel("super_car_app_noti_id")
//    notificationManager.deleteNotificationChannel("super_car_app_indi_left_noti_id")
//    notificationManager.deleteNotificationChannel("super_car_app_indi_right_noti_id")

    val audioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
        .build()

    val lightAlertUri: Uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.light_alert)
    val lightChannel = NotificationChannel(
        "super_car_app_noti_id_3",
        "Super Car App Notification Channel",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Channel for alert notifications"
        setSound(lightAlertUri, audioAttributes)
    }

    val leftAlertUri: Uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.left_indi_alert)
    val leftIndiChannel = NotificationChannel(
        "super_car_app_indi_left_noti_id_1",
        "Super Car App Left Indicator Notification Channel",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Channel for left indi alert notifications"
        setSound(leftAlertUri, audioAttributes)
    }

    val rightAlertUri: Uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.right_indi_alert)
    val rightIndiChannel = NotificationChannel(
        "super_car_app_indi_right_noti_id_1",
        "Super Car App Right Indicator Notification Channel",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Channel for right indi alert notifications"
        setSound(rightAlertUri, audioAttributes)
    }

    notificationManager.createNotificationChannel(lightChannel)
    notificationManager.createNotificationChannel(leftIndiChannel)
    notificationManager.createNotificationChannel(rightIndiChannel)
}

@Composable
fun IconRowListScreen(
    viewModel: RowViewModel
) {
    val rowDataList by viewModel.rowData.collectAsState()
    viewModel.getRealtimeUpdate()

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(rowDataList) { row ->
            println("You go here")
            IconRow(row)
        }
    }
}

@Composable
fun IconRow(rowData: RowData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = rowData.iconRes),
            contentDescription = "Row Icon",
            modifier = Modifier
                .size(48.dp)
                .padding(end = 16.dp),
            contentScale = ContentScale.Fit
        )

        // Title and description in the middle
        Column(
            modifier = Modifier
                .weight(1f) // Take available space between the icons
                .padding(end = 16.dp)
        ) {
            Text(
                text = rowData.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
            Text(
                text = rowData.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            )
        }

        // Status Icon on the right
        Image(
            painter = painterResource(id = rowData.statusIconMap[rowData.currentValue]!!), // Replace with your status icon resource
            contentDescription = "Status Icon",
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Fit
        )
    }
}