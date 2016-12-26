package action

import Util.EditorUtil
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent, CommonDataKeys}
import com.intellij.openapi.editor.Editor
import marker.SimpleMarkerCalculatorStrategy
import overlay.{OneOverlayStrategy, SingleOffsetUndoFuncCreator}
import scoll.ScrollStrategy
import state.PluginState
import stateinflation.SimpleStateInflator

/**
  * Created by runed on 12/11/2016.
  */
class JumpAction extends AnAction{
  override def actionPerformed(anActionEvent: AnActionEvent): Unit = {
    val editor = anActionEvent.getData(CommonDataKeys.EDITOR)
    new ActionExample(
      editor,
      new SimpleStateInflator(new PluginState(), editor),
      OneOverlayStrategy(
        SingleOffsetUndoFuncCreator(
          (offset: Int, editor: Editor) => EditorUtil.performMove(offset,editor),
          (editor: Editor) => EditorUtil.getCurrentPosition(editor),
          (offset: Int, editor: Editor) => () => EditorUtil.performMove(offset, editor))),
      new SimpleMarkerCalculatorStrategy,
      new ScrollStrategy)
      .actionPerformed(anActionEvent)
  }
}
