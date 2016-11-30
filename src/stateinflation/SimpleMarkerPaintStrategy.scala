package stateinflation

import javax.swing.JComponent

import com.intellij.openapi.editor.Editor
import com.intellij.ui.JBColor
import marker.{Marker, MarkerType}

/**
  * Created by runed on 11/25/2016.
  */
class SimpleMarkerPaintStrategy extends MarkerPaintStrategy {
  override def paintMarkers(markerCandidates: List[Option[Marker]], editor: Editor, markerPainter: IndividualMarkerPainter, canvas: JComponent): Unit = {
    val markers = prepareMarkers(markerCandidates, editor)
    markers.foreach(marker => marker.markerType match {
      case MarkerType.Primary => paintMarkers(marker, editor, backgroundColor = JBColor.GRAY, textColor = JBColor.WHITE, replacementTextColor = JBColor.RED, markerPainter, canvas)
      case MarkerType.Selected => paintMarkers(marker, editor, backgroundColor = JBColor.BLUE, textColor = JBColor.WHITE, replacementTextColor = JBColor.RED, markerPainter, canvas)
      case MarkerType.Secondary => paintMarkers(marker, editor, backgroundColor = JBColor.GRAY, textColor = JBColor.WHITE, replacementTextColor = JBColor.YELLOW, markerPainter, canvas)
    })
  }

  private def prepareMarkers(optionalMarkers: List[Option[Marker]], editor: Editor): List[Marker] = {
    val actualOptions = optionalMarkers.filter(option => option.isDefined)
    actualOptions.map(option => option.get)
    // TODO: Remove duplicates and markers at cursor positions
  }

  private def paintMarkers(marker: Marker, editor: Editor, backgroundColor: JBColor, textColor: JBColor, replacementTextColor: JBColor, markerPainter: IndividualMarkerPainter, canvas: JComponent): Unit ={
    val graphics = editor.getContentComponent.getGraphics
    markerPainter.drawBackground(editor, backgroundColor, textColor, marker, canvas)
    markerPainter.drawMarkerChar(editor, marker, replacementTextColor, canvas)
  }
}
