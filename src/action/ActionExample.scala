package action

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}
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
class ActionExample(val editor: Editor, val stateInflator: SimpleStateInflator, val overlayStrategy: OverlayStrategy, val markerCalculatorStrategy: MarkerCalculatorStrategy) extends AnAction with InputAcceptor{
  var actionStates : List[PluginState] = List[PluginState]()

  override def actionPerformed(anActionEvent: AnActionEvent): Unit = {
    // TODO: create listeners
    println("Starting action")
    calculateStartState(None)
  }

  def handleInput(input: Input): Unit = {
    if(input.inputType == InputType.Char && actionStates.last.markerList.nonEmpty &&actionStates.last.markerList.head.isDefined && input.value.get == actionStates.last.markerList.head.get.searchText){
      println("Duplicate input detected")
      return
    }
    print("dispatching input")
    println(input.inputType + " " + input.value + " " + input.modifiers)
    if(actionStates.last.listenerList.map(ld => ld.listenerType).contains(ListenerType.NonAccept) && input.inputType != InputType.Escape){
      handleExitAction()
      return
    }
    input.inputType match {
      case InputType.Enter =>
        if(actionStates.last.isSelecting){handleWidenMarkerNet()}
        else { handleSetSelecting() }
      case InputType.Escape =>
        if(actionStates.isEmpty){handleExitAction()}
        else {handleUndo()}
      case InputType.Char =>
        if(actionStates.last.isSelecting){handleSelect(input.value.getOrElse(return))}
        else {handleUpdateMarkers(input.value.getOrElse(return))}
      case InputType.Scroll =>
        handleScroll(ScrollDirection.Down)
    }
  }

  def calculateStartState(selectedMarker : Option[Marker]): Unit = {
    if(actionStates.isEmpty){
      actionStates = actionStates ::: List(new PluginState(new TextPopup(true, "", false),List[Option[Marker]](), List[Option[Marker]](), false, List[ListenerDescription](), () => Unit))
    } else {
      actionStates = actionStates ::: List(new PluginState(new TextPopup(true, "", false),List[Option[Marker]](), actionStates.last.selectedMarkers ::: List[Option[Marker]](), false, List[ListenerDescription](), () => Unit))
    }
    stateInflator.inflateState(actionStates.last, editor, this)
  }

  def handleExitAction(): Unit = {
    stateInflator.deflateState(editor)
    actionStates = actionStates.drop(actionStates.size)
  }

  def handleUndo(): Unit = {
    println("Detecting regret")
    actionStates.length match {
      case 1 =>
        stateInflator.deflateState(editor)
      case _ =>
        actionStates.last.undoable()
        actionStates = actionStates.dropRight(1)
        stateInflator.inflateState(actionStates.last, editor, this)
    }
  }

  def isEnterKeypress(string: String): Boolean = {
    string == "" || string == "\r" || string == "\r\n" || string == "\n"
  }

  def handleSelect(string: String): Unit ={
    if(isEnterKeypress(string))return
    actionStates = actionStates ::: overlayStrategy.handleSelect(string, actionStates.last, editor)
    stateInflator.inflateState(actionStates.last, editor, this)

  }

  def handleSetSelecting(): Unit ={
    val currentState = actionStates.last
    actionStates = actionStates ::: List(new PluginState(currentState.popup, currentState.markerList, currentState.selectedMarkers, true, currentState.listenerList, () => Unit))
    stateInflator.inflateState(actionStates.last, editor, this)
  }

  def handleUpdateMarkers(string: String): Unit ={
    //calculate new markers
    val currentCaretPosition = editor.getCaretModel.getPrimaryCaret.getOffset
    val markers = markerCalculatorStrategy.calculateMarkers(editor, string, currentCaretPosition, actionStates.last.selectedMarkers.map(marker => marker.get)).map(marker => Some(marker))
    val currentState = actionStates.last

    actionStates = actionStates.dropRight(1) ::: List(new PluginState(new TextPopup(true, string, false), markers, currentState.selectedMarkers, currentState.isSelecting, currentState.listenerList, () => Unit))
    stateInflator.inflateState(actionStates.last,editor, this)
  }

  def handleWidenMarkerNet(): Unit ={
    //remove the primary ones
    val currentState = actionStates.last

    // TODO: getOrElse
    val markerList = currentState.markerList.filter(marker => marker.get.markerType == MarkerType.Secondary)
    actionStates = actionStates.dropRight(1) ::: List(new PluginState(currentState.popup, markerList, currentState.selectedMarkers, currentState.isSelecting, currentState.listenerList, () => Unit))
    stateInflator.inflateState(actionStates.last,editor, this)
  }

  def handleScroll(direction: ScrollDirection): Unit ={
    //calculate scroll
    //repaint markers
  }
}
