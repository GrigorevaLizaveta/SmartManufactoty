package ru.protei.smart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.protei.smart.SensorsDB.RetrofitClient
import ru.protei.smart.SensorsDB.SensorService
import ru.protei.smart.domain.Sensor
import ru.protei.smart.ui.theme.PurpleGrey80

var maxi ="0"
var mini = "0"
var tip = "Нет"


class MoreInfActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("name")
        val data = intent.getStringExtra("data")
        val status = intent.getBooleanExtra("status", false)

        val service = RetrofitClient.getRetrofitInstance().create(SensorService::class.java)

        if (name != null) {
            service.getInfo(name.toString())?.enqueue(object : Callback<List<String?>?> {
                override fun onResponse(call: Call<List<String?>?>, response: Response<List<String?>?>) {
                    if (response.isSuccessful) {
                        val info = response.body()
                        if (!info.isNullOrEmpty()) {
                            if (info != null) {
                                if (info.isNotEmpty()) {
                                    mini = (info[0] ?: "Нет данных").toString()
                                    maxi = (info[1] ?: "Нет данных").toString()
                                    tip = (info[2] ?: "Нет данных").toString()
                                    Log.d("API Data", "Lower Bound: $mini, Upper Bound: $maxi, Data Type: $tip")
                                }
                            }
                        }
                    } else {
                        Log.e("API Error", "Response not successful")
                    }
                }

                override fun onFailure(call: Call<List<String?>?>, t: Throwable) {
                    Log.e("API Error", "Error fetching data: ${t.message}")
                }
            })
        }
        setContent {
            val sensor = Sensor(name.toString(), data.toString(), status, tip)
            Greeting(sensor, onBackButtonClick = {
                restartMainActivity()
            })
        }
    }

    private fun restartMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}



@Composable
fun Greeting(sensor: Sensor, onBackButtonClick: () -> Unit, modifier: Modifier = Modifier) {
    var status by remember { mutableStateOf(sensor.status) }
    val service = RetrofitClient.getRetrofitInstance().create(SensorService::class.java)
    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Row(
//            modifier = Modifier.padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Button(
//                onClick = onBackButtonClick,
//                modifier = Modifier
//                    .padding(end = 6.dp)
//            ) {
//                Text("Назад")
//            }
//            Spacer(modifier = Modifier.width(16.dp))
//        }
        Row {
            Text(
                text = sensor.name,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ВКЛ/ВЫКЛ",
                modifier = Modifier.padding(start = 65.dp)
            )
            Checkbox(
                checked = status,
                onCheckedChange = {
                    status = it
                    // Отправляем изменение статуса на сервер
                    service.updateSensorStatus(sensor.name, status)
                        .enqueue(object : Callback<Map<String, String>> {
                            override fun onResponse(
                                call: Call<Map<String, String>>,
                                response: Response<Map<String, String>>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d(
                                        "API",
                                        "Статус успешно обновлен: ${
                                            response.body()?.get("message")
                                        }"
                                    )
                                } else {
                                    Log.e("API Error", "Response not successful")
                                }
                            }

                            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                                Log.e("API Error", "Ошибка при отправке запроса: ${t.message}")
                            }
                        })
                },
                modifier = Modifier.weight(1f)
            )
        }
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color = PurpleGrey80)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Последнее значение датчика:",
                        style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
                        modifier = Modifier.padding(start = 25.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 26.dp, bottom = 16.dp)
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "${sensor.data} ${sensor.tip}",
                        style = TextStyle(fontSize = 20.sp),
                        modifier = Modifier.padding(start = 90.dp)
                    )
                }
            }
        }
        Row {
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Основные характеристики:",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Column {
            Row {
                Text(
                    text = "Нижнее нормативное значение:",
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = mini,
                    onValueChange = { mini = it },
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .width(100.dp),
                    textStyle = TextStyle(fontSize = 16.sp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tip,
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            Row {
                Text(
                    text = "Верхнее нормативное значение:",
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = mini,
                    onValueChange = { mini = it },
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .width(100.dp),
                    textStyle = TextStyle(fontSize = 16.sp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tip,
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        }
        Row {
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Зависимые устройства:",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Column {
            Row {
                Text(
                    text = "Устройство:",
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Spacer(modifier = Modifier.width(160.dp))
                Text(
                    text = " Розетка",
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            FloatingActionButton(
                onClick = {},
                modifier = Modifier
                    .padding(16.dp)
                    .size(48.dp) // Уменьшение размера кнопки
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(24.dp))
            }
        }
    }}






