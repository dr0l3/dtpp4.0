package scoll

import Util.EditorUtil
import com.intellij.openapi.editor.Editor
import listener.ScrollDirection
import listener.ScrollDirection.ScrollDirection
import marker.{Marker, MarkerType}
import state.PluginState


/**
  * Created by runed on 12/18/2016.
  */
class ScrollStrategy{
  def calculateScroll(state: PluginState, editor: Editor, direction: ScrollDirection): PluginState = {
    def calculateScrollInner(state: PluginState, dirFunc: (Marker) => Boolean, sortFunc: (Marker) => Int): PluginState = {
      val secMrkrs: List[Marker] = state.markerList
        .filter(opt => opt.get.markerType == MarkerType.Secondary).map(opt => opt.get)
      val dirMrkrs: List[Marker] = secMrkrs.filter(dirFunc)
      val visMrkrs: List[Marker] = dirMrkrs.filter(marker => Marker.isVisible(marker, editor))
      //if any of these left scroll to the furthest one
      visMrkrs.size match {
        case 0 => // scroll so that no marker is skipped
          val allMrkrs = Marker.getMrkrs(EditorUtil.getMatchesForStringInTextRange, state.markerList.head.get.searchText, editor, EditorUtil.getEntireDocumentTextRange(editor))
          val dirMrkrs = allMrkrs.filter(dirFunc)
          val nonVisMrkrs = dirMrkrs.filter(!Marker.isVisible(_, editor))
          val sortedMarkers = nonVisMrkrs.sortBy(sortFunc)
          if(sortedMarkers.isEmpty) state
          else {
            val markers = Marker.assignMarkerChar(Marker.markerset, sortedMarkers)
            new PluginState(state.popup, markers.map(m => Some(m)), state.selectedMarkers, state.isSelecting, state.listenerList, sortedMarkers.head.startOffset, createUndoFunction(editor))
          }
        case _ =>
          //calculate new markers
          val dirMrkrs = state.markerList.map(m => m.get).filter(dirFunc)
          val noPriMrkrs = dirMrkrs.filter(m => m.markerType != MarkerType.Primary)
          val sortedMarkers = noPriMrkrs.sortBy(sortFunc)
          if(sortedMarkers.isEmpty) state
          else {
            val markers = Marker.assignMarkerChar(Marker.markerset, sortedMarkers)
            new PluginState(state.popup, markers.map(m => Some(m)), state.selectedMarkers, state.isSelecting, state.listenerList, sortedMarkers.head.startOffset, createUndoFunction(editor))
          }
      }
    }

    if(direction == ScrollDirection.Up){
      calculateScrollInner(state,
        (mrkr) => mrkr.startOffset < state.contextPoint,
        (mrkr) => Math.abs(mrkr.startOffset-state.contextPoint))
    } else {
      calculateScrollInner(state,
        (mrkr) => mrkr.startOffset > state.contextPoint,
        (mrkr) => Math.abs(mrkr.startOffset-state.contextPoint))
    }
  }

  private def createUndoFunction(editor: Editor): () => Unit = {
    () => EditorUtil.performScrollToPosition(editor, editor.getCaretModel.getPrimaryCaret.getOffset)
  }
}


/***
  * General Marker calculation strategy (design doc)
  *
  * Cases:
  *   There are secondary markers visible in the chosen direction -> widen
  *   The above is not true
  *     There are non-visible markers in the chosen direction -> scroll to the first one and reassign markerchars
  *     The above is not true -> stay and keep current markers
  */