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

      var addTransitionButton = new Button("Add Transition") {
        layoutX = 0
        layoutY = 0

        handleEvent(MouseEvent.MouseClicked) {
          a: MouseEvent => {
            val dialog = new TextInputDialog(defaultValue = "") {
              initOwner(stage)
              title = "New Transition"
              contentText = "Please enter state names separate by ~ :\n For example: q0~q1~value"
            }
            val result = dialog.showAndWait()
            result match {
              case Some(list) => sceneManager.addDFATransition(list, content)
              case None => println("Cancel")
            }
          }
        }
      }
      addTransitionButton.setStyle("-fx-font: 10 arial;")

      var evaluateAutomataButton = new Button("Evaluate DFA") {
        layoutX = 78
        layoutY = 0
        handleEvent(MouseEvent.MouseClicked) {
          a: MouseEvent => {
            val dialog = new TextInputDialog(defaultValue = "") {
              initOwner(stage)
              title = "Evaluate DFA"
              contentText = "Please enter chain expresion: "
            }
            val result = dialog.showAndWait()
            result match {
              case Some(expresion) => sceneManager.evaluateDFA(expresion, stage)
              case None => println("Cancel")
            }
          }
        }
      }
      evaluateAutomataButton.setStyle("-fx-font: 10 arial;")
      var deleteButton = new Button("Delete State") {
        layoutX = 154
        layoutY = 0
        handleEvent(MouseEvent.MouseClicked) {
          a: MouseEvent => {
            val dialog = new TextInputDialog(defaultValue = "") {
              initOwner(stage)
              title = "State Name"
              contentText = "Please enter State name:"
            }
            val result = dialog.showAndWait()
            result match {
              case Some(name) => sceneManager.removeState(name, content)
              case None => println("Cancel")
            }
          }
        }
      }
      deleteButton.setStyle("-fx-font: 10 arial;")
      var editStateButton = new Button("Edit Initial&Final") {
        layoutX = 223
        layoutY = 0
        handleEvent(MouseEvent.MouseClicked) {
          a: MouseEvent => {
            val dialog = new TextInputDialog(defaultValue = "") {
              initOwner(stage)
              title = "Edit Initial&Final"
              contentText = "For example: I~q0 or F~q1~q2...."
            }
            val result = dialog.showAndWait()
            result match {
              case Some(list) => sceneManager.editInitialAndFinal(list)
              case None => println("Cancel")
            }
          }
        }
      }
      editStateButton.setStyle("-fx-font: 10 arial;")
      var removeTransitionButton = new Button("Delete Transition") {
        layoutX = 309
        layoutY = 0
        handleEvent(MouseEvent.MouseClicked) {
          a: MouseEvent => {
            val dialog = new TextInputDialog(defaultValue = "") {
              initOwner(stage)
              title = "Delete Transition"
              contentText = "Please enter state names separate by ~ :\n For example: q0~q1~value"
            }
            val result = dialog.showAndWait()
            result match {
              case Some(list) => sceneManager.removeTransition(list, content)
              case None => println("Cancel")
            }
          }
        }
      }
      removeTransitionButton.setStyle("-fx-font: 10 arial;")
      content.add(addTransitionButton)
      content.add(evaluateAutomataButton)
      content.add(editStateButton)
      content.add(deleteButton)
      content.add(removeTransitionButton)

      handleEvent(MouseEvent.MouseClicked){
        a: MouseEvent => {
          if(a.sceneY>60) {
            val dialog = new TextInputDialog(defaultValue = "") {
              initOwner(stage)
              title = "State Name"
              contentText = "Please enter State name:"
            }
            val result = dialog.showAndWait()
            result match {
              case Some(name) => var result = sceneManager.addState(name, content, a.sceneX, a.sceneY)
              case None => println("Cancel")
            }
          }
        }
      }
    }
  }
}
