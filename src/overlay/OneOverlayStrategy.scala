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
    // TODO: This does not work
    val selectedMarker: Option[Marker] = pluginState.markerList.find(markerOpt => markerOpt.get.replacementText.toLowerCase.eq(selectedMarkerString.toLowerCase)).get
    println(selectedMarker.get)
    actionStrategy.performAction(editor,selectedMarker.get.startOffset)
    List(new PluginState(new TextPopup(false, ""), List(), pluginState.selectedMarkers ::: List(selectedMarker), false, List(new SimpleListenerDescription(ListenerType.NonAccept))))
  }
}
