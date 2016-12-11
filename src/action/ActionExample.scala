package action

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent, CommonDataKeys}
import com.intellij.openapi.editor.Editor
import listener.ScrollDirection.ScrollDirection
import listener._
import marker.{Marker, MarkerCalculatorStrategy, MarkerType}
import overlay.OverlayStrategy
import popup.TextPopup
import state.PluginState
import stateinflation.SimpleStateInflator

/**
  * Created by runed on 11/25/2016.
  */
class ActionExample(val stateInflator: SimpleStateInflator, val overlayStrategy: OverlayStrategy, val markerCalculatorStrategy: MarkerCalculatorStrategy) extends AnAction with InputAcceptor{
  var actionStates : List[PluginState] = List[PluginState]()
  var editorOption: Option[Editor] = None

  override def actionPerformed(anActionEvent: AnActionEvent): Unit = {
    // TODO: create listeners
    println("Starting action")
    editorOption = Some(anActionEvent.getData(CommonDataKeys.EDITOR))
    stateInflator.editor = editorOption
    calculateStartState(None)
  }

  def handleInput(input: Input): Unit = {
    if(actionStates.last.listenerList.map(ld => ld.listenerType).contains(ListenerType.NonAccept) && input.inputType != InputType.Escape){
      handleExitAction()
    }
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
        println("inputstring = {}", inputString)
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
    actionStates = actionStates.drop(actionStates.size)
  }

  def handleUndo(): Unit = {
    //pop and paint the last actionstate
    if(editorOption.isEmpty){
      return
    }
    actionStates.length match {
      case 1 =>
        stateInflator.deflateState(editorOption.get)
      case _ =>
        actionStates = actionStates.dropRight(1)
        stateInflator.inflateState(actionStates.last, editorOption.getOrElse(return), this)
    }
  }

  def isEnterKeypress(string: String): Boolean = {
    string == "" || string == "\r" || string == "\r\n" || string == "\n"
  }

  def handleSelect(string: String): Unit ={
    if(editorOption.isEmpty || isEnterKeypress(string)){
      println("Empty input or enter")
      return
    }

    println(s"Handling select for character $string")
    actionStates = actionStates ::: overlayStrategy.handleSelect(string, actionStates.last, editorOption.get)
    stateInflator.inflateState(actionStates.last, editorOption.get, this)

  }

  def handleSetSelecting(): Unit ={
    if(editorOption.isEmpty){
      return
    }
    //save state
    val currentState = actionStates.last
    //compute new state
    actionStates = actionStates ::: List(new PluginState(currentState.popup, currentState.markerList, currentState.selectedMarkers, true, currentState.listenerList))
    stateInflator.inflateState(actionStates.last, editorOption.get, this)
  }

  def handleUpdateMarkers(string: String): Unit ={
    //calculate new markers
    if(editorOption.isEmpty){
      return
    }
    val currentCaretPosition = editorOption.get.getCaretModel.getPrimaryCaret.getOffset
    val markers = markerCalculatorStrategy.calculateMarkers(editorOption.get,string, currentCaretPosition).map(marker => Some(marker))
    val currentState = actionStates.last

    actionStates = actionStates.dropRight(1) ::: List(new PluginState(new TextPopup(true, string), markers, currentState.selectedMarkers, currentState.isSelecting, currentState.listenerList))
    stateInflator.inflateState(actionStates.last,editorOption.get, this)
    stateInflator.invalidate()
    stateInflator.repaint()
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
