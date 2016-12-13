package listener

import listener.InputType.InputType
import listener.Modifier.Modifier


/**
  * Created by runed on 11/27/2016.
  */
class Input (val inputType: InputType, val value: Option[String], val modifiers: List[Option[Modifier]])

object InputType extends Enumeration{
  type InputType = Value
  val Enter, Escape, Char, Scroll = Value
}

object Modifier extends Enumeration{
  type Modifier = Value
  val Alt, Control, Shift = Value
}

object ScrollDirection extends Enumeration{
  type ScrollDirection = Value
  val Up, Down = Value
}
