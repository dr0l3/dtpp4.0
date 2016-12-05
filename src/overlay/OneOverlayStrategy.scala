package overlay
import com.intellij.openapi.editor.Editor
import listener.{ListenerType, SimpleListenerDescription}
import marker.Marker
import popup.TextPopup
import state.PluginState

/**
  * Created by g50848 on 01/12/2016.
  */
class OneOverlayStrategy(val actionStrategy: ActionStrategy) extends OverlayStrategy{
  override def handleSelect(selectedMarkerString: String, pluginState: PluginState, editor: Editor): List[PluginState] = {

    pluginState.markerList.foreach(marker => println(marker.get.replacementText))
    val list = pluginState.markerList.filter(markerOpt => markerOpt.get.replacementText.toLowerCase.eq(selectedMarkerString.toLowerCase))
    list.size match {
      case 1 =>
        // TODO: This does not work. Doesnt jump and popup stays
        val selectedMarker: Option[Marker] = list.head
        actionStrategy.performAction(editor,selectedMarker.get.startOffset)
        List(new PluginState(new TextPopup(false, ""), List(), pluginState.selectedMarkers ::: List(selectedMarker), false, List(new SimpleListenerDescription(ListenerType.NonAccept))))
      case _ =>
        List(new PluginState(pluginState.popup, List(), pluginState.selectedMarkers, pluginState.isSelecting, pluginState.listenerList))
    }

  }
}
