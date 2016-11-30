package state

import listener.ListenerDescription
import marker.Marker
import popup.TextPopup

/**
  * Created by runed on 11/25/2016.
  */
class PluginState (val popup: TextPopup                         = new TextPopup(false, ""),
                   val markerList: List[Option[Marker]]         = List(),
                   val selectedMarkers: List[Option[Marker]]    = List(),
                   val isSelecting: Boolean                     = false,
                   val listenerList: List[ListenerDescription]  = List()){

}
