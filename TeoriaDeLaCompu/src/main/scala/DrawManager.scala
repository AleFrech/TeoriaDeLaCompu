import javafx.collections.ObservableList
import javafx.scene.Node

import scala.collection.mutable.ArrayBuffer
import scalafx.geometry.Pos
import scalafx.scene.control.Label
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Line, Circle}
import scalafx.scene.text.Text

/**
  * Created by AlejandroFrech on 2/2/2016.
  */
class DrawManager {


  def drawState(state: State, content: ObservableList[Node],States:ArrayBuffer[State], posX: Double, posY: Double):Unit={

      state.stateComponents.circle = new Circle(){
        centerX = posX
        centerY = posY
        radius = 20
        stroke=Color.LightGray
        fill = Color.LightGray
      }
      state.stateComponents.labelText = new Text(){
        fill= Color.Black
        alignmentInParent=Pos.TopCenter
        layoutX=posX-6
        layoutY=posY-5
        text.value=state.name
      }
      content.add(state.stateComponents.circle)
      content.add(state.stateComponents.labelText)
  }

  def drawTransition(fromState:State,toState:State,transition: Transition ,content: ObservableList[Node]):Unit={

    if(fromState.name!=transition.DestinyStateName){
      transition.transitionComponents.line= new Line(){
        stroke=Color.LightGray
        strokeWidth=1.0
        startX.set(fromState.stateComponents.circle.centerX.value)
        startY.set(fromState.stateComponents.circle.centerY.value)
        endX.set(toState.stateComponents.circle.centerX.value)
        endY.set(toState.stateComponents.circle.centerY.value)
      }
      transition.transitionComponents.labelText=new Label(transition.transitionName){
        textFill=Color.Black
        layoutX=( transition.transitionComponents.line.startX.value+ transition.transitionComponents.line.endX.value)/2
        layoutY=( transition.transitionComponents.line.startY.value+ transition.transitionComponents.line.endY.value)/2
      }

      transition.transitionComponents.circlePoint = new Circle(){
        centerX = transition.transitionComponents.line.endX.value
        centerY = transition.transitionComponents.line.endY.value
        radius=3
        fill=Color.Green
      }
      content.add(transition.transitionComponents.line)
      content.add(transition.transitionComponents.labelText)
      content.add(transition.transitionComponents.circlePoint)

    }else{

      transition.transitionComponents.buckleCircle= new Circle(){
        centerX=fromState.stateComponents.circle.centerX.value
        centerY=fromState.stateComponents.circle.centerY.value
        radius = 25
        stroke=Color.Black
        strokeWidth=2
        fill = Color.LightGray
      }
      transition.transitionComponents.buckleCircle.setOpacity(0.1)
      transition.transitionComponents.labelText=new Label(transition.transitionName){
        textFill=Color.Black
        layoutX=fromState.stateComponents.circle.centerX.value-4
        layoutY=fromState.stateComponents.circle.centerY.value-40
      }
      transition.isbuckle=true
      content.add(transition.transitionComponents.buckleCircle)
      content.add(transition.transitionComponents.labelText)
    }

  }

  def removeState(name: String, content: ObservableList[Node],States:ArrayBuffer[State]): Unit = {
    if (name.isEmpty)
      return
    content.remove(22, content.size())
    for (elem <-States) {
      if (elem.name != name && !elem.isDeleted) {
        content.add(elem.stateComponents.circle)
        content.add(elem.stateComponents.labelText)
      } else {
        elem.isDeleted = true
        for (trans <- elem.transitionsList) {
          trans.isDeleted = true
        }
      }
    }
    for (elem <- States) {
      for (trans <- elem.transitionsList) {
        if (trans.DestinyStateName == name)
          trans.isDeleted = true
      }
    }

    for (elem <- States) {
      for (trans <- elem.transitionsList) {
        if (!trans.isDeleted) {
          if(trans.transitionComponents.line!=null)
            content.add(trans.transitionComponents.line)
          if(trans.transitionComponents.labelText!=null)
            content.add(trans.transitionComponents.labelText)
          if(trans.transitionComponents.circlePoint!=null)
            content.add(trans.transitionComponents.circlePoint)
          if(trans.transitionComponents.buckleCircle!=null)
            content.add(trans.transitionComponents.buckleCircle)
        }
      }
    }
  }

  def removeTransition(from_To: String, content: ObservableList[Node],States:ArrayBuffer[State]): Unit = {
    val edge = from_To.split("~")
    if (edge.length < 3)
      return
    val from = edge(0)
    val name = edge(2)
    content.remove(22, content.size())

    for (elem <- States) {
      if (elem.name != name && !elem.isDeleted) {
        content.add(elem.stateComponents.circle)
        content.add(elem.stateComponents.labelText)
      } else
        elem.isDeleted = true
    }
    for (elem <- States) {
      for (trans <- elem.transitionsList) {
        if(elem.name==from && trans.transitionName==name)
          trans.isDeleted = true
        if(!trans.isDeleted){
          if(trans.transitionComponents.line!=null)
            content.add(trans.transitionComponents.line)
          if(trans.transitionComponents.labelText!=null)
            content.add(trans.transitionComponents.labelText)
          if(trans.transitionComponents.circlePoint!=null)
            content.add(trans.transitionComponents.circlePoint)
          if(trans.transitionComponents.buckleCircle!=null)
            content.add(trans.transitionComponents.buckleCircle)
        }
      }
    }
  }

  def editInitialAndFinal(list: String,States:ArrayBuffer[State]): Unit = {
    val intialorfinalList = list.split("~")
    if (intialorfinalList.length < 2)
      return
    if (intialorfinalList(0) == "I" && intialorfinalList.size == 2) {
      for (elem <-States) {
        if (elem.name == intialorfinalList(1)) {
          elem.isInicial = true
          elem.stateComponents.circle.fill = Color.Red
        } else {
          elem.isInicial = false
          elem.stateComponents.circle.fill = Color.LightGray
        }
      }
    }

    if (intialorfinalList(0) == "F") {
      for (i <- 1 until intialorfinalList.size) {
        for (elem <-States) {
          if (elem.name == intialorfinalList(i)) {
            elem.isFinal = !elem.isFinal
          }
          if (elem.isFinal)
            elem.stateComponents.circle.stroke = Color.Blue
          else
            elem.stateComponents.circle.stroke = Color.LightGray
        }
      }
    }
  }

  def drawInitalAndFinal(States:ArrayBuffer[State]): Unit ={
    for(elem<-States){
      if(elem.isInicial)
        elem.stateComponents.circle.fill = Color.Red
      if(elem.isFinal)
        elem.stateComponents.circle.stroke = Color.Blue
    }
  }

  def clearALL(content: ObservableList[Node],States:ArrayBuffer[State]):Unit={
    if(States.isEmpty)
      return
    content.remove(22, content.size())
    for(elem<-States){
      elem.stateComponents=null
      for(trans<-elem.transitionsList){
        trans.transitionComponents=null
      }
      elem.transitionsList.clear()
    }
    States.clear()
  }
}







