package stateinflation

import java.awt.{Graphics, Rectangle}
import javax.swing.{JPanel, JTextField}

import action.InputAcceptor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopupFactory
import listener._
import state.PluginState
import scala.collection.JavaConversions._

/**
  * Created by runed on 11/26/2016.
  */
class SimpleStateInflator(var currentState: PluginState, var editor: Option[Editor]) extends JPanel with StateInflator with StateDeflator{
  def inflateState(state: PluginState, editor: Editor, action: InputAcceptor): Unit = {
    //create the popup
    state.popup.visible  match {
      case true  => createPopup(state.popup.text, state.popup.visible, editor, state.listenerList, action)
      case false  => disposePopup(editor)
    }

    currentState = state
    paint(editor.getContentComponent.getGraphics)
  }

  override def paint(g: Graphics): Unit = {
    setupLocationAndBoundsOfPanel(editor.getOrElse(return))
    //paint the markers
    val individualMarkerPainter = new IndividualMarkerPainter()
    // TODO: Constructor parameter
    val markerPaintStrategy = new SimpleMarkerPaintStrategy()

    markerPaintStrategy.paintMarkers(currentState.markerList ::: currentState.selectedMarkers, editor.getOrElse(return), individualMarkerPainter, this)
  }

  private def setupLocationAndBoundsOfPanel(editor: Editor): Unit = {
    val parent = editor.getContentComponent
    this.setLocation(0, 0)
    val visibleArea: Rectangle = editor.getScrollingModel.getVisibleAreaOnScrollingFinished
    val x: Int = (parent.getLocation.getX + visibleArea.getX + editor.getScrollingModel.getHorizontalScrollOffset).toInt
    this.setBounds(x, visibleArea.getY.toInt, visibleArea.getWidth.toInt, visibleArea.getHeight.toInt)
  }

  private def createPopup(text: String, editable: Boolean, editor: Editor, listeners: List[ListenerDescription], action: InputAcceptor): Unit = {
    def inflatePopup(text: String, editable: Boolean, editor: Editor, listeners: List[ListenerDescription], action: InputAcceptor): Unit ={
      val textField = new JTextField()
      textField.setColumns(10)
      textField.setEditable(editable)
      textField.setText(text)
      val panel : JPanel = new JPanel()
      panel.add(textField)

      new NonCharListener(textField, action).register()
      if (editable){
        new UpdateMarkersCharListener(textField, action).register()
      } else {
        new SelectMarkersCharListener(textField, action).register()
      }

      val popup = JBPopupFactory.getInstance().createComponentPopupBuilder(panel, panel)
        .setCancelKeyEnabled(false)
        .setFocusable(true)
        .setMovable(false)
        .setShowBorder(true)
        .createPopup()

      val position = JBPopupFactory.getInstance().guessBestPopupLocation(editor)
      popup.show(position)
      textField.requestFocus()
    }
    JBPopupFactory.getInstance().isChildPopupFocused(editor.getContentComponent) match {
      case true =>
        JBPopupFactory.getInstance().getChildFocusedPopup(editor.getContentComponent).dispose()
        inflatePopup(text,editable,editor,listeners, action)
      case false =>
        inflatePopup(text,editable,editor,listeners, action)
    }
  }

  private def disposePopup(editor: Editor): Unit = {
    JBPopupFactory.getInstance().getChildFocusedPopup(editor.getContentComponent).dispose()
  }

  override def deflateState(editor: Editor): Unit = {
    def disposePopup(editor: Editor): Unit ={

    }
    editor.getContentComponent.remove(this)
    val popups = asScalaBuffer(JBPopupFactory.getInstance().getChildPopups(editor.getContentComponent))
    popups.foreach(popup => popup.dispose())
  }
}
