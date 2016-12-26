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
      val visSecMarkers = state.markerList
        .filter(_.get.markerType == MarkerType.Secondary)
        .map(_.get)
        .filter(dirFunc)
        .filter(Marker.isVisible(_, editor))
      val mrkrs = visSecMarkers.size match {
        case 0 =>
          Marker.getMrkrs(EditorUtil.getMatchesForStringInTextRange, state.markerList.head.get.searchText, editor, EditorUtil.getEntireDocumentTextRange(editor))
            .filter(dirFunc)
            .filter(!Marker.isVisible(_, editor))
            .sortBy(sortFunc)
        case _ =>
          state.markerList.map(m => m.get).filter(dirFunc)
            .filter(_.markerType != MarkerType.Primary)
            .sortBy(sortFunc)
      }
      if(mrkrs.isEmpty) state
      else {
        val markers = Marker.assignMarkerChar(Marker.markerset, mrkrs)
        new PluginState(state.popup, markers.map(m => Some(m)), state.selectedMarkers, state.isSelecting, state.listenerList, mrkrs.head.startOffset, createUndoFunction(editor))
      }
    }

    if(direction == ScrollDirection.Up){
      calculateScrollInner(state,
        mrkr => mrkr.startOffset < state.contextPoint,
        mrkr => Math.abs(mrkr.startOffset-state.contextPoint))
    } else {
      calculateScrollInner(state,
        mrkr => mrkr.startOffset > state.contextPoint,
        mrkr => Math.abs(mrkr.startOffset-state.contextPoint))
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