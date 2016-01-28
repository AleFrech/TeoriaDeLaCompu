/**
  * Created by AlejandroFrech on 1/26/2016.
  */

import java.util.Observable
import javafx.collections.ObservableList
import javafx.scene.Node
import scala.collection.mutable.ArrayBuffer
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.{Button, TextInputDialog}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color

object HelloStageDemo extends JFXApp {
  var States =  ArrayBuffer.empty[State]
  var Transitions= ArrayBuffer.empty[Transition]
  var drawManager= new DrawManager()
  stage = new JFXApp.PrimaryStage {
    width = 900
    height = 600
    scene = new Scene {
      fill = Color.WhiteSmoke
      var deleteButton = new Button("Delete State"){
        handleEvent(MouseEvent.MouseClicked){
          a: MouseEvent => {
            val dialog = new TextInputDialog(defaultValue = "") {
              initOwner(stage)
              title = "State Name"
              contentText = "Please enter State name:"
            }
            val result = dialog.showAndWait()
            result match {
                  case Some(name) =>removeState(name,content)
                  case None => println("Cancel")
            }
          }
        }
      }

      var addTransitionButton = new Button("Add Transition"){
          layoutX=80
          layoutY=0
          handleEvent(MouseEvent.MouseClicked){
            a: MouseEvent => {
              val dialog = new TextInputDialog(defaultValue = "") {
                initOwner(stage)
                title = "New Transition"
                contentText = "Please enter state names separate by ~ :\n For example: q0~q1~value"
              }
              val result = dialog.showAndWait()
              result match {
                case Some(list) =>addTransition(list,content)
                case None => println("Cancel")
              }
            }
          }
      }

      var removeTransitionButton = new Button("Delete Transition"){
        layoutX=173
        layoutY=0
        handleEvent(MouseEvent.MouseClicked){
          a: MouseEvent => {
            val dialog = new TextInputDialog(defaultValue = "") {
              initOwner(stage)
              title = "Delete Transition"
              contentText = "Please enter state names separate by ~ :\n For example: q0~q1~value"
            }
            val result = dialog.showAndWait()
            result match {
              case Some(list) =>removeTransition(list,content)
              case None => println("Cancel")
            }
          }
        }
      }

      content.add(deleteButton)
      content.add(addTransitionButton)
      content.add(removeTransitionButton)
      handleEvent(MouseEvent.MouseClicked){
        a: MouseEvent => {
            val dialog = new TextInputDialog(defaultValue = "") {
              initOwner(stage)
              title = "State Name"
              contentText = "Please enter State name:"
            }
            val result = dialog.showAndWait()
            result match {
              case Some(name) => var result=addState(name,content,a.sceneX,a.sceneY)
              case None => println("Cancel")
            }
        }
      }
    }
  }

  def addState(name:String,content:ObservableList[Node],posX:Double,posY:Double): Boolean ={
    for (elem <- States) {
      if(elem.name==name && !elem.isDeleted)
        return false
    }
    var state = new State(name)
    state.stateComponents = drawManager.DrawState(posX,posY, name)
    content.add(state.stateComponents.circle)
    content.add(state.stateComponents.labelText)
    States += state
    return true
  }

  def addTransition(from_To:String,content:ObservableList[Node]):Unit={
    var edge=from_To.split("~")
    var from=edge(0)
    var to=edge(1)
    var name=edge(2)
    var fromState:State=null
    var toState:State=null
    for (elem <- States) {
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
    content.remove(3,content.size())
    for (elem <- States) {
      if (elem.name != name && !elem.isDeleted ) {
        content.add(elem.stateComponents.circle)
        content.add(elem.stateComponents.labelText)
      } else
        elem.isDeleted=true
    }
    for(elem<-States){
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
    var from = edge(0)
    var to = edge(1)
    var name = edge(2)
    content.remove(3, content.size())
    for (elem <- States) {
      if (elem.name != name && !elem.isDeleted) {
        content.add(elem.stateComponents.circle)
        content.add(elem.stateComponents.labelText)
      } else
        elem.isDeleted = true
    }
    for (elem <- States) {
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
}
