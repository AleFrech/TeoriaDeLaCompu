import javafx.collections.ObservableList
import javafx.scene.Node

import scalafx.scene.control.Label
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Line}

/**
  * Created by AlejandroFrech on 2/1/2016.
  */
class NFAManager extends DFAManager {


  override def DrawTransition(from_To: String, content: ObservableList[Node]): Unit = {

    var edge = from_To.split("~")
    if (edge.length < 3)
      return
    var fromState: State = null
    var toState: State = null
    for (elem <- States) {
      if (elem.name == edge(0)) {
        fromState = elem
      }
      if (elem.name == edge(1)) {
        toState = elem
      }
    }
    if (fromState == null || toState == null)
      return

    var transition = new Transition(edge(2), edge(1))

    if (edge(0) != edge(1)) {
      transition.transitionComponents.line = new Line() {
        stroke = Color.LightGray
        strokeWidth = 1.0
        startX.set(fromState.stateComponents.circle.centerX.value)
        startY.set(fromState.stateComponents.circle.centerY.value)
        endX.set(toState.stateComponents.circle.centerX.value)
        endY.set(toState.stateComponents.circle.centerY.value)
      }
      transition.transitionComponents.labelText = new Label(edge(2)) {
        textFill = Color.Black
        layoutX = (transition.transitionComponents.line.startX.value + transition.transitionComponents.line.endX.value) / 2
        layoutY = (transition.transitionComponents.line.startY.value + transition.transitionComponents.line.endY.value) / 2
      }

      transition.transitionComponents.circlePoint = new Circle() {
        centerX = transition.transitionComponents.line.endX.value
        centerY = transition.transitionComponents.line.endY.value
        radius = 3
        fill = Color.Green
      }

      content.add(transition.transitionComponents.line)
      content.add(transition.transitionComponents.labelText)
      content.add(transition.transitionComponents.circlePoint)

    } else {

      transition.transitionComponents.buckleCircle = new Circle() {
        centerX = fromState.stateComponents.circle.centerX.value
        centerY = fromState.stateComponents.circle.centerY.value
        radius = 25
        stroke = Color.Black
        strokeWidth = 2
        fill = Color.LightGray
      }
      transition.transitionComponents.buckleCircle.setOpacity(0.1)
      transition.transitionComponents.labelText = new Label(edge(2)) {
        textFill = Color.Black
        layoutX = fromState.stateComponents.circle.centerX.value - 4
        layoutY = fromState.stateComponents.circle.centerY.value - 40
      }

      content.add(transition.transitionComponents.buckleCircle)
      content.add(transition.transitionComponents.labelText)

    }
    fromState.transitionsList += transition
  }

  override def evaluate(expresion: String): Boolean = {
    if (States.size == 0)
      return false
    var initial: State = null
    for (elem <- States) {
      if (elem.isInicial) {
        initial = elem
      }
    }
    return evaluateNFA(expresion, initial)
  }

  def evaluateNFA(expresion: String, state: State): Boolean = {
    if (expresion.isEmpty())
      return state.isFinal
    for (trans <- state.transitionsList) {
      if (trans.transitionName == expresion.charAt(0).toString) {
        if (evaluateNFA(expresion.substring(1), getState(trans.DestinyStateName)))
          return true
      }
    }
    return false
  }
}


