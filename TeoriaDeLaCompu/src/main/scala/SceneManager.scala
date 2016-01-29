import javafx.collections.ObservableList
import javafx.scene.Node
import scalafx.Includes._
import scalafx.application.JFXApp
import scala.collection.mutable.ArrayBuffer
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

/**
  * Created by AlejandroFrech on 1/28/2016.
  */
class SceneManager() {
  var automataManager = new AutomataManager()
  var Transitions= ArrayBuffer.empty[Transition]
  var drawManager= new DrawManager()


  def addState(name:String,content:ObservableList[Node],posX:Double,posY:Double): Boolean ={

    if(name.isEmpty)
      return false
    var hasColisioned=false
    for (elem <- automataManager.States) {
      if(elem.name==name && !elem.isDeleted)
        return false
      var collisonFormula:Double=Math.pow(elem.stateComponents.circle.centerX.value-posX,2) + Math.pow(elem.stateComponents.circle.centerY.value-posY,2)
      if(0<=collisonFormula && collisonFormula<= Math.pow(40,2)) {
        if (!elem.isDeleted)
          hasColisioned = true
      }
    }
    if(!hasColisioned) {
      var state = new State(name)
      state.stateComponents = drawManager.DrawState(posX, posY, name)
      content.add(state.stateComponents.circle)
      content.add(state.stateComponents.labelText)
      automataManager.States += state
    }
    return true
  }

  def addTransition(from_To:String,content:ObservableList[Node]):Unit={
    var edge=from_To.split("~")
    if(edge.length<3)
      return
    var from=edge(0)
    var to=edge(1)
    var name=edge(2)
    var fromState:State=null
    var toState:State=null
    for (elem <- automataManager.States) {
      if(elem.name==from) {
        fromState=elem
      }
      if(elem.name==to){
        toState=elem
      }
    }
    var transition= new Transition(name,to)
    transition.transitionComponents=drawManager.DrawTransition(fromState,toState,name)
    fromState.transitionsList+= transition
    content.add(transition.transitionComponents.line)
    content.add(transition.transitionComponents.labelText)
    content.add(transition.transitionComponents.circlePoint)
    Transitions+=transition
  }

  def removeState(name:String,content:ObservableList[Node]): Unit ={
    if(name.isEmpty)
      return
    content.remove(4,content.size())
    for (elem <- automataManager.States) {
      if (elem.name != name && !elem.isDeleted ) {
        content.add(elem.stateComponents.circle)
        content.add(elem.stateComponents.labelText)
      } else
        elem.isDeleted=true
      for(trans<-elem.transitionsList){
        trans.isDeleted=true
        if(trans.DestinyStateName==name)
          trans.isDeleted=true
      }
    }

    for(elem<-automataManager.States){
      for(trans<-elem.transitionsList){
        if(!trans.isDeleted) {
          content.add(trans.transitionComponents.line)
          content.add(trans.transitionComponents.labelText)
          content.add(trans.transitionComponents.circlePoint)
        }
      }
    }
  }

  def removeTransition(from_To:String,content:ObservableList[Node]):Unit= {
    var edge = from_To.split("~")
    if(edge.length<3)
      return
    var from = edge(0)
    var to = edge(1)
    var name = edge(2)
    content.remove(4, content.size())
    for (elem <- automataManager.States) {
      if (elem.name != name && !elem.isDeleted) {
        content.add(elem.stateComponents.circle)
        content.add(elem.stateComponents.labelText)
      } else
        elem.isDeleted = true
    }
    for (elem <- automataManager.States) {
      for (trans <- elem.transitionsList) {
        if (trans.transitionName != name && trans.DestinyStateName != to && !trans.isDeleted) {
          content.add(trans.transitionComponents.line)
          content.add(trans.transitionComponents.labelText)
          content.add(trans.transitionComponents.circlePoint)
        } else
          trans.isDeleted = true
      }
    }
  }



  def evaluateDFA(expresion:String,stage:JFXApp.PrimaryStage):Unit={
    var value= automataManager.evaluateDFA(expresion)
        new Alert(AlertType.Information) {
        initOwner(stage)
        title = "Result Dialog"
        headerText = "Result"
          if(value) {
            contentText = "Expresion accepted"
          }else{
            contentText = "Expresion not accepted"
          }
      }.showAndWait()
    }
}
