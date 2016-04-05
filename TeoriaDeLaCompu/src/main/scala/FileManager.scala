import java.io.{File, BufferedWriter, FileWriter}

import scala.collection.mutable.ArrayBuffer
import scalafx.geometry.Pos
import scalafx.scene.control.Label
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Line, Circle}
import scalafx.scene.text.Text

/**
  * Created by AlejandroFrech on 2/1/2016.
  */
object FileManager {

  def saveToFile(States:ArrayBuffer[State], name: String,mode:String): Unit = {
    val bufferWriter = new BufferedWriter(new FileWriter(name))
    bufferWriter.write(mode+":")
    for (elem <- States) {
      if (!elem.isDeleted) {
        bufferWriter.write(elem.name + "," + elem.isDeleted + "," + elem.isInicial + "," + elem.isFinal + ",")
        bufferWriter.write(elem.stateComponents.circle.centerX.value + "," + elem.stateComponents.circle.centerY.value + ",")
        bufferWriter.write(elem.stateComponents.labelText.layoutX.value + "," + elem.stateComponents.labelText.layoutY.value + "," + elem.stateComponents.labelText.text.value)
        bufferWriter.write("Transitionlist")
        for (trans <- elem.transitionsList) {
          if (!trans.isDeleted) {
            bufferWriter.write(trans.transitionName + "," + trans.DestinyStateName + "," + trans.isDeleted + "," + trans.isbuckle + ",")
            if (!trans.isbuckle) {
              bufferWriter.write(trans.transitionComponents.line.startX.value + "," + trans.transitionComponents.line.startY.value + "," + trans.transitionComponents.line.endX.value + "," + trans.transitionComponents.line.startY.value + ",")
              bufferWriter.write(trans.transitionComponents.labelText.layoutX.value + "," + trans.transitionComponents.labelText.layoutY.value + "," + trans.transitionComponents.labelText.text.value + ",")
              bufferWriter.write(trans.transitionComponents.circlePoint.centerX.value + "," + trans.transitionComponents.circlePoint.centerY.value + ",")
            } else {
              bufferWriter.write(trans.transitionComponents.buckleCircle.centerX.value + "," + trans.transitionComponents.buckleCircle.centerY.value + ",")
              bufferWriter.write(trans.transitionComponents.labelText.layoutX.value + "," + trans.transitionComponents.labelText.layoutY.value + "," + trans.transitionComponents.labelText.text.value + ",")
            }
            bufferWriter.write("TransitionsEnd")
          }
        }
        bufferWriter.write("EndState")
      }
    }
    bufferWriter.flush()
    bufferWriter.close()
  }

  def iterToString(iter:Iterator[String]):String={
    var str=""
    for(x<-iter){
      str+=x
    }
    str
  }
  def loadFromFile(file:File):(ArrayBuffer[State],String)={
    var arrayStates=ArrayBuffer.empty[State]
    val buffer = iterToString(scala.io.Source.fromFile(file).getLines())
    val entireFile= buffer.split(":")
    val mode = entireFile(0)
    val statesInfo = entireFile(1).split("EndState")
    for(states<-statesInfo){
      var tmpState:State=null
      val statesAndTransitions = states.toString.split("Transitionlist")
        var i=0
        while(i < statesAndTransitions.size){
          val stateAndComponents = statesAndTransitions(i).split(",")
          tmpState = new State(stateAndComponents(0),stateAndComponents(4).toDouble,stateAndComponents(5).toDouble)
          tmpState.isDeleted = stateAndComponents(1).toBoolean
          tmpState.isInicial = stateAndComponents(2).toBoolean
          tmpState.isFinal = stateAndComponents(3).toBoolean
            tmpState.stateComponents.circle = new Circle() {
              centerX = stateAndComponents(4).toDouble
              centerY = stateAndComponents(5).toDouble
              radius = 20
              stroke = Color.LightGray
              fill = Color.LightGray
            }
            tmpState.stateComponents.labelText = new Text() {
              fill = Color.Black
              alignmentInParent = Pos.TopCenter
              layoutX = stateAndComponents(6).toDouble - 6
              layoutY = stateAndComponents(7).toDouble - 5
              text.value = stateAndComponents(5)
            }
          if(statesAndTransitions.size>(i+1)) {
            val transitionsAndComponents = statesAndTransitions(i + 1).split("TransitionsEnd")
            var tmpTrans: Transition = null
            for (x <- transitionsAndComponents) {
              val t = x.toString.split(",")
              tmpTrans = new Transition(t(0), t(1))
              tmpTrans.isDeleted = t(2).toBoolean
              tmpTrans.isbuckle = t(3).toBoolean
                if (!t(3).toBoolean) {
                  tmpTrans.transitionComponents.line = new Line() {
                    stroke = Color.LightGray
                    strokeWidth = 1.0
                    startX.set(t(4).toDouble)
                    startY.set(t(5).toDouble)
                    endX.set(t(6).toDouble)
                    endY.set(t(7).toDouble)
                  }
                  tmpTrans.transitionComponents.labelText = new Label(t(10)) {
                    textFill = Color.Black
                    layoutX = t(8).toDouble
                    layoutY = t(9).toDouble
                  }

                  tmpTrans.transitionComponents.circlePoint = new Circle() {
                    centerX = t(11).toDouble
                    centerY = t(12).toDouble
                    radius = 3
                    fill = Color.Green
                  }
                  tmpState.transitionsList += tmpTrans
                } else {
                  tmpTrans.transitionComponents.buckleCircle = new Circle() {
                    centerX = t(4).toDouble
                    centerY = t(5).toDouble
                    radius = 25
                    stroke = Color.Black
                    strokeWidth = 2
                    fill = Color.LightGray
                  }
                  tmpTrans.transitionComponents.buckleCircle.setOpacity(0.1)
                  tmpTrans.transitionComponents.labelText = new Label(t(8)) {
                    textFill = Color.Black
                    layoutX = t(6).toDouble
                    layoutY = t(7).toDouble
                  }
                  tmpTrans.isbuckle = true
                  tmpState.transitionsList += tmpTrans
                }
            }
          }
          arrayStates += tmpState
          i+=2
        }
      }
    (arrayStates,mode)
  }
}
