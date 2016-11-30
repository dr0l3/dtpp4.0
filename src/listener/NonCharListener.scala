package listener

import java.awt.event.{ActionEvent, InputEvent, KeyEvent}
import javax.swing.{AbstractAction, JTextField, KeyStroke}

import action.InputAcceptor
import listener.ListenerType.ListenerType

/**
  * Created by runed on 11/27/2016.
  */
class NonCharListener(val textField: JTextField, val action: InputAcceptor) extends Listener{
  override def register(): Unit = {
    textField.getInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape")
    textField.getActionMap.put("escape", new EscapeAction(action))

    textField.getInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter")
    textField.getActionMap.put("enter", new EnterAction(action))

    textField.getInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), "enter+alt+ctrl")
    textField.getActionMap.put("enter+alt+ctrl", new AltCtrlEnterAction(action))

    textField.getInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "enter+ctrl")
    textField.getActionMap.put("enter+ctrl", new CtrlEnterAction(action))

    textField.getInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK), "enter+alt")
    textField.getActionMap.put("enter+alt", new AltEnterAction(action))

    textField.getInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK), "i+alt")
    textField.getActionMap.put("i+alt", new AltIAction(action))

    textField.getInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.ALT_DOWN_MASK), "k+alt")
    textField.getActionMap.put("k+alt", new AltKAction(action))
  }

  override def unregister(): Unit = {
    textField.getInputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0))
    textField.getInputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0))
    textField.getInputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK))
    textField.getInputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK))
    textField.getInputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK))
    textField.getInputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK))
    textField.getInputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.ALT_DOWN_MASK))
  }


  class EnterAction(action: InputAcceptor) extends AbstractAction{
    override def actionPerformed(e: ActionEvent): Unit = {
      action.handleInput(new Input(InputType.Enter, None, List(None)))
    }
  }

  class EscapeAction(val action: InputAcceptor) extends AbstractAction{
    override def actionPerformed(e: ActionEvent): Unit = {
      action.handleInput(new Input(InputType.Escape, None, List(None)))
    }
  }

  class AltCtrlEnterAction(action: InputAcceptor) extends AbstractAction{
    override def actionPerformed(e: ActionEvent): Unit = {
      action.handleInput(new Input(InputType.Enter, None, List(Some(Modifier.Alt), Some(Modifier.Control))))
    }
  }

  class CtrlEnterAction(action: InputAcceptor) extends AbstractAction {
    override def actionPerformed(e: ActionEvent): Unit = {
      action.handleInput(new Input(InputType.Enter, None, List(Some(Modifier.Control))))
    }
  }


  class AltEnterAction(action: InputAcceptor) extends AbstractAction {
    override def actionPerformed(e: ActionEvent): Unit = {
      action.handleInput(new Input(InputType.Enter, None, List(Some(Modifier.Alt))))
    }
  }

  class AltIAction(action: InputAcceptor) extends AbstractAction {
    override def actionPerformed(e: ActionEvent): Unit = {
      action.handleInput(new Input(InputType.Scroll, Some("i"), List(Some(Modifier.Alt))))
    }
  }

  class AltKAction(action: InputAcceptor) extends AbstractAction {
    override def actionPerformed(e: ActionEvent): Unit = {
      action.handleInput(new Input(InputType.Scroll, Some("k"), List(Some(Modifier.Alt))))
    }
  }

  override def getType(): ListenerType = {
    ListenerType.NonChar
  }
}


