package ru.protei.smart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.protei.smart.SensorsDB.RetrofitClient
import ru.protei.smart.SensorsDB.SensorService
import ru.protei.smart.domain.Thing

class ThinfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra("id", -1)
        val name = intent.getStringExtra("name") ?: "Default Name"
        val status = intent.getBooleanExtra("status", false)

        setContent {
            if (id != -1) {
                val thing = Thing(id, name, status)
                Greeting2(thing, onBackButtonClick = {
                    restartMainActivity()
                })
            } else {
                Log.e("ThinfoActivity", "Invalid ID")
            }
        }
    }


    private fun restartMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting2(thing: Thing, onBackButtonClick: () -> Unit, modifier: Modifier = Modifier) {
    var status by remember { mutableStateOf(thing.status) }
    val service = RetrofitClient.getRetrofitInstance().create(SensorService::class.java)
    val context = LocalContext.current
    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Row {
            Text(
                text = thing.name.toString(),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text(
                text = "ВКЛ/ВЫКЛ",
                modifier = Modifier.padding(start = 65.dp)
            )
            Checkbox(
                checked = status,
                onCheckedChange = { newStatus ->
                    status = newStatus
                    service.updateThStatus(thing.id, newStatus)
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
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Управляющий датчик:",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        FloatingActionButton(
            onClick = {
                showToast(context, "Нет соединения с сервером")
            },
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp) // Уменьшение размера кнопки
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(24.dp))
        }
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}