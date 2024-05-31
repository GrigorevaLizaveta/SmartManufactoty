package ru.protei.smart.SensorsDB
import retrofit2.Call
import retrofit2.http.GET;
import retrofit2.http.PUT
import retrofit2.http.Query


interface SensorService {
    @GET("/sens")
    fun getSensors(): retrofit2.Call<List<List<String?>?>?>?

    @GET("/allData")
    fun getallData(): retrofit2.Call<List<List<String?>?>?>?

    @GET("/th")
    fun getThings(): retrofit2.Call<List<List<String?>?>?>?

    @GET("/takes")
    fun getSenInf(
        @Query("id_d") id_d: Int,
        @Query("date") date: String
    ): Call<List<List<String?>?>?>?

    @GET("/sensTypes")
    fun getAllSen(): retrofit2.Call<List<List<String?>?>?>?

    @GET("/lastVal")
    fun getLastVal(@Query("id") id: Int): Call<List<List<String?>?>?>?

    @GET("/info")
    fun getInfo(@Query("name") name: String): Call<List<String?>?>?

    @GET("/chartData")
    fun getchartData(@Query("dat") dat: String, @Query("name") name: String): Call<List<List<String?>?>?>?

    @PUT("/updateSensorStatus")
    fun updateSensorStatus(
        @Query("name") name: String,
        @Query("status") status: Boolean
    ): Call<Map<String, String>>

    @PUT("/updateThStatus")
    fun updateThStatus(
        @Query("id") id: Int,
        @Query("status") status: Boolean
    ): Call<Map<String, String>>
}