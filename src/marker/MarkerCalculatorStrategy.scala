package marker

import Util.EditorUtil
import com.intellij.openapi.editor.Editor
import popup.TextPopup
import state.PluginState

/**
  * Created by runed on 11/27/2016.
  */
trait MarkerCalculatorStrategy {
  def calculateMarkers(editor: Editor, searchText: String, contextPoint: Int, excludeList: List[Marker]): List[Marker]
  def calculateMarkersv2(editor: Editor, searchText: String, state: PluginState, excludeList: List[Marker]): PluginState
}

class SimpleMarkerCalculatorStrategy extends MarkerCalculatorStrategy{

  override def calculateMarkers(editor: Editor, searchText: String, contextPoint: Int, excludeList: List[Marker]): List[Marker] = {
    print("ExcludeList: ")
    println(excludeList)
    if (searchText.equals("")) return List()

    val markerList = calculateMarkerList(searchText, editor, excludeList)

    val sorted = markerList.sortWith((marker1, marker2) => math.abs(marker1.startOffset - contextPoint) < math.abs(marker2.startOffset - contextPoint))
    Marker.assignMarkerChar(Marker.markerset, sorted.filter(isNotInExcludeList(_, excludeList)))
  }

  private def calculateMarkerList(searchText: String, editor: Editor, excludeList: List[Marker]): List[Marker] = {
    val markerList = Marker.getMrkrs(EditorUtil.getMatchesForStringInTextRange, searchText, editor, EditorUtil.getVisibleTextRange(editor))
      .filter(isNotInExcludeList(_, excludeList))
    if (markerList.nonEmpty) markerList
    else Marker.getMrkrs(EditorUtil.getMatchesForStringInTextRange, searchText, editor, EditorUtil.getEntireDocumentTextRange(editor))
      .filter(isNotInExcludeList(_, excludeList))
  }

  private def isNotInExcludeList(marker: Marker, excludeList: List[Marker]): Boolean = {
    !excludeList.map(excluded => excluded.startOffset).contains(marker.startOffset)
  }


  def calculateContextPoint(editor: Editor, markers: List[Marker], state: PluginState): Int = {
    val upwards = markers.filter(m => m.startOffset < state.contextPoint)
    if(upwards.size >= (markers.size - upwards.size))upwards.reverse.head.startOffset
    else markers.filter(m => !upwards.contains(m)).head.startOffset
  }


  override def calculateMarkersv2(editor: Editor, searchText: String, state: PluginState, excludeList: List[Marker]): PluginState = {
    //get the markers
    val markers = calculateMarkers(editor, searchText, state.contextPoint, state.selectedMarkers.map(mrkr => mrkr.get))
    //if visible markers return with original context point
    if(markers.nonEmpty && !markers.exists(Marker.isVisible(_, editor))){
      val cntxtPnt = calculateContextPoint(editor, markers, state)
      new PluginState(new TextPopup(true, searchText, false), markers.map(mrkr => Some(mrkr)), state.selectedMarkers, state.isSelecting, state.listenerList, cntxtPnt, () => EditorUtil.performScrollToPosition(editor, state.contextPoint))
    } else {
      new PluginState(new TextPopup(true, searchText, false), markers.map(mrkr => Some(mrkr)), state.selectedMarkers, state.isSelecting, state.listenerList, state.contextPoint)
    }
  }
}


/**
  * General marker calculationstrategy (design doc)
  *
  * Cases:
  *   There are visible occurences of the search string -> sort them and assign chars and type
  *   The above is not true -> scroll to first occurence and treat location as the new context point.
  */

