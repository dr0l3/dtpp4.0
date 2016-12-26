package marker

import Util.EditorUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import marker.MarkerType.MarkerType

import scala.collection.JavaConverters.asScalaBuffer

/**
  * Created by runed on 11/25/2016.
  */
trait Marker {
  def searchText: String
  def replacementText: String
  def startOffset: Int
  def endOffset: Int
  def markerType: MarkerType
}

object Marker {
  def markerset: String = "asdfwerhjkltcvbyuiopågæøxnmz".toUpperCase

  def isVisible(marker: Marker, editor: Editor): Boolean = {
    marker.startOffset > EditorUtil.getMinVisibleOffset(editor) && marker.startOffset < EditorUtil.getMaxVisibleOffset(editor)
  }

  def getMrkrs(offsetFunc: (String, Editor, TextRange) => java.util.List[Integer], searchText: String, editor: Editor, textRange: TextRange): List[Marker] = {
    val offsets = asScalaBuffer(offsetFunc(searchText,editor, textRange)).toList.map(_.toInt)
    offsetsToMarkers(offsets, searchText)
  }

  def offsetsToMarkers(offsets: List[Int], searchString: String): List[Marker] = {
    offsets.map(offset => new DtppMarker(searchString, "", offset, offset+searchString.length))
  }

  def assignMarkerChar(markerSet: String, markerList: List[Marker]): List[Marker] = {
    val primaryMarkerChars = markerSet.dropRight(1).iterator
    markerList
      .map(marker =>
        if (primaryMarkerChars.hasNext) new DtppMarker(marker.searchText, primaryMarkerChars.next().toString, marker.startOffset, marker.endOffset, MarkerType.Primary)
        else new DtppMarker(marker.searchText, markerSet.takeRight(1), marker.startOffset, marker.endOffset, MarkerType.Secondary))
  }
}

object MarkerType extends Enumeration{
  type MarkerType = Value
  val Selected, Primary, Secondary, Unknown = Value
}
