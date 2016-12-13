package marker

import marker.MarkerType.MarkerType

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

object MarkerType extends Enumeration{
  type MarkerType = Value
  val Selected, Primary, Secondary, Unknown = Value
}
