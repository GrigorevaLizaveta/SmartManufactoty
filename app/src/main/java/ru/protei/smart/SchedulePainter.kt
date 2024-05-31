package ru.protei.smart

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.protei.smart.SensorsDB.RetrofitClient
import ru.protei.smart.SensorsDB.SensorService
import java.time.LocalDate


//Класс со всеми данными для графика
data class ChartData(
    val listok: List<Point>,
    val maxl: List<Point>,
    val minl: List<Point>,
    val timeStamps: List<Float>,
    val max: Float,
    val min: Float,
    val xAxisData: AxisData,
    val yAxisData: AxisData
)

//резервные данные
val coffeeDrinks = arrayOf("Датчик звука", "Датчик света", "Датчик температуры", "Датчик дыма", "Датчик влажности")
val dataVals = arrayOf(LocalDate.now().toString(), "нет")

fun getSensList(onSuccess: (List<String>) -> Unit, onFailure: (String) -> Unit) {
    val service = RetrofitClient.getRetrofitInstance().create(SensorService::class.java)
    service.getAllSen()?.enqueue(object : Callback<List<List<String?>?>?> {
        override fun onResponse(
            call: Call<List<List<String?>?>?>,
            response: Response<List<List<String?>?>?>
        ) {
            if (response.isSuccessful) {
                val sensorData = response.body()
                if (!sensorData.isNullOrEmpty() && sensorData.size > 0) {
                    val sensorList = mutableListOf<String>()
                    sensorData.forEach { sensorItem ->
                        sensorItem?.forEach { sensor ->
                            sensor?.let {
                                sensorList.add(it)
                            }
                        }
                    }
                    onSuccess(sensorList)
                } else {
                    onFailure("Sensor data is empty")
                }
            } else {
                onFailure("Request failed: ${response.code()}")
            }
        }
        override fun onFailure(call: Call<List<List<String?>?>?>, t: Throwable) {
            onFailure("Error occurred: ${t.message}")
        }
    })
}



fun getDataList(onSuccess: (List<String>) -> Unit, onFailure: (String) -> Unit) {
    val service = RetrofitClient.getRetrofitInstance().create(SensorService::class.java)
    service.getallData()?.enqueue(object : Callback<List<List<String?>?>?> {
        override fun onResponse(
            call: Call<List<List<String?>?>?>,
            response: Response<List<List<String?>?>?>
        ) {
            if (response.isSuccessful) {
                val allData = response.body()
                if (!allData.isNullOrEmpty() && allData.size > 0) {
                    val dataList = mutableListOf<String>()
                    allData.forEach { dataItem ->
                        dataItem?.forEach { data ->
                            data?.let {
                                dataList.add(it)
                            }
                        }
                    }
                    onSuccess(dataList)
                } else {
                    onFailure("Sensor data is empty")
                }
            } else {
                onFailure("Request failed: ${response.code()}")
            }
        }
        override fun onFailure(call: Call<List<List<String?>?>?>, t: Throwable) {
            onFailure("Error occurred: ${t.message}")
        }
    })
}


const val steps = 10
@Composable
fun PageGraf(onBackButtonClick: () -> Unit) {
    // Используем состояние для управления данными графика
    var points by remember { mutableStateOf(getPointListSmoke1()) }
    val chartData = setupChartData(points)

    Column {
        Row(modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text("Фильтры:")
        }
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dataListState = remember { mutableStateOf<List<String>>(emptyList()) }

            getDataList(
                onSuccess = { dataList ->
                    dataListState.value = dataList
                },
                onFailure = { errorMessage ->
                    Log.e("Send", errorMessage)
                }
            )
            val coffee = arrayOf("10 минут", "1 час", "Датчик температуры", "Датчик дыма", "Датчик влажности")
            DataDropdown(dataList = coffee)
        }
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            val sensorListState = remember { mutableStateOf<List<String>>(emptyList()) }

            getSensList(
                onSuccess = { sensorList ->
                    sensorListState.value = sensorList
                },
                onFailure = { errorMessage ->
                    Log.e("Send", errorMessage)
                }
            )
            SensorDropdown(sensorList = sensorListState.value)
        }
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MakeGrBut {
                // Обновление данных при нажатии на кнопку(Вызов метода, который берет данные
                // из бд и преобразует их в массив)
                points = getPointListSmoke2()
            }
        }
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            lineChartData = createLineChartData(points, chartData.xAxisData, chartData.yAxisData)
        )
        LineChartWithLabelsAndText()
    }
}

@Composable
private fun MakeGrBut(onClick: () -> Unit) {
    Button(
        onClick = onClick, // Обработка нажатия кнопки
        modifier = Modifier.padding(end = 16.dp)
    ) {
        Text("Показать график")
    }
}

private fun setupChartData(points: List<Point>): ChartData {
    val maxl = getMaxPLine(points)
    val minl = getMinPLine(points)
    val timeStamps = points.map { it.x }
    val max = getMaxT()
    val min = getMinT()
    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .backgroundColor(Color.Transparent)
        .steps(points.size - 1)
        .labelData { i -> formatTime(timeStamps[i]) }
        .labelAndAxisLinePadding(25.dp)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(35.dp)
        .labelData { i ->
            val yScale = (max - min) / steps.toFloat()
            val scaledValue = (((i * yScale) + min) / 2.5).toString().toFloat()
            String.format("%.0f", scaledValue)
        }
        .build()

    return ChartData(points, maxl, minl, timeStamps, max, min, xAxisData, yAxisData)
}


//Сюда со стороны передается лист с датчиками
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorDropdown(sensorList: List<String>) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(ru.protei.smart.coffeeDrinks[0]) }
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

//Сюда со стороны передается лист с датами
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataDropdown(dataList: Array<String>) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(ru.protei.smart.dataVals[0]) }
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



private fun createLineChartData(
    listok: List<Point>,
    xAxisData: AxisData,
    yAxisData: AxisData
): LineChartData {
    return LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = listok,
                    LineStyle(color = Color.Blue),
                    IntersectionPoint(),
                    SelectionHighlightPoint()
                ),
                Line(
                    dataPoints = getMaxPLine(listok),
                    LineStyle( color = Color.Black )
                ),
                Line(
                    dataPoints = getMinPLine(listok),
                    LineStyle( color = Color.Black )
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        backgroundColor = Color.White
    )
}


fun getPointListSmoke1(): ArrayList<Point> {
    val listic = ArrayList<Point>()
    listic.add(Point(1F, 21F))
    listic.add(Point(2F, 21F))
    listic.add(Point(3F, 18F))
    listic.add(Point(4F, 21F))
    listic.add(Point(5F, 21F))
    listic.add(Point(6F, 26F))
    listic.add(Point(7F, 21F))
    listic.add(Point(8F, 21F))
    listic.add(Point(9F, 22F))
    listic.add(Point(10F, 21F))
    return listic
}

fun getPointListSmoke2(): ArrayList<Point> {
    val listic = ArrayList<Point>()
    listic.add(Point(1F, 30F))
    listic.add(Point(2F, 31F))
    listic.add(Point(3F, 38F))
    listic.add(Point(4F, 31F))
    listic.add(Point(5F, 31F))
    listic.add(Point(6F, 36F))
    listic.add(Point(7F, 31F))
    listic.add(Point(8F, 31F))
    listic.add(Point(9F, 32F))
    listic.add(Point(10F, 31F))
    return listic
}


//Заполнение в соответствии со списком
fun getMaxPLine(listok: List<Point>): List<Point> {
    val maxlist = mutableListOf<Point>()
    for (point in listok) {
        maxlist.add(Point(point.x, 40F))
    }
    return maxlist
}

fun getMinPLine(listok: List<Point>): List<Point> {
    val minlist = mutableListOf<Point>()
    for (point in listok) {
        minlist.add(Point(point.x, 0F))
    }
    return minlist
}


private fun getMaxT(): Float {
    var max = 100F
    return max
}

private fun getMinT(): Float {
    var min = 0F
    return min
}


private fun formatTime(time: Float): String {
    val hours = 13
    val minutes = ((time*30 % 3600) / 60).toInt()
    val seconds = (time*30 % 60).toInt()
    return String.format("%02d:%02d:%02d",hours, minutes, seconds)
}


@Composable
fun LineChartWithLabelsAndText(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            //Тип данных
            text = "OX: Время, OY: Значения",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            color = Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}


fun getValList(dat: String, name: String, onSuccess: (Array<Point>) -> Unit, onFailure: (String) -> Unit) {
    val service = RetrofitClient.getRetrofitInstance().create(SensorService::class.java)
    service.getchartData(dat, name)?.enqueue(object : Callback<List<List<String?>?>?> {
        override fun onResponse(
            call: Call<List<List<String?>?>?>,
            response: Response<List<List<String?>?>?>
        ) {
            if (response.isSuccessful) {
                val allData = response.body()
                if (!allData.isNullOrEmpty() && allData.size > 0) {
                    val dataList = mutableListOf<String>()
                    allData.forEach { dataItem ->
                        dataItem?.forEach { data ->
                            data?.let {
                                dataList.add(it)
                            }
                        }
                    }
                    //  onSuccess(dataList)
                } else {
                    onFailure("Sensor data is empty")
                }
            } else {
                onFailure("Request failed: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<List<List<String?>?>?>, t: Throwable) {
            onFailure("Error occurred: ${t.message}")
        }
    })
}





