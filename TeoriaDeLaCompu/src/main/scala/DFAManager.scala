import javafx.collections.ObservableList
import javafx.scene.Node
import scala.collection.mutable.ArrayBuffer
import scalafx.application.JFXApp
import scalafx.geometry.Pos
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Label}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Line, Circle}
import scalafx.scene.text.Text

/**
  * Created by AlejandroFrech on 1/28/2016.
  */
@SerialVersionUID(100L)
class DFAManager()extends Serializable{
  var States =  ArrayBuffer.empty[State]

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
        if(state.isFinal)
          return true
        else
          return false
      }
    }
    return false

  }


  def DrawState(name: String, content: ObservableList[Node], posX: Double, posY: Double):Unit={
    if (name.isEmpty)
      return
    var hasColisioned = false
    for (elem <- States) {
      if (elem.name == name && !elem.isDeleted)
        return
      var collisonFormula: Double = Math.pow(elem.stateComponents.circle.centerX.value - posX, 2) + Math.pow(elem.stateComponents.circle.centerY.value - posY, 2)
      if (0 <= collisonFormula && collisonFormula <= Math.pow(40, 2)) {
        if (!elem.isDeleted)
          hasColisioned = true
      }
    }
    if (!hasColisioned) {
      var state = new State(name)
      state.stateComponents.circle = new Circle(){
        centerX = posX
        centerY = posY
        radius = 20
        stroke=Color.LightGray
        fill = Color.LightGray
      }
      state.stateComponents.labelText = new Text(posX-6,posY-5,name){
        fill= Color.Black
        alignmentInParent=Pos.TopCenter
      }
      content.add(state.stateComponents.circle)
      content.add(state.stateComponents.labelText)
      States += state
    }
  }

  def DrawTransition(from_To: String, content: ObservableList[Node]):Unit={

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
    for (trans <- fromState.transitionsList) {
      if (trans.transitionName == edge(2) && !trans.isDeleted)
        return
    }

    var transition = new Transition(edge(2), edge(1))

    if(edge(0)!=edge(1)){
      transition.transitionComponents.line= new Line(){
        stroke=Color.LightGray
        strokeWidth=1.0
        startX.set(fromState.stateComponents.circle.centerX.value)
        startY.set(fromState.stateComponents.circle.centerY.value)
        endX.set(toState.stateComponents.circle.centerX.value)
        endY.set(toState.stateComponents.circle.centerY.value)
      }
      transition.transitionComponents.labelText=new Label(edge(2)){
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
      transition.transitionComponents.labelText=new Label(edge(2)){
        textFill=Color.Black
        layoutX=fromState.stateComponents.circle.centerX.value-4
        layoutY=fromState.stateComponents.circle.centerY.value-40
      }

      content.add(transition.transitionComponents.buckleCircle)
      content.add(transition.transitionComponents.labelText)

    }
      fromState.transitionsList += transition
  }

  def removeState(name: String, content: ObservableList[Node]): Unit = {
    if (name.isEmpty)
      return
    content.remove(6, content.size())
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

  def removeTransition(from_To: String, content: ObservableList[Node]): Unit = {
    var edge = from_To.split("~")
    if (edge.length < 3)
      return
    var from = edge(0)
    var to = edge(1)
    var name = edge(2)
    content.remove(6, content.size())

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

}