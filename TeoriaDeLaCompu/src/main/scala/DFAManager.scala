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

  def addState(state:State): Unit = {
    States += state
  }

  def addTransition(fromState:State,transition: Transition): Unit = {

    fromState.transitionsList += transition
  }
}
