package stateinflation

import java.awt.Graphics
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
  def drawBackground(editor: Editor, backgroundColor: JBColor, textColor: JBColor, marker: Marker, markerPanel: JComponent, graphics: Graphics): Unit = {
    // TODO: Painting the markers on the editor content component rather than the expected canvas
    val markerPanel = editor.getContentComponent
    val font = editor.getColorsScheme.getFont(EditorFontType.BOLD)
    val fontRect: Rectangle2D = markerPanel.getFontMetrics(font).getStringBounds(marker.searchText, graphics)
    graphics.setColor(backgroundColor)
    graphics.setFont(font)
    val x = markerPanel.getX + editor.logicalPositionToXY(editor.offsetToLogicalPosition(marker.startOffset)).getX
    val y = markerPanel.getY + editor.logicalPositionToXY(editor.offsetToLogicalPosition(marker.endOffset)).getY
    graphics.fillRect(x.toInt,y.toInt,fontRect.getWidth.toInt, fontRect.getHeight.toInt)
    if(marker.searchText.length > 1){
      graphics.setColor(textColor)
      val x_text = markerPanel.getX + editor.logicalPositionToXY(editor.offsetToLogicalPosition(marker.startOffset+1)).getX
      val y_text = markerPanel.getY + editor.logicalPositionToXY(editor.offsetToLogicalPosition(marker.endOffset)).getY
      val bottomYOfMarkerChar = y_text + font.getSize
      graphics.drawString(marker.searchText.substring(1), x_text.toInt, bottomYOfMarkerChar.toInt)
    }
  }

  def drawMarkerChar(editor: Editor, marker: Marker, textColor: JBColor, markerPanel: JComponent, graphics: Graphics, stringFunc: (Marker) => String): Unit = {
    val markerPanel = editor.getContentComponent
    val font = editor.getColorsScheme.getFont(EditorFontType.BOLD)
    val x = markerPanel.getX + editor.logicalPositionToXY(editor.offsetToLogicalPosition(marker.startOffset)).getX
    val y = markerPanel.getY + editor.logicalPositionToXY(editor.offsetToLogicalPosition(marker.endOffset)).getY
    val bottomYOfMarkerChar = y + font.getSize
    graphics.setColor(textColor)
    graphics.setFont(font)
    graphics.drawString(stringFunc(marker), x.toInt, bottomYOfMarkerChar.toInt)
  }

  def drawSelectedChar(editor: Editor, marker: Marker,backgroundColor: JBColor, textColor: JBColor, markerPanel: JComponent, graphics: Graphics): Unit ={
    val markerPanel = editor.getContentComponent
    val font = editor.getColorsScheme.getFont(EditorFontType.BOLD)
    val x = markerPanel.getX + editor.logicalPositionToXY(editor.offsetToLogicalPosition(marker.startOffset)).getX
    val y = markerPanel.getY + editor.logicalPositionToXY(editor.offsetToLogicalPosition(marker.startOffset+1)).getY
    val bottomYOfMarkerChar = y + font.getSize
    graphics.setFont(font)
    val fontRect: Rectangle2D = markerPanel.getFontMetrics(font).getStringBounds(marker.searchText.charAt(0).toString, graphics)
    graphics.setColor(backgroundColor)
    graphics.fillRect(x.toInt,y.toInt,fontRect.getWidth.toInt, fontRect.getHeight.toInt)
    graphics.setColor(textColor)
    graphics.drawString(marker.searchText.charAt(0).toString, x.toInt, bottomYOfMarkerChar.toInt)
  }
}
