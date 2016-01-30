import javafx.collections.ObservableList
import javafx.scene.Node

import scala.collection.mutable.ArrayBuffer

/**
  * Created by AlejandroFrech on 1/28/2016.
  */
class DFAManager() {
  var States =  ArrayBuffer.empty[State]
  var drawManager = new DrawManager()




  def getTransition(elem: State,name:String):Transition={
    for(trans<- elem.transitionsList){
      if(trans.transitionName==name&& !trans.isDeleted)
        return trans
    }
    return null
  }

  def getState(name:String):State={
    for(elem<-States){
      if(elem.name==name && !elem.isDeleted)
        return elem
    }
    return null
  }


  def evaluate(expresion:String): Boolean = {
    if (States.size == 0)
      return false
    var i = 0;
    for (elem <- States) {
      if (elem.isInicial) {
        var state = elem
        for (x <- expresion) {
          var transition = getTransition(state, x.toString)
          if (transition == null)
            return false
          state = getState(transition.DestinyStateName)
          if (state == null)
            return false

        }
        return state.isFinal
      }
    }
    return false

  }


  def addState(name: String, content: ObservableList[Node], posX: Double, posY: Double): Boolean = {

    if (name.isEmpty)
      return false
    var hasColisioned = false
    for (elem <- States) {
      if (elem.name == name && !elem.isDeleted)
        return false
      var collisonFormula: Double = Math.pow(elem.stateComponents.circle.centerX.value - posX, 2) + Math.pow(elem.stateComponents.circle.centerY.value - posY, 2)
      if (0 <= collisonFormula && collisonFormula <= Math.pow(40, 2)) {
        if (!elem.isDeleted)
          hasColisioned = true
      }
    }
    if (!hasColisioned) {
      var state = new State(name)
      state.stateComponents = drawManager.DrawState(posX, posY, name)
      content.add(state.stateComponents.circle)
      content.add(state.stateComponents.labelText)
      States += state
    }
    return true
  }

  def addTransition(from_To: String, content: ObservableList[Node]): Unit = {
    var edge = from_To.split("~")
    if (edge.length < 3)
      return
    var from = edge(0)
    var to = edge(1)
    var name = edge(2)
    var fromState: State = null
    var toState: State = null
    for (elem <- States) {
      if (elem.name == from) {
        fromState = elem
      }
      if (elem.name == to) {
        toState = elem
      }
    }
    if (fromState == null || toState == null)
      return
    for (trans <- fromState.transitionsList) {
      if (trans.transitionName == name && !trans.isDeleted)
        return
    }
    var transition = new Transition(name, to)
    transition.transitionComponents = drawManager.DrawTransition(fromState, toState, name)
    fromState.transitionsList += transition
    content.add(transition.transitionComponents.line)
    content.add(transition.transitionComponents.labelText)
    content.add(transition.transitionComponents.circlePoint)
  }
}
