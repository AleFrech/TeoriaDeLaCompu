import scalafx.geometry.Pos
import scalafx.scene.control.Label
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

}
