package in.javamantra.spark

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime}

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row

case class Flight(flightDate: LocalDate, airlineCode: Int, flightNumber: Int, origin: String, destination: String,
                  depTime: LocalTime, depDelay: Double, arrTime: LocalTime, arrDelay: Double, airTime: Double, distance: Double)

object MostDelayedAirports {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark = SparkSession
      .builder()
      .master("local")
      //.enableHiveSupport()
      .getOrCreate()
    import spark.implicits._
    //spark.table("flights").as[FlightEncoder].map(parse)
    spark.read.option("header", "true").option("inferSchema", "true").csv("flights.csv").rdd.map(parse).take(10).foreach(println)

  }

  def parse(row: Row): Flight = {
    val timeFormat = DateTimeFormatter.ofPattern("HHmm")
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.S")
    val flightDate = LocalDate.parse(row.getAs[java.sql.Timestamp]("air_date").toString ,dateFormat)//parse(row.getAs[java.sql.Timestamp]("air_date").toString.split("")(0))
    println(flightDate)
    val airlineCode = row.getAs[Int]("airline")
    val flightNumber = row.getAs[Int]("flight_no")
    val origin = row.getAs[String]("origin")
    val destination = row.getAs[String]("destination")
    val deptTime = LocalTime.parse(row.getAs[Int]("dept_time") + "", timeFormat)
    print(deptTime)
    val depDelay = row.getAs[Double]("dept_delay")
    val arrTime = LocalTime.parse(row.getAs[String]("arr_time"), timeFormat)
    val arrDelay = row.getAs[Double]("arr_delay")
    val airTime = row.getAs[Double]("airtime")
    val distance = row.getAs[Double]("distance")

    Flight(flightDate, airlineCode, flightNumber, origin, destination,
      deptTime, depDelay, arrTime, arrDelay, airTime, distance)
  }

}
