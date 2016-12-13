package marker

import Util.EditorUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import scala.collection.JavaConverters.asScalaBuffer

/**
  * Created by runed on 11/27/2016.
  */
trait MarkerCalculatorStrategy {
  def calculateMarkers(editor: Editor, searchText: String, contextPoint: Int): List[Marker]
}

class SimpleMarkerCalculatorStrategy extends MarkerCalculatorStrategy{
  val markerset: String = "asdfwerhjkltcvbyuiopågæøxnmz".toUpperCase

  override def calculateMarkers(editor: Editor, searchText: String, contextPoint: Int): List[Marker] = {
    if(searchText.equals("")) return List()

    var markerList = getOffsets(EditorUtil.getMatchesForStringInTextRange, searchText, editor, EditorUtil.getVisibleTextRange(editor))

    if(markerList.isEmpty){
      markerList = markerList ::: getOffsets(EditorUtil.getMatchesForStringInTextRange, searchText, editor, new TextRange(0, editor.getDocument.getTextLength))
      // TODO: Scrolling?
    }

    markerList = markerList.sortWith((marker1, marker2) => math.abs(marker1.startOffset - contextPoint) < math.abs(marker2.startOffset - contextPoint))
    val primaryMarkers = markerset.dropRight(1).iterator
    markerList.map(marker =>
      if (primaryMarkers.hasNext) new DtppMarker(marker.searchText, primaryMarkers.next().toString, marker.startOffset, marker.endOffset, MarkerType.Primary)
      else new DtppMarker(marker.searchText, markerset.takeRight(1), marker.startOffset, marker.endOffset, MarkerType.Secondary))
    // TODO: Complete this

  }

  private def offsetsToMarkers(offsets: List[Int], searchString: String): List[Marker] = {
    offsets.map(offset => new DtppMarker(searchString, "", offset, offset+searchString.length))
  }

  private def getOffsets(func: (String, Editor, TextRange) => java.util.List[Integer], searchText: String, editor: Editor, textRange: TextRange): List[Marker] = {
    val offsets = asScalaBuffer(func(searchText,editor, textRange)).toList.map(_.toInt)
    offsetsToMarkers(offsets, searchText)
  }
}

