package listener

import java.awt.event.{KeyEvent, KeyListener}

import action.ActionExample
import listener.ListenerType.ListenerType

/**
  * Created by runed on 11/27/2016.
  */
class NonAccept(val actionExample: ActionExample) extends KeyListener with Listener{
  override def keyTyped(e: KeyEvent): Unit = {}

  override def keyPressed(e: KeyEvent): Unit = {}

  override def keyReleased(e: KeyEvent): Unit = {}

  override def register(): Unit = {}

  override def unregister(): Unit = {}

  override def getType(): ListenerType = {
    ListenerType.NonAccept
  }
}


