package action

import Util.EditorUtil
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent, CommonDataKeys}
import marker.SimpleMarkerCalculatorStrategy
import overlay.OneOverlayStrategy
import state.PluginState
import stateinflation.SimpleStateInflator

/**
  * Created by runed on 12/11/2016.
  */
class JumpAction extends AnAction{
  override def actionPerformed(anActionEvent: AnActionEvent): Unit = {
    val editor = anActionEvent.getData(CommonDataKeys.EDITOR)
    new ActionExample(editor, new SimpleStateInflator(new PluginState(), editor), OneOverlayStrategy(EditorUtil.performMove), new SimpleMarkerCalculatorStrategy)
      .actionPerformed(anActionEvent)
  }
}
