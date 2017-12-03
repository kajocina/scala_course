package observatory
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
object Main extends App {


  // setup Spark environment
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  @transient lazy val conf: SparkConf = new SparkConf()
    .setMaster("local")
    .setAppName("Observatory")
    .set("spark.executor.heartbeatInterval", "20s")
  @transient lazy val sc: SparkContext = new SparkContext(conf)

  // Load data
  val foo = Extraction.locateTemperatures(2000,
    "/stations.csv",
    "/2000.csv")
  println("Calculating averages")
  val foo_averages = Extraction.locationYearlyAverageRecords(foo)
  val colors = ColorCode()
  Visualization.visualize(foo_averages,colors.colorMap)
}
