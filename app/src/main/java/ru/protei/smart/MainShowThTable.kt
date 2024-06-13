package ru.protei.smart

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
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

class MainShowThTable : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PreviewDataTable()
                }
            }
        }
    }


    @Composable
    fun DataTable(data: List<Pair<String, String>>) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.padding(5.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("Фильтры:")
            }
            Row(
                modifier = Modifier.padding(5.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                DataThDropdown()
            }
            Row(
                modifier = Modifier.padding(5.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                ThDropdown()
            }
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Время",
                    modifier = Modifier.weight(1f),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                )
                Text(
                    text = "Статус",
                    modifier = Modifier.weight(1f),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                )
            }

            // Data Rows
            LazyColumn {
                items(data) { item ->
                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = item.first,
                            modifier = Modifier.weight(1f),
                            style = TextStyle(fontSize = 16.sp)
                        )
                        Text(
                            text = item.second,
                            modifier = Modifier.weight(1f),
                            style = TextStyle(fontSize = 16.sp)
                        )
                    }
                    Divider()
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewDataTable() {
        val data = listOf(
            Pair("11:20", "выкл"),
            Pair("12:15", "вкл"),
            Pair("12:47", "выкл"),
            Pair("12:55", "вкл"),
            Pair("13:00", "выкл")
        )
        DataTable(data = data)
    }

    //Сюда со стороны передается лист с датами
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ThDropdown() {
        val dataList = arrayOf("Розетка")
        val context = LocalContext.current
        var expanded by remember { mutableStateOf(false) }
        var selectedText by remember { mutableStateOf(dataList[0]) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
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
                    dataList.forEach { item ->
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

    //Сюда со стороны передается лист с датами
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DataThDropdown() {
        val dataList = arrayOf("13-06-2024")
        val context = LocalContext.current
        var expanded by remember { mutableStateOf(false) }
        var selectedText by remember { mutableStateOf(dataList[0]) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
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
                    dataList.forEach { item ->
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
}