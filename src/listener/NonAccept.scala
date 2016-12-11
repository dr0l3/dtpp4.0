package listener

import java.awt.event.{KeyEvent, KeyListener}

import action.InputAcceptor
import com.intellij.openapi.editor.Editor
import listener.ListenerType.ListenerType

/**
  * Created by runed on 11/27/2016.
  */
class NonAccept(val actionExample: InputAcceptor, val editor: Editor) extends KeyListener with Listener{
  private var counter: Int = 0
  override def keyTyped(e: KeyEvent): Unit = {
    counter = counter +1
    if(counter <= 1){
      return
    }
    if(e.getKeyChar == '\u001B'){
      actionExample.handleInput(new Input(InputType.Escape, None, List(None)))
    } else {
      //We only really care that it is not an escape input
      actionExample.handleInput(new Input(InputType.Char, None, List(None)))
    }
    unregister()
  }

  override def keyPressed(e: KeyEvent): Unit = {
    val temp = e.getKeyChar
    println(s"pressed $temp")
    keyTyped(e)
  }

  override def keyReleased(e: KeyEvent): Unit = {
    val temp = e.getKeyChar
    println(s"released $temp")
    keyTyped(e)
  }

  override def register(): Unit = {
    editor.getContentComponent.addKeyListener(new NonAccept(actionExample, editor))
  }

  override def unregister(): Unit = {
    editor.getContentComponent.removeKeyListener(this)
  }

  override def getType(): ListenerType = {
    ListenerType.NonAccept
  }
}


