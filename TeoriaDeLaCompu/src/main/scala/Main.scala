/**
  * Created by AlejandroFrech on 1/26/2016.
  */
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.{Button, TextInputDialog}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color

object Main extends JFXApp {
  var sceneManager= new SceneManager()
  stage = new JFXApp.PrimaryStage {
    width = 900
    height = 600
    scene = new Scene {
      fill = Color.WhiteSmoke

      var addTransitionButton = new Button("Add Transition"){
          layoutX=0
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
                case Some(list) =>sceneManager.addTransition(list,content)
                case None => println("Cancel")
              }
            }
          }
      }

      var evaluateAutomataButton= new Button("Evaluate DFA"){
        layoutX=94
        layoutY=0
        handleEvent(MouseEvent.MouseClicked){
          a: MouseEvent => {
            val dialog = new TextInputDialog(defaultValue = "") {
              initOwner(stage)
              title = "Evaluate DFA"
              contentText = "Please enter chain expresion: "
            }
            val result = dialog.showAndWait()
            result match {
              case Some(expresion) =>sceneManager.evaluateDFA(expresion,stage)
              case None => println("Cancel")
            }
          }
        }
      }

      var deleteButton = new Button("Delete State"){
        layoutX=180
        layoutY=0
        handleEvent(MouseEvent.MouseClicked){
          a: MouseEvent => {
            val dialog = new TextInputDialog(defaultValue = "") {
              initOwner(stage)
              title = "State Name"
              contentText = "Please enter State name:"
            }
            val result = dialog.showAndWait()
            result match {
              case Some(name) =>sceneManager.removeState(name,content)
              case None => println("Cancel")
            }
          }
        }
      }

      var removeTransitionButton = new Button("Delete Transition"){
        layoutX=260
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
              case Some(list) =>sceneManager.removeTransition(list,content)
              case None => println("Cancel")
            }
          }
        }
      }
      content.add(addTransitionButton)
      content.add(evaluateAutomataButton)
      content.add(deleteButton)
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
              case Some(name) => var result=sceneManager.addState(name,content,a.sceneX,a.sceneY)
              case None => println("Cancel")
            }
        }
      }
    }
  }
}
