package ru.protei.smart

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.protei.smart.ui.theme.SmartTheme

class MainAddSen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(onButtonClickRoom = { val intent = Intent(this@MainAddSen, MainAddRoom::class.java)
                        this@MainAddSen.startActivity(intent)},
                        onButtonClickT = { val intent = Intent(this@MainAddSen, MainAddType::class.java)
                            this@MainAddSen.startActivity(intent)})
                }
            }
        }
    }


    //
////Добавление датчика
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Greeting(modifier: Modifier = Modifier,  onButtonClickRoom: () -> Unit, onButtonClickT: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var password by remember { mutableStateOf("") }
            var mini by remember { mutableStateOf("") }
            var maxi by remember { mutableStateOf("") }

            Row {
                Text(
                    text = "Добавить датчик",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(
                    text = "Выберите или добавьте тип:",
                    style = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Row(
                modifier = Modifier.padding(5.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                TDropdown{ onButtonClickT() }
            }
            Row {
                Text(
                    text = "Выберите или добавьте помещение:",
                    style = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

            }
            Row(
                modifier = Modifier.padding(5.dp),
                verticalAlignment = Alignment.Top
            ) {
                RDropdown { onButtonClickRoom() }
                FloatingActionButton(
                    onClick = {},
                    modifier = Modifier
                        .padding(16.dp)
                        .size(48.dp) // Уменьшение размера кнопки
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(
                    text = "Укажите диапазон желаемых значений:",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Text(
                text = "Верхняя желаемая граница:",
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value =maxi,
                onValueChange = { maxi = it },
                label = { Text("Введите значение") },
                //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Text(
                text = "Нижняя желаемая граница:",
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = mini,
                onValueChange = { mini = it },
                label = { Text("Введите значение") },
                //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Row {
                Text(
                    text = "MAC-адрес:",
                    style = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Введите значение") },
                //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                Toast.makeText(this@MainAddSen, "Нет соединения с сервером", Toast.LENGTH_SHORT).show()
            }) {
                Text("Добавить")
            }
        }
    }


    ////типы
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TDropdown( onButtonClickT: () -> Unit ) {
        val sensorListU = arrayOf("Датчик света", "Добавить")
        val context = LocalContext.current
        var expanded by remember { mutableStateOf(false) }
        var selectedText by remember { mutableStateOf(sensorListU[0]) }
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
                    sensorListU.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                selectedText = item
                                expanded = false
                                if (index == sensorListU.size - 1) {
                                    Toast.makeText(
                                        context,
                                        "Нет соединения с сервером",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onButtonClickT() // Вызываем функцию onButtonClickRoom
                                } else {
                                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                }
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
    fun RDropdown(onButtonClickRoom: () -> Unit) {
        val sensorList = arrayOf("Помещение 1", "Добавить")
        val context = LocalContext.current
        var expanded by remember { mutableStateOf(false) }
        var selectedText by remember { mutableStateOf(sensorList[0]) }
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
                    sensorList.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                selectedText = item
                                expanded = false
                                if (index == sensorList.size - 1) {
                                    Toast.makeText(
                                        context,
                                        "Нет соединения с сервером",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onButtonClickRoom() // Вызываем функцию onButtonClickRoom
                                } else {
                                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                }
                                }
                        )
                    }
                }
            }
        }
    }
}