package overlay

import com.intellij.openapi.editor.Editor
import listener.{ListenerType, SimpleListenerDescription}
import popup.TextPopup
import state.PluginState

/**
  * Created by g50848 on 01/12/2016.
  */
trait OverlayStrategy {
  def handleSelect(selectedMarkerString: String, state: PluginState, editor: Editor): List[PluginState]
}

case class OneOverlayStrategy(action: (Int, Editor) => Unit) extends OverlayStrategy {
  override def handleSelect(selectedMarkerString: String, state: PluginState, editor: Editor): List[PluginState] = {
    val list = state.markerList.filter(markerOpt => markerOpt.get.replacementText.toLowerCase == selectedMarkerString.toLowerCase)
    list.size match {
      case 0 =>
        // TODO: Decide
        List(state)
      case 1 =>
        action(list.head.get.startOffset, editor)
        List(new PluginState(new TextPopup(false, ""), List(), state.selectedMarkers ::: List(list.head), false, List(new SimpleListenerDescription(ListenerType.NonAccept))))
      case _ =>
        // TODO: Select the best one
        action(list.head.get.startOffset, editor)
        List(new PluginState(new TextPopup(false, ""), List(), state.selectedMarkers ::: List(list.head), false, List(new SimpleListenerDescription(ListenerType.NonAccept))))
    }
  }
}

case class TwoOverlayStrategy(action: (Int, Int, Editor) => Unit) extends OverlayStrategy {
  override def handleSelect(selectedMarkerString: String, state: PluginState, editor: Editor): List[PluginState] = {
    val list = state.markerList.filter(markerOpt => markerOpt.get.replacementText.toLowerCase == selectedMarkerString.toLowerCase)
    list.size match {
      case 0 =>
        List(state)
      case _ =>
        // TODO: Better selection
        action(state.selectedMarkers.head.get.startOffset, list.head.get.startOffset, editor)
        List(new PluginState(new TextPopup(false, ""), List(), state.selectedMarkers ::: List(list.head), false, List(new SimpleListenerDescription(ListenerType.NonAccept))))
    }
  }
}
