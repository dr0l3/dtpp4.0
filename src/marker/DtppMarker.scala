package marker
import marker.MarkerType.MarkerType

/**
  * Created by runed on 11/29/2016.
  */
class DtppMarker(val searchText: String, var replacementText: String = "", val startOffset: Int, val endOffset: Int, var markerType: MarkerType = MarkerType.Unknown) extends Marker{
}
