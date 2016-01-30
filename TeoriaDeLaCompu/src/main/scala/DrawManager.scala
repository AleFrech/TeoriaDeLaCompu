import javafx.collections.ObservableList
import javafx.scene.Node

import scalafx.application.JFXApp
import scalafx.geometry.Pos
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Label}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Line, Circle}
import scalafx.scene.text.Text

/**
  * Created by AlejandroFrech on 1/27/2016.
  */
class DrawManager() {
   

  def DrawState(posX: Double, posY: Double , name: String):StateComponents={
     var components= new StateComponents()
     components.circle = new Circle(){
      centerX = posX
      centerY = posY
      radius = 20
       stroke=Color.LightGray
      fill = Color.LightGray
     }
     components.labelText = new Text(posX-6,posY-5,name){
      fill= Color.Black
      alignmentInParent=Pos.TopCenter
     }
     return components
  }

  def DrawTransition(from:State,to:State,Tname:String):TransitionComponents={
    var components= new TransitionComponents()

    components.line= new Line(){
      stroke=Color.LightGray
      strokeWidth=1.0
      startX.set(from.stateComponents.circle.centerX.value)
      startY.set(from.stateComponents.circle.centerY.value)
      endX.set(to.stateComponents.circle.centerX.value)
      endY.set(to.stateComponents.circle.centerY.value)
    }
    components.labelText=new Label(Tname){
      textFill=Color.Black
      layoutX=(components.line.startX.value+components.line.endX.value)/2
      layoutY=(components.line.startY.value+components.line.endY.value)/2
    }
    components.circlePoint = new Circle(){
      centerX =components.line.endX.value
      centerY =components.line.endY.value
      radius=3
      fill=Color.Blue
    }

    return components
  }

  def removeState(name: String, content: ObservableList[Node],automataManager:DFAManager): Unit = {
    if (name.isEmpty)
      return
    content.remove(5, content.size())
    for (elem <- automataManager.States) {
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
    for (elem <- automataManager.States) {
      for (trans <- elem.transitionsList) {
        if (trans.DestinyStateName == name)
          trans.isDeleted = true
      }
    }

    for (elem <- automataManager.States) {
      for (trans <- elem.transitionsList) {
        if (!trans.isDeleted) {
          content.add(trans.transitionComponents.line)
          content.add(trans.transitionComponents.labelText)
          content.add(trans.transitionComponents.circlePoint)
        }
      }
    }
  }

  def removeTransition(from_To: String, content: ObservableList[Node],automataManager:DFAManager): Unit = {
    var edge = from_To.split("~")
    if (edge.length < 3)
      return
    var from = edge(0)
    var to = edge(1)
    var name = edge(2)
    content.remove(5, content.size())
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



  def editInitialAndFinal(list: String,automataManager:DFAManager): Unit = {
    val intialorfinalList = list.split("~")
    if (intialorfinalList.length < 2)
      return
    if (intialorfinalList(0) == "I" && intialorfinalList.size == 2) {
      for (elem <- automataManager.States) {
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
      var i = 0
      for (i <- 1 to intialorfinalList.size - 1) {
        for (elem <- automataManager.States) {
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

  def showResult(expresion: String, stage: JFXApp.PrimaryStage,automataManager:DFAManager): Unit = {
    val value = automataManager.evaluate(expresion)
    new Alert(AlertType.Information) {
      initOwner(stage)
      title = "Result Dialog"
      headerText = "Result"
      if (value) {
        contentText = "Expresion accepted"
      } else {
        contentText = "Expresion not accepted"
      }
    }.showAndWait()
  }
}
