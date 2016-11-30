package stateinflation

import java.awt.geom.Rectangle2D
import javax.swing.JComponent

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.ui.JBColor
import marker.Marker

/**
  * Created by runed on 11/25/2016.
  */
class IndividualMarkerPainter{
  def drawBackground(editor: Editor, backgroundColor: JBColor, textColor: JBColor, marker: Marker, canvas: JComponent): Unit = {
    val graphics = canvas.getGraphics
    val font = editor.getColorsScheme.getFont(EditorFontType.BOLD)
    val fontRect: Rectangle2D = canvas.getFontMetrics(font).getStringBounds(marker.searchText, graphics)
    graphics.setColor(backgroundColor)
    val x = canvas.getX + editor.logicalPositionToXY(editor.offsetToLogicalPosition(marker.startOffset)).getX
    val y = canvas.getY + editor.logicalPositionToXY(editor.offsetToLogicalPosition(marker.endOffset)).getY
    graphics.fillRect(x.toInt,y.toInt,fontRect.getWidth.toInt, fontRect.getHeight.toInt)
    if(marker.searchText.length > 1){
      graphics.setColor(textColor)
      val x_text = canvas.getX + editor.logicalPositionToXY(editor.offsetToLogicalPosition(marker.startOffset+1)).getX
      val y_text = canvas.getY + editor.logicalPositionToXY(editor.offsetToLogicalPosition(marker.endOffset)).getY
      val bottomYOfMarkerChar = y_text + font.getSize
      graphics.drawString(marker.searchText.substring(1), x_text.toInt, bottomYOfMarkerChar.toInt)
    }
  }

  def drawMarkerChar(editor: Editor, marker: Marker, textColor: JBColor, canvas: JComponent): Unit = {
    val graphics = canvas.getGraphics
    val font = editor.getColorsScheme.getFont(EditorFontType.BOLD)
    val x = canvas.getX + editor.logicalPositionToXY(editor.offsetToLogicalPosition(marker.startOffset)).getX
    val y = canvas.getY + editor.logicalPositionToXY(editor.offsetToLogicalPosition(marker.endOffset)).getY
    val bottomYOfMarkerChar = y + font.getSize
    graphics.setColor(textColor)
    graphics.drawString(marker.replacementText, x.toInt, bottomYOfMarkerChar.toInt)
  }
}
