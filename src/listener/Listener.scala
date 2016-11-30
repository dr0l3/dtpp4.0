package listener

import listener.ListenerType.ListenerType

/**
  * Created by runed on 11/25/2016.
  */
trait Listener {
  def register(): Unit
  def unregister(): Unit
  def getType(): ListenerType
}

object ListenerType extends Enumeration{
  type ListenerType = Value
  val NonAccept, NonChar, SelectMarkersCharListener, UpdateMarkersCharListener = Value
}
