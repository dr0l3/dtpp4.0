package stateinflation

import com.intellij.openapi.editor.Editor

/**
  * Created by runed on 11/27/2016.
  */
trait StateDeflator {
  def deflateState(editor: Editor): Unit
}
