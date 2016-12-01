package overlay
import Util.EditorUtil
import com.intellij.openapi.editor.Editor

/**
  * Created by g50848 on 01/12/2016.
  */
class JumpActionStrategy extends ActionStrategy{
  override def performAction(editor: Editor, offsets: Int*): Unit = {
    offsets.length match{
      case 1 => EditorUtil.performMove(offsets.head, editor)
      case _ => //TODO:decide
    }
  }
}
