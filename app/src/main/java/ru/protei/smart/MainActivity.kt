package ru.protei.smart

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.protei.smart.SensorsDB.RetrofitClient
import ru.protei.smart.SensorsDB.SensorService
import ru.protei.smart.domain.Sensor
import ru.protei.smart.domain.Thing
import ru.protei.smart.ui.theme.SmartTheme

//Данные при отсутвии коннекта с бд
val sensorData = listOf(
            Sensor("Датчик света", "0", true," лк"),
)
val thData = listOf(
    Thing(0, "Розетка", false),
)

class MainActivity : ComponentActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val service = RetrofitClient.getRetrofitInstance().create(SensorService::class.java)

        // Объявляем переменные для списков sensors и things
        var sensors: List<Sensor> = emptyList()
        var things: List<Thing> = emptyList()

        updateRunnable = object : Runnable {
            override fun run() {
                // Получаем данные для списка sensors
                service.getSensors()?.enqueue(object : Callback<List<List<String?>?>?> {
                    override fun onResponse(
                        call: Call<List<List<String?>?>?>,
                        response: Response<List<List<String?>?>?>
                    ) {
                        if (response.isSuccessful) {
                            sensors = convertToSensors(response.body())
                            Log.d("Send", sensors.toString())

                            // Получаем данные для списка things
                            service.getThings()?.enqueue(object : Callback<List<List<String?>?>?> {
                                override fun onResponse(
                                    call: Call<List<List<String?>?>?>,
                                    response: Response<List<List<String?>?>?>
                                ) {
                                    if (response.isSuccessful) {
                                        things = convertToThings(response.body())
                                        Log.d("Send", things.toString())

                                        // Обновляем UI
                                        updateUI(sensors, things)
                                    } else {
                                        Log.d("Send", "no things")
                                        updateUI(sensors, thData)
                                    }
                                }

                                override fun onFailure(call: Call<List<List<String?>?>?>, t: Throwable) {
                                    Log.e("Send", "Error occurred while fetching things: ${t.message}")
                                }
                            })
                        } else {
                            Log.d("Send", "no sensors")
                            updateUI(sensorData, thData)
                        }
                    }

                    override fun onFailure(call: Call<List<List<String?>?>?>, t: Throwable) {
                        Log.e("Send", "Error occurred while fetching sensors: ${t.message}")
                    }
                })

                // Повторяем этот Runnable через 30 секунд
                handler.postDelayed(this, 30000)
            }
        }

        // Запускаем первое выполнение Runnable
        handler.post(updateRunnable)
    }

    private fun updateUI(sensors: List<Sensor>, things: List<Thing>) {
        setContent {
            SmartTheme {
                PageMain(
                    onButtonClickTh = { val intent = Intent(this@MainActivity, MainAddTh::class.java)
                        this@MainActivity.startActivity(intent)},
                    onButtonClickS = { val intent = Intent(this@MainActivity, MainAddSen::class.java)
                        this@MainActivity.startActivity(intent)},
                    onButtonClick = {
                        val intent = Intent(this@MainActivity, SecondActivity::class.java)
                        startActivity(intent)
                    },
                    onButtonClick2 = { val intent = Intent(this@MainActivity, MainShowThTable::class.java)
                        this@MainActivity.startActivity(intent)},
                    onButtonClickRoom = { val intent = Intent(this@MainActivity, MainAddRoom::class.java)
                        this@MainActivity.startActivity(intent)},
                    onSensorItemClick = { sensor ->
                        val intent = Intent(this@MainActivity, MoreInfActivity::class.java).apply {
                            putExtra("name", sensor.name)
                            putExtra("data", sensor.data)
                            putExtra("status", sensor.status)
                            putExtra("tip", sensor.tip)
                        }
                        startActivity(intent)
                    },
                    onThItemClick = { thing ->
                        val intent = Intent(this@MainActivity, ThinfoActivity::class.java).apply {
                            putExtra("id", thing.id)
                            putExtra("name", thing.name.toString())
                            putExtra("status", thing.status)
                        }
                        startActivity(intent)
                    },
                    sensorData = sensorData, // Передаем список sensors
                    thData = thData   // Передаем список things
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Очищаем handler при уничтожении активности, чтобы избежать утечек памяти
        handler.removeCallbacks(updateRunnable)
    }

    // Функция для преобразования данных в список сенсоров
    private fun convertToSensors(data: List<List<String?>?>?): List<Sensor> {
        val sensorsList: MutableList<Sensor> = mutableListOf()
        data?.forEach { sensorData ->
            val name = sensorData?.get(0) ?: ""
            val value = sensorData?.get(1) ?: ""
            val isActive = sensorData?.get(2)?.toBoolean() ?: false
            val tip = sensorData?.get(3) ?: ""
            val sensor = Sensor(name, value, isActive, tip)
            sensorsList.add(sensor)
        }
        return sensorsList
    }

    // Функция для преобразования данных в список вещей
    private fun convertToThings(data: List<List<String?>?>?): List<Thing> {
        val thingsList: MutableList<Thing> = mutableListOf()
        data?.forEach { thingData ->
            val id = thingData?.get(0) ?: ""
            val name = thingData?.get(1) ?: ""
            val isActive = thingData?.get(2)?.toBoolean() ?: false
            val thing = Thing(id.toInt(), name, isActive)
            thingsList.add(thing)
        }
        return thingsList
    }
}