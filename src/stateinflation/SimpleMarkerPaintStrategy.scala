package stateinflation

import java.awt.Graphics
import javax.swing.JComponent

import com.intellij.openapi.editor.Editor
import com.intellij.ui.JBColor
import marker.{Marker, MarkerType}

/**
  * Created by runed on 11/25/2016.
  */
class SimpleMarkerPaintStrategy extends MarkerPaintStrategy {
  override def paintMarkers(markerCandidates: List[Option[Marker]], editor: Editor, markerPainter: IndividualMarkerPainter, markerPanel: JComponent, graphics: Graphics): Unit = {
    val markers = prepareMarkers(markerCandidates, editor)
    markers.foreach(marker => marker.markerType match {
      case MarkerType.Primary => paintMarker(marker, editor, backgroundColor = JBColor.GRAY, textColor = JBColor.WHITE, replacementTextColor = JBColor.RED, markerPainter, markerPanel, graphics)
      case MarkerType.Selected => paintSelectedMarker(marker, editor, backgroundColor = JBColor.BLACK, textColor = JBColor.WHITE, markerPainter, markerPanel, graphics)
      case MarkerType.Secondary => paintMarker(marker, editor, backgroundColor = JBColor.GRAY, textColor = JBColor.WHITE, replacementTextColor = JBColor.YELLOW, markerPainter, markerPanel, graphics)
    })
  }

  private def prepareMarkers(optionalMarkers: List[Option[Marker]], editor: Editor): List[Marker] = {
    val actualOptions = optionalMarkers.filter(option => option.isDefined)
    actualOptions.map(option => option.get)
    // TODO: Remove duplicates and markers at cursor positions
  }

  private def paintMarker(marker: Marker, editor: Editor, backgroundColor: JBColor, textColor: JBColor, replacementTextColor: JBColor, markerPainter: IndividualMarkerPainter, markerPanel: JComponent, graphics: Graphics): Unit ={
    markerPainter.drawBackground(editor, backgroundColor, textColor, marker, markerPanel, graphics)
    markerPainter.drawMarkerChar(editor, marker, replacementTextColor, markerPanel, graphics, (m: Marker) => m.replacementText)
  }

  private def paintSelectedMarker(marker: Marker, editor: Editor, backgroundColor: JBColor, textColor: JBColor, markerPainter: IndividualMarkerPainter, markerPanel: JComponent, graphics: Graphics): Unit ={
    markerPainter.drawSelectedChar(editor, marker, backgroundColor, textColor, markerPanel, graphics)
  }
}
