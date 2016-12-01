package stateinflation

import java.awt.{Graphics, Rectangle}
import javax.swing.{JComponent, JPanel, JTextField}

import action.{ActionExample, DummyInputAcceptor, InputAcceptor}
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopupFactory
import listener._
import state.PluginState

import scala.collection.JavaConverters.asScalaBuffer
/**
  * Created by runed on 11/26/2016.
  */
class SimpleStateInflator(var currentState: PluginState, var editor: Option[Editor]) extends JComponent with StateInflator with StateDeflator{
  var addedToComponent: Boolean = false
  var textField: JTextField = new JTextField()
  // TODO: Omfg fix ugliness
  var updateListener: UpdateMarkersCharListener = new UpdateMarkersCharListener(textField, new DummyInputAcceptor)
  var selectListener: SelectMarkersCharListener = new SelectMarkersCharListener(textField, new DummyInputAcceptor)
  def inflateState(state: PluginState, editor: Editor, action: InputAcceptor): Unit = {
    println("Inflating state")
    //create the popup
    state.popup.visible  match {
      case true  => createPopup(state.popup.text, !state.isSelecting, editor, state.listenerList, action)
      case false  => disposePopup(editor)
    }

    currentState = state
    if(!addedToComponent){
      println("adding stuff to component")
      editor.getContentComponent.add(this)
      addedToComponent = true
      paint(editor.getContentComponent.getGraphics)
    }
  }

  override def paint(graphics: Graphics): Unit = {
//    println("Paint the stateinflator")
    setupLocationAndBoundsOfPanel(editor.getOrElse(return))
    //paint the markers
    val individualMarkerPainter = new IndividualMarkerPainter()
    // TODO: Constructor parameter
    val markerPaintStrategy = new SimpleMarkerPaintStrategy()
    markerPaintStrategy.paintMarkers(currentState.markerList ::: currentState.selectedMarkers, editor.getOrElse(return), individualMarkerPainter, this, graphics)
  }

  override def update(g: Graphics): Unit = {
    println("Updating the stateinflator")
  }

  private def setupLocationAndBoundsOfPanel(editor: Editor): Unit = {
    val parent = editor.getContentComponent
    this.setLocation(0, 0)
    this.invalidate()
    val visibleArea: Rectangle = editor.getScrollingModel.getVisibleAreaOnScrollingFinished
    val x: Int = (parent.getLocation().getX + visibleArea.getX + editor.getScrollingModel.getHorizontalScrollOffset).toInt
    this.setBounds(x, visibleArea.getY.toInt, visibleArea.getWidth.toInt, visibleArea.getHeight.toInt)
  }

  private def createPopup(text: String, editable: Boolean, editor: Editor, listeners: List[ListenerDescription], action: InputAcceptor): Unit = {
    def updatePopup(text: String, editable: Boolean) = {
      // TODO: Ugly hack. Think about different solution
      textField.setEditable(editable)
      if (editable){
        selectListener.unregister()
        updateListener = new UpdateMarkersCharListener(textField, action)
        updateListener.register()
      } else {
        updateListener.unregister()
        selectListener = new SelectMarkersCharListener(textField, action)
        selectListener.register()
      }
    }
    def inflatePopup(text: String, editable: Boolean, editor: Editor, listeners: List[ListenerDescription], action: InputAcceptor): Unit ={
      textField = new JTextField()
      textField.setColumns(10)
      textField.setEditable(editable)
      textField.setText(text)
      val panel : JPanel = new JPanel()
      panel.add(textField)

      new NonCharListener(textField, action).register()
      if (editable){
        selectListener.unregister()
        updateListener = new UpdateMarkersCharListener(textField, action)
        updateListener.register()
      } else {
        updateListener.unregister()
        selectListener = new SelectMarkersCharListener(textField, action)
        selectListener.register()
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
        // TODO: check wheter it is actually the correct popup
        updatePopup(text, editable)
      case false =>
        inflatePopup(text,editable,editor,listeners, action)
    }
  }

  private def disposePopup(editor: Editor): Unit = {
    JBPopupFactory.getInstance().getChildFocusedPopup(editor.getContentComponent).dispose()
  }

  override def deflateState(editor: Editor): Unit = {
    println("deflating stateinflator")
    editor.getContentComponent.remove(this)
    addedToComponent = false
    val popups = asScalaBuffer(JBPopupFactory.getInstance().getChildPopups(editor.getContentComponent))
    popups.foreach(popup => popup.dispose())
  }
}
