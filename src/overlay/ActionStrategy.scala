package overlay

import com.intellij.openapi.editor.Editor

/**
  * Created by g50848 on 01/12/2016.
  */
trait ActionStrategy {
  def performAction(editor: Editor, offsets: Int*)
}
