package stateinflation

import action.InputAcceptor
import com.intellij.openapi.editor.Editor
import state.PluginState

/**
  * Created by runed on 11/27/2016.
  */
trait StateInflator {
  def inflateState(state: PluginState, editor: Editor, action: InputAcceptor): Unit
}
