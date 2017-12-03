type Temperature = Double // °C, introduced in Week 1
type Year = Int // Calendar year, introduced in Week 1

case class Location(lat: Double, lon: Double)

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types._
import java.time.LocalDate

import observatory.{Location, Temperature}
import org.apache.spark.sql._
import org.apache.spark.sql.functions._


Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
val spark: SparkSession =
  SparkSession
    .builder()
    .master("local")
    .appName("Weather")
    .config("spark.master", "local")
    .getOrCreate()
import spark.implicits
import spark.implicits._

val stations_schema: StructType = StructType(Array(
  StructField("STN",StringType,true),
  StructField("WBAN",StringType,true),
  StructField("LAT",DoubleType,true),
  StructField("LON",DoubleType,true)
))

val stations_df: DataFrame = spark.read
  .format("csv")
  .option("header","false")
  .schema(stations_schema)
  .load("/home/kaj/IdeaProjects/observatory/src/main/resources/stations.csv")
  .cache()



val stations_gps = stations_df.filter("LAT is not null")


val years_schema: StructType = StructType(Array(
  StructField("STN",StringType,true),
  StructField("WBAN",StringType,true),
  StructField("Month",IntegerType,true),
  StructField("Day",IntegerType,true),
  StructField("Temp",DoubleType,true)
))


val year_temps: DataFrame = spark.read
  .format("csv")
  .option("header","false")
  .schema(years_schema)
  .load("/home/kaj/IdeaProjects/observatory/src/main/resources/1975.csv")
  .cache()


val year_temps_no_missing = year_temps.filter($"Temp" < 9999.9)

val matched = stations_gps.as("stat").join(
  year_temps_no_missing.as("yt"), $"stat.STN" === $"yt.STN" && $"stat.STN" != "" || $"stat.WBAN" === $"yt.WBAN" && $"stat.WBAN" != ""
).select("LAT","LON","Month","Day","Temp")
matched.show()

val matched_celsius = matched.withColumn("Temp",
  (matched("Temp")-32) * (5.0/9.0) )

val collected = matched_celsius.collect()
val matched_celsius_collected = matched_celsius.collect()

//val out = matched_celsius_collected.map{ case Row(lat,lon,month,day,temp) =>
//  ( LocalDate.of(1975,month.asInstanceOf[Int],day.asInstanceOf[Int]),
//    Location(lat.asInstanceOf[Double],lon.asInstanceOf[Double]),
//    temp.asInstanceOf[Temperature]
//  ) }
//
//out