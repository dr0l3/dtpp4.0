package listener

import javax.swing.JTextField
import javax.swing.event.{DocumentEvent, DocumentListener}

import action.InputAcceptor
import listener.ListenerType.ListenerType

/**
  * Created by runed on 11/27/2016.
  */
class UpdateMarkersCharListener(textField: JTextField, action: InputAcceptor) extends DocumentListener with Listener{
  override def insertUpdate(e: DocumentEvent): Unit = {
    val text = e.getDocument.getText(0, e.getDocument.getLength)
    action.handleInput(new Input(InputType.Char, Some(text), List()))
  }

  override def changedUpdate(e: DocumentEvent): Unit = {insertUpdate(e)}

  override def removeUpdate(e: DocumentEvent): Unit = {insertUpdate(e)}

  override def register(): Unit = {
    textField.getDocument.addDocumentListener(this)
  }

  override def unregister(): Unit = {
    textField.getDocument.removeDocumentListener(this)
  }

  override def getType(): ListenerType = {
    ListenerType.UpdateMarkersCharListener
  }
}
