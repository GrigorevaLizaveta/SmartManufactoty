package ru.protei.smart

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.protei.smart.domain.Sensor
import ru.protei.smart.domain.Thing
import ru.protei.smart.ui.theme.PurpleGrey80


val ReservRooms = arrayOf("Помещение 1", "Помещение 2", "Добавить")

@Composable
fun PageMain(
    onButtonClick: () -> Unit, // Функция для обработки нажатия на кнопку "Посмотреть статистику"
    onSensorItemClick: (Sensor) -> Unit, // Функция для обработки нажатия на элемент списка датчиков
    onThItemClick: (Thing) -> Unit,
    sensorData: List<Sensor>,
    thData: List<Thing>
) {
    Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            Text(
                text = "Добро пожаловать на 'умное' производство!!!",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center),
                modifier = Modifier.padding(10.dp)
            )
        }
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            RoomsDropdown(ReservRooms)
        }
        //Сорт по помещениям
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Датчики:",
                style = TextStyle(fontWeight = FontWeight.Bold,
                    fontSize = 16.sp))
            Spacer(modifier = Modifier.width(35.dp))
            FloatingActionButton(
                onClick = {},
                modifier = Modifier
                    .padding(16.dp)
                    .size(48.dp) // Уменьшение размера кнопки
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(24.dp))
            }
            Button(
                onClick = onButtonClick,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text("Подробнее")
            }
        }
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color =  PurpleGrey80)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SensorListScreen(sensorData = sensorData, onItemClick = onSensorItemClick)
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Вещи:",
                style = TextStyle(fontWeight = FontWeight.Bold,
                    fontSize = 16.sp))
            Spacer(modifier = Modifier.width(35.dp))
            FloatingActionButton(
                onClick = {},
                modifier = Modifier
                    .padding(16.dp)
                    .size(48.dp) // Уменьшение размера кнопки
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(24.dp))
            }
            Button(
                onClick = onButtonClick,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text("Подробнее")
            }
        }
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color = PurpleGrey80)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ThingListScreen(thData = thData, onItemClick2 = onThItemClick)
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}



@Composable
fun SensorItem(sensor: Sensor, onClick: (Sensor) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick(sensor) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = sensor.name)
            Text(
                text = if (sensor.status) {
                    "Включено"
                } else {
                    "Выключено"
                }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(text = sensor.data + " " + sensor.tip)
    }
}

@Composable
fun ThingItem(thing: Thing, onClick: (Thing) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .clickable { onClick(thing)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = thing.name.toString())
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = if (thing.status == true) {
                "Включено"
            } else {
                "Выключено"
            }
        )
    }
}


// Основной экран со списком датчиков
@Composable
fun SensorListScreen(sensorData: List<Sensor>, onItemClick: (Sensor) -> Unit) {
    LazyColumn {
        items(sensorData) { sensor ->
            SensorItem(sensor = sensor) {
                onItemClick(sensor)
            }
        }
    }
}

@Composable
fun ThingListScreen(thData: List<Thing>, onItemClick2: (Thing) -> Unit) {
    LazyColumn {
        items(thData) { thing ->
            ThingItem(thing = thing) {
                onItemClick2(thing)
            }
        }
    }
}


//Сюда со стороны передается лист с датчиками
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsDropdown(sensorList: Array<String>) {
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


