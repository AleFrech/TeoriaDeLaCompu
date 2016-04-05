/**
  * Created by AlejandroFrech on 1/26/2016.
  */

import javafx.collections.ObservableList
import javafx.scene.Node
import scala.collection.mutable.ArrayBuffer
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.stage.{Stage, FileChooser}

object Main extends JFXApp {

    var automataManager = new DFAManager()
    var drawManager = new DrawManager()
    var currentMode="DFA"
    var tmpregex=""

    stage = new JFXApp.PrimaryStage {
      width = 1100
      height = 700
      title="DFA Mode"
      scene = new Scene {
        fill = Color.WhiteSmoke
        var ShowTransitionButton= new Button("Show Transition"){
          layoutX = 77
          layoutY = 20
          handleEvent(MouseEvent.MouseClicked) {
            a: MouseEvent => {
              val dialog = new TextInputDialog(defaultValue = "") {
                initOwner(stage)
                title = "Show Transiton Values"
                contentText = "Please enter state names separate by ~ :\n For example: q0~q1"
              }
              val result = dialog.showAndWait()
              result match {
                case Some(list) =>
                  val edge = list.split("~")
                  val from = automataManager.getState(edge(0))
                  val to = automataManager.getState(edge(1))
                  if((from!=null) && (to!=null)) {
                    val tranitionsValues = automataManager.getTransitionsValues(from, to)
                    val stage1 = new Stage() {
                      scene = new Scene {
                        var txtbox = new Label()
                        txtbox.layoutX = 15
                        txtbox.layoutY = 20
                        var str = ""
                        for (i <- tranitionsValues.indices) {
                          str += (i + 1) + ")  " + tranitionsValues(i) + "\n\n"
                        }
                        txtbox.text.value = str
                        content.add(txtbox)
                      }
                    }
                    stage1.setWidth(320)
                    stage1.setTitle("From "+from.name+" To "+to.name+" Transitions:")
                    stage1.setHeight(250)
                    stage1.show()
                  }
                case None =>println("Cancel")
              }
            }
          }
        }
        ShowTransitionButton.setStyle("-fx-font: 10 arial;")
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
                case Some(list) => val (fromstate, transition) = automataManager.addTransition(list)
                  if (fromstate != null && transition != null)
                    drawManager.drawTransition(fromstate, automataManager.getState(transition.DestinyStateName), transition, content)
                case None =>println("Cancel")
              }
            }
          }
        }
        addTransitionButton.setStyle("-fx-font: 10 arial;")
        var evaluateButton = new Button("Evaluate ") {
          layoutX = 78
          layoutY = 0
          handleEvent(MouseEvent.MouseClicked) {
            a: MouseEvent => {
              val dialog = new TextInputDialog(defaultValue = "") {
                initOwner(stage)
                title = "Evaluate"
                contentText = "Please enter chain expresion: "
              }
              val result = dialog.showAndWait()
              result match {
                case Some(expresion) => showResult(expresion, stage)
                case None =>println("Cancel")
              }
            }
          }
        }
        evaluateButton.setStyle("-fx-font: 10 arial;")
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
                case Some(name) => drawManager.removeState(name, content, automataManager.States)
                case None=>println("Cancel")
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
                case Some(list) => drawManager.editInitialAndFinal(list, automataManager.States)
                case None=>println("Cancel")
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
                case Some(list) => drawManager.removeTransition(list, content, automataManager.States)
                case None =>println("Cancel")
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
              val fileChoser = new FileChooser()
              val file = fileChoser.showSaveDialog(stage)
              if (file != null) {
                FileManager.saveToFile(automataManager.States,file.getAbsolutePath,currentMode)
              }
            }
          }
        }
        saveButton.setStyle("-fx-font: 10 arial;")
        var loadButton = new Button("Load File") {
          layoutX = 433
          layoutY = 0
          handleEvent(MouseEvent.MouseClicked) {
            a: MouseEvent => {
              val fileChoser = new FileChooser()
              val file = fileChoser.showOpenDialog(stage)
              if (file != null) {
                if (file.exists()) {
                  val (statesArray,mode) = FileManager.loadFromFile(file)
                  if(mode=="DFA"){
                    automataManager= new DFAManager()
                    title="DFA Mode"
                    currentMode="DFA"
                  }else if(mode =="NFA"){
                    automataManager= new NFAManager()
                    title="NFA Mode"
                    currentMode="NFA"
                  }else if(mode =="NFA-E"){
                    automataManager= new NFAManager()
                    title="NFA-E Mode"
                    automataManager.acceptsEpsilon=true
                    currentMode="NFA-E"
                  }
                  automataManager.States = statesArray
                  reDrawStates(content)
                }else {
                  throwError("File Not Found !!!!")
                }
              }
            }
          }
        }
        loadButton.setStyle("-fx-font: 10 arial;")
        var minimizeButton = new Button("Minimize DFA") {
          layoutX = 487
          layoutY = 0
          handleEvent(MouseEvent.MouseClicked) {
            a: MouseEvent => {
                if(currentMode=="DFA"){
                    automataManager=automataManager.minimize()
                    reDrawStates(content)
                }
            }
          }
        }
        minimizeButton.setStyle("-fx-font: 10 arial;")
        var UnionButton = new Button("Union") {
          layoutX = 563
          layoutY = 0
          handleEvent(MouseEvent.MouseClicked) {
            a: MouseEvent => {
              val fileChoser = new FileChooser()
              val file1 = fileChoser.showOpenDialog(stage)
              val file2 = fileChoser.showOpenDialog(stage)
              if (file1 !=null && file2!= null) {
                if(file1.exists() && file2.exists()){
                  val (statesArray1,mode1) = FileManager.loadFromFile(file1)
                  val (statesArray2,mode2) = FileManager.loadFromFile(file2)
                  if(mode1=="DFA" && mode2=="DFA"){
                    automataManager=automataManager.union(statesArray1,statesArray2)
                    reDrawStates(content)
                  }else{
                    throwError("Files must be a DFA")
                  }
                }else{
                  throwError("File Not Found!!!!!!")
                }
              }
            }
          }
        }
        UnionButton.setStyle("-fx-font: 10 arial;")
        var IntersectionButton = new Button("Intersection") {
          layoutX = 603
          layoutY = 0
          handleEvent(MouseEvent.MouseClicked) {
            a: MouseEvent => {
              val fileChoser = new FileChooser()
              val file1 = fileChoser.showOpenDialog(stage)
              val file2 = fileChoser.showOpenDialog(stage)
              if (file1 !=null && file2!= null) {
                if(file1.exists() && file2.exists()){
                  val (statesArray1,mode1) = FileManager.loadFromFile(file1)
                  val (statesArray2,mode2) = FileManager.loadFromFile(file2)
                  if(mode1=="DFA" && mode2=="DFA"){
                    automataManager=automataManager.intersection(statesArray1,statesArray2)
                    reDrawStates(content)
                  }else{
                    throwError("Files must be a DFA")
                  }
                }else{
                  throwError("File Not Found!!!!!!")
                }
              }
            }
          }
        }
        IntersectionButton.setStyle("-fx-font: 10 arial;")
        var DiffrenceButton = new Button("Diffrence") {
          layoutX = 669
          layoutY = 0
          handleEvent(MouseEvent.MouseClicked) {
            a: MouseEvent => {
              val fileChoser = new FileChooser()
              val file1 = fileChoser.showOpenDialog(stage)
              val file2 = fileChoser.showOpenDialog(stage)
              if (file1 !=null && file2!= null) {
                if(file1.exists() && file2.exists()){
                  val (statesArray1,mode1) = FileManager.loadFromFile(file1)
                  val (statesArray2,mode2) = FileManager.loadFromFile(file2)
                  if(mode1=="DFA" && mode2=="DFA"){
                    automataManager=automataManager.diffrence(statesArray1,statesArray2)
                    reDrawStates(content)
                  }else{
                    throwError("Files must be a DFA")
                  }
                }else{
                  throwError("File Not Found!!!!!!")
                }
              }
            }
          }
        }
        DiffrenceButton.setStyle("-fx-font: 10 arial;")
        var ComplementButton = new Button("Complement") {
          layoutX = 722
          layoutY = 0
          handleEvent(MouseEvent.MouseClicked) {
            a: MouseEvent => {
              val fileChoser = new FileChooser()
              val file1 = fileChoser.showOpenDialog(stage)
              if (file1 !=null ) {
                if(file1.exists()){
                  val (statesArray1,mode1) = FileManager.loadFromFile(file1)
                    if(mode1=="DFA"){
                    automataManager=automataManager.complement(statesArray1)
                    reDrawStates(content)
                  }else{
                    throwError("Files must be a DFA")
                  }
                }else{
                  throwError("File Not Found!!!!!!")
                }
              }
            }
          }
        }
        ComplementButton.setStyle("-fx-font: 10 arial;")
        var convertButton = new Button("Convert") {
          layoutX = 793
          layoutY = 0
          handleEvent(MouseEvent.MouseClicked) {
            a: MouseEvent => {
              val (i,f)=hasInitialAndFinal(automataManager.States)
              if(i && f) {
                if(currentMode=="NFA") {
                  automataManager = automataManager.convertToDFA()
                  reDrawStates(content)
                  currentMode = "DFA"
                  title = "DFA Mode"
                }else if(currentMode=="NFA-E"){
                  automataManager = automataManager.convertToNFA()
                  automataManager=automataManager.convertToDFA()
                  reDrawStates(content)
                  currentMode="DFA"
                  title="DFA Mode"
                }
              }else{
                var x = new Alert(AlertType.Error) {
                  initOwner(stage)
                  title = "Error!!!"
                  headerText = "Error"
                  contentText = "Please Set Inicial State  and FInal States"
                }.showAndWait()
              }
            }
          }
        }
        convertButton.setStyle("-fx-font: 10 arial;")
        var changeModeButton = new Button("Change Mode") {
          layoutX = 0
          layoutY = 21
          handleEvent(MouseEvent.MouseClicked) {
            a: MouseEvent => {
              if(currentMode=="DFA"){
                val tmp=automataManager
                automataManager= new NFAManager()
                automataManager.States=tmp.States
                title="NFA Mode"
                currentMode="NFA"
              }else if(currentMode=="NFA"){
                val tmp=automataManager
                automataManager= new NFAManager()
                automataManager.States=tmp.States
                automataManager.acceptsEpsilon=true
                title="NFA-E Mode"
                currentMode="NFA-E"
              }else if(currentMode=="NFA-E"){
                val tmp=automataManager
                automataManager= new PDAManager()
                automataManager.States=tmp.States
                title="PDA Mode"
                currentMode="PDA"
              }else if(currentMode=="PDA"){
                val tmp=automataManager
                automataManager= new TurinManager()
                automataManager.States=tmp.States
                title="Turin Mode"
                currentMode="Turin"
              }else if(currentMode=="Turin"){
                val tmp=automataManager
                automataManager= new DFAManager()
                automataManager.States=tmp.States
                title="DFA Mode"
                currentMode="DFA"
              }

            }
          }
        }
        changeModeButton.setStyle("-fx-font: 10 arial;")
        var CFLToPDAButton = new Button("CFL to PDA") {
          layoutX = 163
          layoutY = 20
          handleEvent(MouseEvent.MouseClicked) {
            a: MouseEvent => {
              val dialog = new TextInputDialog(defaultValue = "") {
                initOwner(stage)
                title = "Enter CFL"
                contentText = "Please enter CFL:\n For example: S->{0S0,1S1,0,1}"
              }
              val result = dialog.showAndWait()
              result match {
                case Some(list) =>
                  if(currentMode=="PDA"){
                    val cfl = CFLStringToMap(list)
                    automataManager=automataManager.asInstanceOf[PDAManager].CFLToPDA(cfl)
                    reDrawStates(content)
                  }
                case None =>println("Cancel")
              }
            }
          }
        }
        CFLToPDAButton.setStyle("-fx-font: 10 arial;")
        var RegLabel = new Label("Regex"){
          layoutX = 250
          layoutY = 20
        }
        var RegTxtBox= new TextField(){
          layoutX = 290
          layoutY = 20
        }
        var ExpLabel = new Label("Expression"){
          layoutX = 450
          layoutY = 20
        }
        var ExpTxtBox= new TextField(){
          layoutX = 510
          layoutY = 20
        }
        var EvaluateRegButton = new Button("Evaluate Regex") {
          layoutX = 670
          layoutY = 20
          handleEvent(MouseEvent.MouseClicked) {
            a: MouseEvent => {
              var x=new Alert(AlertType.Information) {
                initOwner(stage)
                title = "Result Dialog"
                headerText = "Result"
                var parser = new RegexParser
                parser.parse(RegTxtBox.text.value)
                if(tmpregex!=RegTxtBox.text.value) {
                  tmpregex=RegTxtBox.text.value
                  automataManager = parser.getNFA_E()
                  automataManager.acceptsEpsilon = true
                  reDrawStates(content)
                  currentMode="NFA-E"
                  stage.title="NFA-E Mode"
                }
                var value =automataManager.evaluate(ExpTxtBox.text.value)
                if (value) {
                    contentText= "Expresion accepted"
                } else {
                  alertType.value = AlertType.Error
                  contentText = "Expresion not accepted"
                }
              }.showAndWait()

              ExpTxtBox.clear()
            }
          }
        }
        EvaluateRegButton.setStyle("-fx-font: 10 arial;")
        var clearButton = new Button("Clear") {
          layoutX = 842
          layoutY = 0
          handleEvent(MouseEvent.MouseClicked) {
            a: MouseEvent => {
              drawManager.clearALL(content, automataManager.States)
            }
          }
        }
        clearButton.setStyle("-fx-font: 10 arial;")
        content.add(addTransitionButton)
        content.add(evaluateButton)
        content.add(editStateButton)
        content.add(deleteButton)
        content.add(removeTransitionButton)
        content.add(saveButton)
        content.add(loadButton)
        content.add(convertButton)
        content.add(clearButton)
        content.add(changeModeButton)
        content.add(minimizeButton)
        content.add(UnionButton)
        content.add(IntersectionButton)
        content.add(DiffrenceButton)
        content.add(ComplementButton)
        content.add(ShowTransitionButton)
        content.add(CFLToPDAButton)
        content.add(RegLabel)
        content.add(ExpLabel)
        content.add(RegTxtBox)
        content.add(ExpTxtBox)
        content.add(EvaluateRegButton)

        handleEvent(MouseEvent.MouseClicked) {
          a: MouseEvent => {
            if (a.sceneY > 100) {
              val dialog = new TextInputDialog(defaultValue = "") {
                initOwner(stage)
                title = "State Name"
                contentText = "Please enter State name:"
              }
              val result = dialog.showAndWait()
              result match {
                case Some(name) =>
                    val state = automataManager.addState(name, a.sceneX, a.sceneY)
                    if (state != null)
                      drawManager.drawState(state, content, automataManager.States, a.sceneX, a.sceneY)
                case None =>println("Cancel")
              }
            }
          }
        }
      }

    }
    def showResult(expresion: String, stage: JFXApp.PrimaryStage): Unit = {
      var value:Boolean=false
      var tape=""
      if(currentMode=="Turin"){
        val (v,t) = automataManager.asInstanceOf[TurinManager].evaluateTurin(expresion)
        value=v
        tape=t
      }else{
        value = automataManager.evaluate(expresion)
      }
      new Alert(AlertType.Information) {
        initOwner(stage)
        title = "Result Dialog"
        headerText = "Result"
        if (value) {
          if(automataManager.isInstanceOf[TurinManager]) {
            contentText = "Expresion accepted\n Tape :" + tape
          }else{
            contentText= "Expresion accepted"
          }
          println(tape)
        } else {
          alertType.value = AlertType.Error
          contentText = "Expresion not accepted"
          println(tape)
        }
      }.showAndWait()
    }
  def reDrawStates(content: ObservableList[Node]): Unit ={
    content.remove(22, content.size())
    for (elem <- automataManager.States) {
        drawManager.drawState(elem, content, automataManager.States, elem.posX, elem.posY)
    }
    for (elem <- automataManager.States) {
      for (trans <- elem.transitionsList) {
          drawManager.drawTransition(elem, automataManager.getState(trans.DestinyStateName), trans, content)
      }
    }
    drawManager.drawInitalAndFinal(automataManager.States)
  }

  def hasInitialAndFinal(States:ArrayBuffer[State]): (Boolean,Boolean)={
    var hasInital=false
    var hasFinal=false
    for(elem<-States){
      if(elem.isInicial){
        hasInital=true
      }
      if(elem.isFinal){
        hasFinal=true
      }
    }
    (hasInital,hasFinal)
  }

  def CFLStringToMap(list:String):Map[String,ArrayBuffer[String]]={
    var cfl:Map[String,ArrayBuffer[String]]=Map()
    val variables = list.split(" ")
    for(v<-variables){
      val varsAndProd=v.split("->")
      val Prods=varsAndProd(1).drop(1).dropRight(1).split(",")
      var productions=ArrayBuffer.empty[String]
      for(p<-Prods){
        productions+=p
      }
      cfl+=(varsAndProd(0)->productions)
    }
    cfl
  }

  def throwError(message:String): Unit ={
    new Alert(AlertType.Error) {
      initOwner(stage)
      title = "Error"
      headerText = "Error!!!"
      contentText = message
    }.showAndWait()
  }
}
