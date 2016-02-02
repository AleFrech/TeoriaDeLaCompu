/**
  * Created by AlejandroFrech on 1/26/2016.
  */
import scalafx.Includes._
import DFATYPES._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.{Button, TextInputDialog}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color

object Main extends JFXApp {

  def run(typeDFA:DFATYPES): Unit = {
    var automataManager:DFAManager=null
    if(typeDFA==DFATYPES.DFA) {
       automataManager = new DFAManager()
    }else if(typeDFA==DFATYPES.NFA){
      automataManager = new NFAManager()
    }
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
                case Some(list) => automataManager.DrawTransition(list, content)
                case None => println("Cancel")
              }
            }
          }
        }
        addTransitionButton.setStyle("-fx-font: 10 arial;")

        var evaluateAutomataButton = new Button("Evaluate ") {
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
                case Some(expresion) => automataManager.showResult(expresion, stage)
                case None => println("Cancel")
              }
            }
          }
        }
        evaluateAutomataButton.setStyle("-fx-font: 10 arial;")
        var deleteButton = new Button("Delete State") {
          layoutX = 134
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
                case Some(name) => automataManager.removeState(name, content)
                case None => println("Cancel")
              }
            }
          }
        }
        deleteButton.setStyle("-fx-font: 10 arial;")
        var editStateButton = new Button("Edit Initial&Final") {
          layoutX = 203
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
                case Some(list) => automataManager.editInitialAndFinal(list)
                case None => println("Cancel")
              }
            }
          }
        }
        editStateButton.setStyle("-fx-font: 10 arial;")
        var removeTransitionButton = new Button("Delete Transition") {
          layoutX = 289
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
                case Some(list) => automataManager.removeTransition(list, content)
                case None => println("Cancel")
              }
            }
          }
        }
        removeTransitionButton.setStyle("-fx-font: 10 arial;")
        var saveButton = new Button("Save File") {
          layoutX = 378
          layoutY = 0
          handleEvent(MouseEvent.MouseClicked) {
            a: MouseEvent => {
              val dialog = new TextInputDialog(defaultValue = "") {
                initOwner(stage)
                title = "Save To File "
                contentText = "Please enter names of File "
              }
              val result = dialog.showAndWait()
              result match {
                case Some(name) => FileManager.saveDFAToFile(automataManager,name)
                case None => println("Cancel")
              }
            }
          }
        }
        saveButton.setStyle("-fx-font: 10 arial;")
        content.add(addTransitionButton)
        content.add(evaluateAutomataButton)
        content.add(editStateButton)
        content.add(deleteButton)
        content.add(removeTransitionButton)
        content.add(saveButton)
        handleEvent(MouseEvent.MouseClicked) {
          a: MouseEvent => {
            if (a.sceneY > 60) {
              val dialog = new TextInputDialog(defaultValue = "") {
                initOwner(stage)
                title = "State Name"
                contentText = "Please enter State name:"
              }
              val result = dialog.showAndWait()
              result match {
                case Some(name) => automataManager.DrawState(name, content, a.sceneX, a.sceneY)
                case None => println("Cancel")
              }
            }
          }
        }
      }
    }
  }
}