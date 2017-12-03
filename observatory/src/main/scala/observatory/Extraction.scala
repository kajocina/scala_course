package observatory

import java.time.LocalDate

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.{col, round}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types._

/**
  * 1st milestone: data extraction
  */
object Extraction {
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  @transient lazy val spark: SparkSession =
    SparkSession
      .builder()
      .master("local")
      .appName("Weather")
      .config("spark.master", "local")
      .getOrCreate()
  import spark.implicits
  import spark.implicits._

  def average[T]( ts: Iterable[T] )( implicit num: Numeric[T] ) = {
    num.toDouble( ts.sum ) / ts.size
  }

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Year, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Temperature)] = {

    val stations_schema: StructType = StructType(Array(
      StructField("STN",StringType,true),
      StructField("WBAN",StringType,true),
      StructField("LAT",DoubleType,true),
      StructField("LON",DoubleType,true)
    ))

    val stat_file_resource = this.getClass().getResource(stationsFile).toString

    val stations_df: DataFrame = spark.read
      .format("csv")
      .option("header","false")
      .schema(stations_schema)
      .load(stat_file_resource)
      .cache()

    val years_schema: StructType = StructType(Array(
      StructField("STN",StringType,true),
      StructField("WBAN",StringType,true),
      StructField("Month",IntegerType,true),
      StructField("Day",IntegerType,true),
      StructField("Temp",DoubleType,true)
    ))

    val temp_file_resource = this.getClass().getResource(temperaturesFile).toString

    val year_temps: DataFrame = spark.read
      .format("csv")
      .option("header","false")
      .schema(years_schema)
      .load(temp_file_resource)
      .cache()

    val year_temps_no_missing = year_temps.filter($"Temp" < 9999.9)

    val stations_gps = stations_df.filter($"LAT".isNotNull && $"LON".isNotNull).dropDuplicates("LAT","LON")

    val matched = stations_gps.as("stat").join(
      year_temps_no_missing.as("yt"), $"stat.STN" <=> $"yt.STN" && $"stat.WBAN" <=> $"yt.WBAN"
    ).select("LAT","LON","Month","Day","Temp")
    matched.show()

    val matched_celsius = matched.withColumn("Temp",
      (matched("Temp")-32) * (5.0/9.0) )

    val matched_celsius_collected = matched_celsius.collect()
    matched_celsius_collected.map{ case Row(lat,lon,month,day,temp) =>
      ( LocalDate.of(year,month.asInstanceOf[Int],day.asInstanceOf[Int]),
        Location(lat.asInstanceOf[Double],lon.asInstanceOf[Double]),
        temp.asInstanceOf[Temperature]
      ) }
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Temperature)]): Iterable[(Location, Temperature)] = {
    val records_grouped = records.groupBy( row => (row._1.getYear(),row._2))
    val temps_grouped: Map[(Int,Location),Iterable[Temperature]] = records_grouped.mapValues(x => x.map(y => y._3))
    temps_grouped.mapValues(x => average(x) ).map( x => (x._1._2,x._2)).toList
  }

}
