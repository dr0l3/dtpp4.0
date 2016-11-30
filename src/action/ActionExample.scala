package action

import javafx.print.Printer.MarginType

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent, CommonDataKeys}
import com.intellij.openapi.editor.Editor
import listener.ScrollDirection.ScrollDirection
import listener._
import marker.{Marker, MarkerCalculatorStrategy, MarkerType, SimpleMarkerCalculatorStrategy}
import popup.TextPopup
import state.PluginState
import stateinflation.SimpleStateInflator

/**
  * Created by runed on 11/25/2016.
  */
class ActionExample extends AnAction with InputAcceptor{
  var actionStates : List[PluginState] = List[PluginState]()
  val stateInflator : SimpleStateInflator = new SimpleStateInflator(new PluginState(), None)
  var editorOption: Option[Editor] = None
  val markerCalculatorStrategy: MarkerCalculatorStrategy = new SimpleMarkerCalculatorStrategy

  override def actionPerformed(anActionEvent: AnActionEvent): Unit = {
    // TODO: create listeners
    editorOption = Some(anActionEvent.getData(CommonDataKeys.EDITOR))
    stateInflator.editor = editorOption
    calculateStartState(None)
  }

  def handleInput(input: Input): Unit = {
    // TODO: DispatchStrategy
    input.inputType match {
      case InputType.Enter =>
        if(actionStates.last.isSelecting){
          handleWidenMarkerNet()
        } else {
          handleSetSelecting()
        }
      case InputType.Escape =>
        if(actionStates.isEmpty){
          handleExitAction()
        } else {
          handleUndo()
        }
      case InputType.Char =>
        if(input.value.isEmpty){
          return
        }
        val inputString = input.value.get
        if(actionStates.last.isSelecting){
          handleSelect(inputString)
        } else {
          handleUpdateMarkers(inputString)
        }
      case InputType.Scroll =>
        handleScroll(ScrollDirection.Down)
    }
  }

  def calculateStartState(selectedMarker : Option[Marker]): Unit = {
    if(editorOption.isEmpty){
      handleExitAction()
    }

    if(actionStates.isEmpty){
      actionStates = actionStates ::: List(new PluginState(new TextPopup(true, ""),List[Option[Marker]](), List[Option[Marker]](), false, List[ListenerDescription]()))
    } else {
      actionStates = actionStates ::: List(new PluginState(new TextPopup(true, ""),List[Option[Marker]](), actionStates.last.selectedMarkers ::: List[Option[Marker]](), false, List[ListenerDescription]()))
    }

    stateInflator.inflateState(actionStates.last, editorOption.get, this)
  }

  def handleExitAction(): Unit = {
    if(editorOption.isEmpty){
      return
    }
    stateInflator.deflateState(editorOption.get)
  }

  def handleUndo(): Unit = {
    //pop and paint the last actionstate
    actionStates = actionStates.dropRight(1)
    stateInflator.inflateState(actionStates.last, editorOption.getOrElse(return), this)
  }

  def handleSelect(string: String): Unit ={
    //find the selected marker
    //use overlay strategy to decide next action
  }

  def handleSetSelecting(): Unit ={
    //save state
    val currentState = actionStates.last
    //compute new state
    actionStates = actionStates ::: List(new PluginState(currentState.popup, currentState.markerList, currentState.selectedMarkers, true, currentState.listenerList))
  }

  def handleUpdateMarkers(string: String): Unit ={
    //calculate new markers
    if(editorOption.isEmpty){
      return
    }
    val markers = markerCalculatorStrategy.calculateMarkers(editorOption.get,string).map(marker => Some(marker))
    val currentState = actionStates.last

    actionStates = actionStates.dropRight(1) ::: List(new PluginState(currentState.popup, markers, currentState.selectedMarkers, currentState.isSelecting, currentState.listenerList))
    stateInflator.inflateState(actionStates.last,editorOption.get, this)
  }

  def handleWidenMarkerNet(): Unit ={
    //remove the primary ones
    val currentState = actionStates.last

    // TODO: getOrElse
    val markerList = currentState.markerList.filter(marker => marker.get.markerType == MarkerType.Secondary)
    actionStates = actionStates.dropRight(1) ::: List(new PluginState(currentState.popup, markerList, currentState.selectedMarkers, currentState.isSelecting, currentState.listenerList))
    stateInflator.inflateState(actionStates.last,editorOption.get, this)
  }

  def handleScroll(direction: ScrollDirection): Unit ={
    //calculate scroll
    //repaint markers
  }
}
