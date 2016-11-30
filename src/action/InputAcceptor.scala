package action

import listener.Input

/**
  * Created by runed on 11/27/2016.
  */
trait InputAcceptor {
  def handleInput(input: Input): Unit
}
