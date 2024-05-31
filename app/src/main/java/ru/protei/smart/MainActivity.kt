package ru.protei.smart


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            Sensor("Датчик света", "350", true," лк"),
            Sensor("Датчик температуры", "20", true, " °C"),
            Sensor("Датчик влажности", "45", true," %"),
            Sensor("Датчик дыма 1", "0,00", true, " ppm"),
            Sensor("Датчик дыма 2", "0,00", true," ppm"),
            Sensor("Датчик дыма 3", "0,00", true, " ppm"),
)
val thData = listOf(
    Thing(0, "Окно", false),
)


class MainActivity : ComponentActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//        setContent {
//            SmartTheme {
//                Greeting()
//            }}}}

        val service = RetrofitClient.getRetrofitInstance().create(SensorService::class.java)

        // Объявляем переменные для списков sensors и things
        var sensors: List<Sensor> = emptyList()
        var things: List<Thing> = emptyList()

        // Получаем данные для списка sensors
        service.getSensors()?.enqueue(object : Callback<List<List<String?>?>?> {
            override fun onResponse(
                call: Call<List<List<String?>?>?>,
                response: Response<List<List<String?>?>?>
            ) {
                if (response.isSuccessful) {
                    // Преобразуем полученные данные в список сенсоров
                    sensors = convertToSensors(response.body())
                    Log.d("Send", sensors.toString())

                    // После получения данных для списка sensors, получаем данные для списка things
                    service.getThings()?.enqueue(object : Callback<List<List<String?>?>?> {
                        override fun onResponse(
                            call: Call<List<List<String?>?>?>,
                            response: Response<List<List<String?>?>?>
                        ) {
                            if (response.isSuccessful) {
                                // Преобразуем полученные данные в список сенсоров для things
                                things = convertToThings(response.body())
                                Log.d("Send", things.toString())

                                // После получения данных вызываем setContent с актуальными данными
                                setContent {
                                    SmartTheme {
                                        PageMain(
                                            onButtonClick = {
                                                val intent = Intent(this@MainActivity, SecondActivity::class.java)
                                                startActivity(intent)
                                            },
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
                                            sensors, // Передаем список sensors
                                            things   // Передаем список things
                                        )
                                    }
                                }
                            } else {
                                Log.d("Send", "no things")
                                setContent {
                                    SmartTheme {
                                        PageMain(
                                            onButtonClick = {
                                                val intent = Intent(this@MainActivity, SecondActivity::class.java)
                                                startActivity(intent)
                                            },
                                            onSensorItemClick = {
                                                val intent = Intent(this@MainActivity, MoreInfActivity::class.java)
                                                // Передача дополнительных данных в другую активити, если необходимо
                                                // intent.putExtra("SENSOR_ID", sensor.id)
                                                startActivity(intent)
                                            },
                                            onThItemClick = {
                                                val intent = Intent(this@MainActivity, ThinfoActivity::class.java)
                                                // Передача дополнительных данных в другую активити, если необходимо
                                                // intent.putExtra("SENSOR_ID", sensor.id)
                                                startActivity(intent)
                                            },
                                            sensors, // Передаем список sensors
                                            thData   // Передаем список things
                                        )
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<List<List<String?>?>?>, t: Throwable) {
                            Log.e("Send", "Error occurred while fetching things: ${t.message}")
                        }
                    })
                } else {
                    Log.d("Send", "no sensors")
                    setContent {
                        SmartTheme {
                            PageMain(
                                onButtonClick = {
                                    val intent = Intent(this@MainActivity, SecondActivity::class.java)
                                    startActivity(intent)
                                },
                                onSensorItemClick = {
                                    val intent = Intent(this@MainActivity, MoreInfActivity::class.java)
                                    // Передача дополнительных данных в другую активити, если необходимо
                                    // intent.putExtra("SENSOR_ID", sensor.id)
                                    startActivity(intent)
                                },
                                onThItemClick = {
                                    val intent = Intent(this@MainActivity, MoreInfActivity::class.java)
                                    // Передача дополнительных данных в другую активити, если необходимо
                                    // intent.putExtra("SENSOR_ID", sensor.id)
                                    startActivity(intent)
                                },
                                sensorData = sensorData, // Передаем список sensors
                                thData = thData   // Передаем список things
                            )
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<List<String?>?>?>, t: Throwable) {
                Log.e("Send", "Error occurred while fetching sensors: ${t.message}")
            }
               })
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








//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun Greeting(modifier: Modifier = Modifier) {
//    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
//        var password by remember { mutableStateOf("") }
//
//        Row {
//            Text(
//                text = "Авторизация",
//                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//        }
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Введите логин") },
//            //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Введите пароль") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(onClick = {
//            // обработка нажатия кнопки входа
//        }) {
//            Text("Войти")
//        }
//    }
//}


//

//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun Greeting(modifier: Modifier = Modifier) {
//    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
//        var password by remember { mutableStateOf("") }
//
//        Row {
//            Text(
//                text = "Добавить 'вещь'",
//                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Row {
//            Text(
//                text = "Выберите или добавьте тип:",
//                style = TextStyle( fontSize = 18.sp),
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//        }
//        Row(
//            modifier = Modifier.padding(5.dp),
//            verticalAlignment = Alignment.Bottom
//        ) {
//            TDropdown()
//        }
//        Row {
//            Text(
//                text = "Выберите или добавьте помещение:",
//                style = TextStyle( fontSize = 18.sp),
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//
//        }
//        Row(
//            modifier = Modifier.padding(5.dp),
//            verticalAlignment = Alignment.Top
//        ) {
//            RDropdown()
//            FloatingActionButton(
//                onClick = {},
//                modifier = Modifier
//                    .padding(16.dp)
//                    .size(48.dp) // Уменьшение размера кнопки
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(24.dp))
//            }
//        }
//                Spacer(modifier = Modifier.height(16.dp))
//        Row {
//            Text(
//                text = "Укажите MAC-адрес:",
//                style = TextStyle( fontWeight = FontWeight.Bold, fontSize = 18.sp),
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//        }
//                    OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = { Text("Введите значение") },
//                //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//            )
//        Spacer(modifier = Modifier.height(16.dp))
//        Row {
//            Text(
//                text = "Управляющий датчик:",
//                style = TextStyle( fontWeight = FontWeight.Bold, fontSize = 18.sp),
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//        }
//        Button(onClick = {
//            // обработка нажатия кнопки входа
//        }) {
//            Text("Добавить")
//        }
//    }
//}



//
////Добавление датчика
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun Greeting(modifier: Modifier = Modifier) {
//    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
//        var password by remember { mutableStateOf("") }
//
//        Row {
//            Text(
//                text = "Добавить датчик",
//                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Row {
//            Text(
//                text = "Выберите или добавьте тип:",
//                style = TextStyle( fontSize = 18.sp),
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//        }
//        Row(
//            modifier = Modifier.padding(5.dp),
//            verticalAlignment = Alignment.Bottom
//        ) {
//            TDropdown()
//        }
//        Row {
//            Text(
//                text = "Выберите или добавьте помещение:",
//                style = TextStyle( fontSize = 18.sp),
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//
//        }
//        Row(
//            modifier = Modifier.padding(5.dp),
//            verticalAlignment = Alignment.Top
//        ) {
//            RDropdown()
//            FloatingActionButton(
//                onClick = {},
//                modifier = Modifier
//                    .padding(16.dp)
//                    .size(48.dp) // Уменьшение размера кнопки
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(24.dp))
//            }
//        }
//        //выпад список
//        Spacer(modifier = Modifier.height(16.dp))
//        Row {
//            Text(
//                text = "Укажите диапазон желаемых значений:",
//                style = TextStyle( fontWeight = FontWeight.Bold, fontSize = 18.sp),
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//        }
//            Text(
//                text = "Верхняя желаемая граница:",
//                style = TextStyle(fontSize = 18.sp),
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//            OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = { Text("Введите значение") },
//                //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//            )
//            Text(
//                text = "Нижняя желаемая граница:",
//                style = TextStyle(fontSize = 18.sp),
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//            OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = { Text("Введите значение") },
//                //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//            )
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(onClick = {
//            // обработка нажатия кнопки входа
//        }) {
//            Text("Добавить")
//        }
//    }
//}
//
//
////типы
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TDropdown() {
    val sensorList = arrayOf("Датчик света", "Розетка", "Добавить")
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(ReservRooms[0]) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sensorList.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedText = item
                            expanded = false
                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}
//
//
////типы
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RDropdown() {
    val sensorList = arrayOf("Помещение 1", "Помещение 2", "Добавить")
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(ReservRooms[0]) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sensorList.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedText = item
                            expanded = false
                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}