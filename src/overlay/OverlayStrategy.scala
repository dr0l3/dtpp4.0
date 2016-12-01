package overlay

import com.intellij.openapi.editor.Editor
import state.PluginState

/**
  * Created by g50848 on 01/12/2016.
  */
trait OverlayStrategy {
  def handleSelect(selectedMarkerString: String, state: PluginState, editor: Editor): List[PluginState]
}
