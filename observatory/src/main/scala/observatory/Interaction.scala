package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param tile Tile coordinates
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    * This method converts a tile's geographic position to its corresponding GPS coordinates, by applying the Web Mercator projection.
    */
  def tileLocation(tile: Tile): Location = {
    // tile has x, y and zoom
    val n = math.pow(2,tile.zoom)
    val lon_deg = tile.x / n * 360 - 180
    val lat_rad = math.atan(math.sinh(math.Pi * (1-2*tile.y/ n)))
    val lat_deg = lat_rad * 180 / math.Pi

    Location(lat_deg,lon_deg)
  }

  /**
    * This method returns a 256×256 image showing the given temperatures, using the given color scale, at the location
    * corresponding to the given zoom, x and y values.
    *
    * Hint: you will have to compute the corresponding latitude and longitude of each pixel within a tile.
    * A simple way to achieve that is to rely on the fact that each pixel in a tile can be thought of a subtile at a
    * higher zoom level (256 = 2⁸).
    *
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param tile Tile coordinates
    * @return A 256×256 image showing the contents of the given tile
    */
  def tile(temperatures: Iterable[(Location, Temperature)], colors: Iterable[(Temperature, Color)], tile: Tile): Image = {
//    val a = 127
???
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Year, Data)],
    generateImage: (Year, Tile, Data) => Unit
  ): Unit = {
    ???
  }

}
