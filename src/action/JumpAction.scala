package action

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}
import marker.SimpleMarkerCalculatorStrategy
import overlay.{JumpActionStrategy, OneOverlayStrategy}
import state.PluginState
import stateinflation.SimpleStateInflator

/**
  * Created by runed on 12/11/2016.
  */
class JumpAction extends AnAction{
  val innerAction: ActionExample = new ActionExample(new SimpleStateInflator(new PluginState(), None), new OneOverlayStrategy(new JumpActionStrategy), new SimpleMarkerCalculatorStrategy)
  override def actionPerformed(anActionEvent: AnActionEvent): Unit = {
    innerAction.actionPerformed(anActionEvent)
  }
}
