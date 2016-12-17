package overlay

import com.intellij.openapi.editor.Editor
import listener.{ListenerType, SimpleListenerDescription}
import marker.Marker
import overlay.OverlayStrategy._
import popup.TextPopup
import state.PluginState

/**
  * Created by g50848 on 01/12/2016.
  */
trait OverlayStrategy {
  def handleSelect(selectedMarkerString: String, state: PluginState, editor: Editor): List[PluginState]
}

object OverlayStrategy{
  type UndoOffsetFunc = Editor => Int
  type UndoFunc = () => Unit
  type SingleOffsetUndoFunc = (Int, Editor) => UndoFunc
  type UndoValueFunc = (Int, Int, Editor) => String
  type SingleOffsetValueUndoFunc = (Int, String, Editor) => UndoFunc
}

trait actionPerformer {
  def performAction(matchedMarker: Marker, editor: Editor, state: PluginState, undoFunctionCreator: UndoFunctionCreator): List[PluginState] = {
    undoFunctionCreator match {
      case SingleOffsetUndoFuncCreator(action, offsetFunc, undoFunc) =>
        val undoOffset = offsetFunc(editor)
        action(matchedMarker.startOffset, editor)
        List(new PluginState(new TextPopup(false, "", false), List(), state.selectedMarkers ::: List(Option(matchedMarker)), false, List(new SimpleListenerDescription(ListenerType.NonAccept)), undoFunc(undoOffset, editor)))
      case TwoOffsetStringUndoFuncCreator(action, offsetFunc, valueFunc, undoFunc) =>
        val undoOffset = offsetFunc(state)
        val text = valueFunc(state.selectedMarkers.head.get.startOffset, matchedMarker.startOffset, editor)
        action(state.selectedMarkers.head.get.startOffset, matchedMarker.startOffset, editor)
        List(new PluginState(new TextPopup(false, "", false), List(), state.selectedMarkers ::: List(Option(matchedMarker)), false, List(new SimpleListenerDescription(ListenerType.NonAccept)), undoFunc(undoOffset, text, editor)))
    }
  }
}

case class OneOverlayStrategy(undoFunctionCreator: UndoFunctionCreator) extends OverlayStrategy with actionPerformer {
  override def handleSelect(selectedMarkerString: String, state: PluginState, editor: Editor): List[PluginState] = {
    val matchedMarkers = state.markerList.filter(markerOpt => markerOpt.get.replacementText.toLowerCase == selectedMarkerString.toLowerCase)
    matchedMarkers.size match {
      case 0 =>
        // TODO: Decide
        List(state)
      case 1 =>
        performAction(matchedMarkers.head.get, editor, state, undoFunctionCreator)
      case _ =>
        // TODO: Select the best one
        performAction(matchedMarkers.head.get, editor, state, undoFunctionCreator)
    }
  }
}

case class TwoOverlayStrategy(undoFunctionCreator: UndoFunctionCreator) extends OverlayStrategy with actionPerformer {
  override def handleSelect(selectedMarkerString: String, state: PluginState, editor: Editor): List[PluginState] = {
    val matchedMarkers = state.markerList.filter(markerOpt => markerOpt.get.replacementText.toLowerCase == selectedMarkerString.toLowerCase)
    state.selectedMarkers.size match { //size 0 -> first overlay. size 1 -> second overlay
      case 0 => List(new PluginState(new TextPopup(true, "", true), Nil, List(matchedMarkers.head), false, Nil, () => Unit))
      case _ =>
        matchedMarkers.size match {
          case 0 =>
            List(state)
          case _ =>
            // TODO: Better selection
            performAction(matchedMarkers.head.get, editor, state, undoFunctionCreator)
        }
    }
  }
}

trait UndoFunctionCreator
case class SingleOffsetUndoFuncCreator(action: (Int, Editor) => Unit, offsetFunc: UndoOffsetFunc, undoFunc: SingleOffsetUndoFunc) extends UndoFunctionCreator {
  def computeUndoOffset(editor: Editor): Int = {offsetFunc(editor)}
  def createUndoFunction(offset: Int, editor: Editor) : UndoFunc = {
    () => undoFunc(offset, editor)
  }
}
case class TwoOffsetStringUndoFuncCreator(action: (Int, Int, Editor) => Unit, offsetFunc: (PluginState) => Int, valueFunc: UndoValueFunc, undoFunc: SingleOffsetValueUndoFunc) extends UndoFunctionCreator {
  def computeUndoOffset(pluginState: PluginState): Int = {offsetFunc(pluginState)}
  def computeUndoText(startOffset: Int, endOffset: Int, editor: Editor): String = {
    valueFunc(startOffset, endOffset, editor)
  }
  def createUndoFunction(offset: Int, text: String, editor: Editor): UndoFunc = {
    () => undoFunc(offset, text, editor)
  }
}
