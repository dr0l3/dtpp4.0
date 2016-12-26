package action

import Util.EditorUtil
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent, CommonDataKeys}
import com.intellij.openapi.editor.Editor
import marker.SimpleMarkerCalculatorStrategy
import overlay.{OverlayStrategy, TwoOffsetStringUndoFuncCreator, TwoOverlayStrategy}
import scoll.ScrollStrategy
import state.PluginState
import stateinflation.SimpleStateInflator

/**
  * Created by runed on 12/17/2016.
  */
class DeleteAction extends AnAction{
  override def actionPerformed(anActionEvent: AnActionEvent): Unit = {
    val editor = anActionEvent.getData(CommonDataKeys.EDITOR)
    new ActionExample(
      editor,
      new SimpleStateInflator(new PluginState(), editor),
      TwoOverlayStrategy(
        TwoOffsetStringUndoFuncCreator(
          (startOffset: Int, endOffset: Int, editor: Editor) => EditorUtil.performDelete(startOffset,endOffset,editor),
          OverlayStrategy.minOfSelectedMarkers,
          (startOffset: Int, endOffset: Int, editor: Editor) => EditorUtil.getTextBetweenOffsets(startOffset,endOffset,editor),
          (offset: Int, text: String, editor: Editor) => () => EditorUtil.performPaste(offset, editor, text))),
      new SimpleMarkerCalculatorStrategy,
      new ScrollStrategy)
      .actionPerformed(anActionEvent)
  }
}
