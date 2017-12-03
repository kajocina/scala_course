package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import scala.math
import scala.collection.parallel._
/**
  * 2nd milestone: basic visualization
  */
object Visualization {

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Temperature)], location: Location): Temperature = {

    def great_circle_distance(location1: Location, location2: Location): Double = {
      if(location1 == location2){return 0}
        // could be an antipode:
      else if(location1.lat == location2.lat+180.0 && location1.lon == location2.lon+180.0){return math.Pi}
      else{
        val abs_lat: Double = math.abs(location1.lat - location2.lat).toRadians
        val abs_lon: Double = math.abs(location1.lon - location2.lon).toRadians
        val sig1 = location1.lat.toRadians
        val sig2 = location2.lat.toRadians

        val a = (math.sin(abs_lat/2) * math.sin(abs_lat/2)) + (math.cos(sig1) * math.cos(sig2) *
          math.sin(abs_lon/2) * math.sin(abs_lon/2))
        val c = 2 * math.atan2(math.sqrt(a),math.sqrt(1-a))
        // a c solution from https://www.movable-type.co.uk/scripts/latlong.html

//        val d_sigma = 2 * math.asin( math.sqrt( math.pow(math.sin( abs_lat/2 ),2) + math.cos(location1.lat.toRadians) *
//          math.cos(location2.lat.toRadians) * math.pow(math.sin(abs_lon),2) ) )

        val distance = c * 6371 // 6371km is a radius of Earth
        distance
      }
    }

    val distance_temps: Iterable[(Double,Temperature)] = temperatures.map {case (loc,temp) => (great_circle_distance(loc,location),temp)}

    //if some point <1km, return it
    for( (dist,temp) <- distance_temps ){
      if(dist <= 1){return math.round(temp)}
    }

    val power: Double = 6.0
    val sum_divided: Double = distance_temps.map {case (dist,temp) => temp/math.pow(dist,power) }.sum
    val denominator: Double = distance_temps.map {case (dist, _) => 1/math.pow(dist,power)}.sum
    sum_divided / denominator
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Temperature, Color)], value: Temperature): Color = {
    val pointsMap = points.toMap
    if(pointsMap.keys.toList.contains(value)){return pointsMap(value)}
    val sorted_points: List[(Temperature,Color)] = points.toList.sortBy(_._1) // increasing sort
    if(value < sorted_points.head._1){return sorted_points.head._2}
    if(value > sorted_points.last._1){return sorted_points.last._2}

    // at this stage value can be only between some two points
    val first_bigger: List[(Temperature,Color)] =
      for {
        point <- sorted_points
        if point._1 > value
      } yield point

    val lower_point: (Temperature,Color) = points.toList(points.toList.indexOf(first_bigger.head) - 1)
    val xy_0 = lower_point
    val xy_1 = first_bigger.head

    def clamp_color(value: Int): Int = {
      if(value > 255){255}
      else if(value < 0){0}
      else{value}
    }

    // x are temperature
    // y is the color, f(x)
    // value is the unknown x here
    val prediction_red: Int = (( xy_0._2.red * (xy_1._1 - value) + xy_1._2.red * (value - xy_0._1) ) / (xy_1._1 - xy_0._1)).round.toInt
    val prediction_green: Int = (( xy_0._2.green * (xy_1._1 - value) + xy_1._2.green * (value - xy_0._1) ) / (xy_1._1 - xy_0._1)).round.toInt
    val prediction_blue: Int = (( xy_0._2.blue * (xy_1._1 - value) + xy_1._2.blue * (value - xy_0._1) ) / (xy_1._1 - xy_0._1)).round.toInt

    Color(clamp_color(prediction_red),clamp_color(prediction_green),clamp_color(prediction_blue))
  }


  def gps_to_array(lat: Double, lon: Double): (Int) = {
    // turns GPS coordinates into a pixel position for a canvas with origin in the upper-left corner
    val array_position: Int = (((90-lat)*360 ) + (lon+180)).toInt
    array_position
  }

  def array_to_gps(index: Int, height: Int, width: Int): Location = {
    val lat: Double = 90 - (index / height/2)
    val lon: Double = (index % 360)  - 180 // not sure if this is right
    Location(lat,lon)
  }

  def array_to_predicted_pixel(index: Int,temperatures: Iterable[(Location, Temperature)],
                               colors: Iterable[(Temperature, Color)]): Pixel = {
    val predicted_temp: Temperature = predictTemperature(temperatures,array_to_gps(index,180,360))
    val interpolated_color: Color = interpolateColor(colors,predicted_temp)
    Pixel(interpolated_color.red,interpolated_color.green,interpolated_color.blue,100)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */

  def visualize(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)]): Image = {

    val location_color = temperatures.map {case (loc,temp) => (loc,interpolateColor(colors,temp)) }
    val colors_array_positions: Iterable[(Int,Color)] = location_color.map {case (loc,color) => (gps_to_array(loc.lat,loc.lon),color) }
    val colors_array_positions_sorted: Array[(Int,Color)] = colors_array_positions.toArray.sortBy(_._1) // increasing order

    val emptyPixel = Pixel(255,255,255,255)
    val position_array = Array.fill(64800+360){emptyPixel}.par

    // fill-out what is known
    for(position <- colors_array_positions_sorted){
      position_array(position._1) = Pixel(position._2.red,position._2.green,position._2.blue,100)
    }

    // fill-out predictions in parallel
    val predicted_array = position_array.zipWithIndex.map(x =>
      if(x._1 == emptyPixel){array_to_predicted_pixel(x._2,temperatures,colors)}
      else if(x._1 != emptyPixel){x._1}
    )

    val image = Image(
      360,
      181,
      predicted_array.map(x => x.asInstanceOf[Pixel]).toArray
    )
      image.output(new java.io.File("/home/kaj/test.png"))
    image
    }

}

