package stateinflation

import javax.swing.JComponent

import com.intellij.openapi.editor.Editor
import marker.Marker

/**
  * Created by runed on 11/25/2016.
  */
trait MarkerPaintStrategy {
  def paintMarkers(markers: List[Option[Marker]], editor: Editor, markerPainter: IndividualMarkerPainter, canvas: JComponent)
}
