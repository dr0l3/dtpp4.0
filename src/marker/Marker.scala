package marker

import marker.MarkerType._

/**
  * Created by runed on 11/25/2016.
  */
trait Marker {
  def searchText: String
  def replacementText: String
  def startOffset: Int
  def endOffset: Int
  def markerType: MarkerType
}
