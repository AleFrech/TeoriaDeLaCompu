
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color

/**
  *Created by AlejandroFrech on 1/31/2016.
  */
object Startup extends JFXApp {
  stage = new PrimaryStage {
    width = 400
    height = 300
    scene = new Scene {
      fill = Color.WhiteSmoke
      var DFAButton = new Button("DFA") {
        layoutX = 150
        layoutY = 65
        onMouseClicked = (event: MouseEvent) => Main.run(DFATYPES.DFA)
      }
      DFAButton.setStyle("-fx-font: 20 arial;")
      var NFAButton = new Button("NFA") {
        layoutX = 150
        layoutY = 140
        onMouseClicked = (event: MouseEvent) => Main.run(DFATYPES.NFA)
      }
      NFAButton.setStyle("-fx-font: 20 arial;")
      content.add(DFAButton)
      content.add(NFAButton)
    }
  }
}
