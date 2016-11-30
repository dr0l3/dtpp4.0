package listener

import java.awt.event.{KeyEvent, KeyListener}
import javax.swing.JTextField

import action.InputAcceptor
import listener.ListenerType.ListenerType

/**
  * Created by runed on 11/27/2016.
  */
class SelectMarkersCharListener(textField: JTextField, action: InputAcceptor) extends KeyListener with Listener{
  override def keyTyped(e: KeyEvent): Unit = {
    action.handleInput(new Input(InputType.Char, Some(String.valueOf(e.getKeyChar)), List()))
  }

  override def keyPressed(e: KeyEvent): Unit = {}

  override def keyReleased(e: KeyEvent): Unit = {}

  override def register(): Unit = {
    textField.addKeyListener(this)
  }

  override def unregister(): Unit = {
    textField.removeKeyListener(this)
  }

  override def getType(): ListenerType = {
    ListenerType.SelectMarkersCharListener
  }
}
