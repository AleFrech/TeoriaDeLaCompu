import java.util.Random
import scala.collection.mutable.ArrayBuffer
/**
  * Created by AlejandroFrech on 1/28/2016.
  */
class DFAManager() {
  var acceptsEpsilon = false
  var States = ArrayBuffer.empty[State]

  def addState(name: String, posX: Double, posY: Double): State = {
    if (name.isEmpty)
      return null
    var state = new State(name, posX, posY)
    States += state
    state
  }

  def addTransition(from_To: String): (State, Transition) = {
    if (from_To.isEmpty)
      return (null, null)
    val edge = from_To.split("~")
    if (edge.length < 3)
      return (null, null)
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
      return (null, null)

    for (trans <- fromState.transitionsList) {
      if (trans.transitionName == edge(2) && !trans.isDeleted)
        return (null, null)
    }
    if (edge(2) == " " && !acceptsEpsilon)
      return (null, null)

    var transition = new Transition(edge(2), edge(1))
    fromState.transitionsList += transition
    (fromState, transition)
  }

  def getRandomPos: (Double, Double) = {
    val r = new Random()
    val x = 40 + (1060 - 40) * r.nextDouble()
    val y = 60 + (640 - 60) * r.nextDouble()
    (x, y)
  }

  def evaluate(expresion: String): Boolean = {
    if (States.isEmpty)
      return false
    for (elem <- States) {
      if (elem.isInicial) {
        var state = elem
        for (x <- expresion) {
          val transition = getTransition(state, x.toString)
          if (transition == null)
            return false
          state = getState(transition.DestinyStateName)
          if (state == null)
            return false
        }
        if (state.isFinal)
          return true
        else
          return false
      }
    }
    false
  }

  def getStatesbyValue(elem:State, value:String):ArrayBuffer[State]={
    var states=ArrayBuffer.empty[State]
    for(trans<-elem.transitionsList){
      if(trans.transitionName==value){
        states+=getState(trans.DestinyStateName)
      }
    }
    states
  }

  def getTrasitions(state: State, string: String): ArrayBuffer[Transition] = {
    var Transitions = ArrayBuffer.empty[Transition]
    for (trans <- state.transitionsList) {
      if (trans.transitionName == string)
        Transitions += trans
    }
    Transitions
  }

  def getTransitionsValues(from:State,to:State): ArrayBuffer[String] ={
    var transitionValues=ArrayBuffer.empty[String]
    for(trans<-from.transitionsList){
      if(trans.DestinyStateName==to.name){
        transitionValues+=trans.transitionName
      }
    }
    transitionValues
  }

  def getState(name: String): State = {
    for (elem <- States) {
      if (elem.name == name && !elem.isDeleted)
        return elem
    }
    null
  }

  def getTransition(elem: State, name: String): Transition = {
    for (trans <- elem.transitionsList) {
      if (trans.transitionName == name && !trans.isDeleted)
        return trans
    }
    null
  }

  def convertToDFA(): DFAManager = {
    null
  }

  def convertToNFA(): NFAManager = {
    null
  }

  def minimize(): DFAManager = {
    var states = ArrayBuffer.empty[State]
    var finalStates = ArrayBuffer.empty[State]

    for (elem <- States) {
      if (elem.isFinal)
        finalStates += elem
      else
        states += elem
    }

    var equivalentStates = ArrayBuffer.empty[ArrayBuffer[State]]
    if (states.nonEmpty)
      equivalentStates += states
    if (finalStates.nonEmpty)
      equivalentStates += finalStates
    
    
    var transitionsValues = ArrayBuffer.empty[String]

    for (elem <- States) {
      for (trans <- elem.transitionsList) {
        if (!transitionsValues.contains(trans.transitionName)) {
          transitionsValues += trans.transitionName
        }
      }
    }
    
    var looping = true
    while (looping) {
      var equivalentStatesRef = ArrayBuffer.empty[ArrayBuffer[State]]
      var nonequivalent = ArrayBuffer.empty[State]

      for (group <- equivalentStates) {
        equivalentStatesRef += ArrayBuffer.empty[State]
        equivalentStatesRef.last += group.head
        for (s <- group) {
          if (s.name != group(0).name) {
            var isEqivalent = true
            for (t <- transitionsValues) {
              if (getTrasitions(s, t).nonEmpty && getTrasitions(group.head, t).nonEmpty) {
                val state1 = getTrasitions(s, t).head
                val state2 = getTrasitions(group.head, t).head
                if (state1 != null && state2 != null) {
                  if (!statesAreEquivalent(getState(state1.DestinyStateName), getState(state2.DestinyStateName), equivalentStates)) {
                    isEqivalent = false
                  }
                }
              }
            }
            if (isEqivalent) {
              equivalentStatesRef(equivalentStatesRef.size - 1) += s
            } else {
              nonequivalent += s
            }
          }
        }
      }
      equivalentStates = equivalentStatesRef
      if (nonequivalent.nonEmpty) {
        equivalentStates += nonequivalent
      } else {
        looping = false
      }

    }


    val dfa = new DFAManager()
    for (group <- equivalentStates) {
      var stateName = ""
      var isFinal = false
      var isInitial = false
      for (st <- group) {
        stateName += st.name
        if (st.isFinal)
          isFinal = true
        if (st.isInicial)
          isInitial = true

      }
      val (x, y) = getRandomPos
      dfa.addState(stateName, x, y)
      dfa.States.last.isFinal = isFinal
      dfa.States.last.isInicial = isInitial
    }

    for (i <- equivalentStates.indices) {
      for (s <- States) {
        if (s.name == equivalentStates(i).head.name) {
          for (t <- s.transitionsList) {
            for (j <- equivalentStates.indices) {
              if (equivalentStates(j).contains(getState(t.DestinyStateName))) {
                dfa.addTransition(dfa.States(i).name + "~" + dfa.States(j).name + "~" + t.transitionName)
              }
            }
          }
        }
      }
    }

    dfa
  }

  def statesAreEquivalent(state1: State, state2: State, equivalentStates: ArrayBuffer[ArrayBuffer[State]]): Boolean = {
    if (state1.name == state2.name) {
      return true
    }
    for (g <- equivalentStates) {
      var found = false
      for (elem <- g) {
        if (elem.name == state1.name || elem.name == state2.name) {
          if (found) {
            return true
          }
          else {
            found = true
          }
        }
      }
      if (found) {
        return false
      }
    }
    true
  }

  def merge(state1: ArrayBuffer[State], state2: ArrayBuffer[State]): DFAManager = {

    val dfa = new DFAManager()

    for (elem1 <- state1) {
      for (elem2 <- state2) {
        val (x, y) = getRandomPos
        dfa.States += new State(elem1.name + elem2.name, x, y)
      }
    }
    var transitionsValues = ArrayBuffer.empty[String]
    for (elem <- state1) {
      for (trans <- elem.transitionsList) {
        if (!transitionsValues.contains(trans.transitionName)) {
          transitionsValues += trans.transitionName
        }
      }
    }
    for (elem <- state2) {
      for (trans <- elem.transitionsList) {
        if (!transitionsValues.contains(trans.transitionName)) {
          transitionsValues += trans.transitionName
        }
      }
    }

    for (elem1 <- state1) {
      for (elem2 <- state2) {
        for (trans <- transitionsValues) {
          val t1 = getTransition(elem1, trans)
          val t2 = getTransition(elem2, trans)
          val from = elem1.name + elem2.name
          var to = ""
          if (t1 != null) {
            to+=t1.DestinyStateName
          }
          if (t2 != null) {
            to+=t2.DestinyStateName
          }
          dfa.addTransition(from + "~" + to + "~" + trans)
        }
      }
    }

    dfa
  }

  def union(state1: ArrayBuffer[State], state2: ArrayBuffer[State]): DFAManager = {
    val dfa = merge(state1, state2)
    for (elem1 <- state1) {
      for (elem2 <- state2) {
        if (elem1.isInicial && elem2.isInicial) {
          for (elem <- dfa.States) {
            if (elem.name == (elem1.name + elem2.name)) {
              elem.isInicial = true
            }
          }
        }
        if (elem1.isFinal || elem2.isFinal) {
          for (elem <- dfa.States) {
            if (elem.name == (elem1.name + elem2.name)) {
              elem.isFinal = true
            }
          }
        }
      }
    }
    dfa
  }

  def intersection(state1:ArrayBuffer[State],state2:ArrayBuffer[State]): DFAManager ={
    val dfa = merge(state1, state2)
    for(elem1<-state1){
      for(elem2<-state2){
        if(elem1.isInicial && elem2.isInicial){
          for(elem<-dfa.States){
            if(elem.name==(elem1.name+elem2.name)){
              elem.isInicial=true
            }
          }
        }
        if(elem1.isFinal && elem2.isFinal ){
          for(elem<-dfa.States){
            if(elem.name==(elem1.name+elem2.name)){
              elem.isFinal=true
            }
          }
        }
      }
    }

    dfa
  }

  def diffrence(state1:ArrayBuffer[State],state2:ArrayBuffer[State]): DFAManager ={
    val dfa = merge(state1, state2)
    for(elem1<-state1){
      for(elem2<-state2){
        if(elem1.isInicial && elem2.isInicial){
          for(elem<-dfa.States){
            if(elem.name==(elem1.name+elem2.name)){
              elem.isInicial=true
            }
          }
        }
        if(elem1.isFinal && !elem2.isFinal ){
          for(elem<-dfa.States){
            if(elem.name==(elem1.name+elem2.name)){
              elem.isFinal=true
            }
          }
        }
      }
    }

    dfa
  }


  def complement(state1:ArrayBuffer[State]):DFAManager={
    val dfa = new DFAManager()
    var transitionsValues = ArrayBuffer.empty[String]


    for(i<- state1.indices){
      val (x, y) = getRandomPos
      dfa.addState(state1(i).name,x,y)
      dfa.States(i).isFinal= !state1(i).isFinal
      dfa.States(i).isInicial=state1(i).isInicial
    }
    for(elem<-state1){
      for(trans<-elem.transitionsList){
        dfa.addTransition(elem.name+"~"+trans.DestinyStateName+"~"+trans.transitionName)
      }
    }

    for (elem <- state1) {
      for (trans <- elem.transitionsList) {
        if (!transitionsValues.contains(trans.transitionName)) {
          transitionsValues += trans.transitionName
        }
      }
    }

    for(elem<-dfa.States){
      for(trans<-transitionsValues){
        val found = getTransition(elem, trans)
        val (x, y) = getRandomPos
        if(found==null){
          dfa.addState(elem.name+trans,x,y)
          dfa.addTransition(elem.name+"~"+elem.name+trans+"~"+trans)
          for(e<-dfa.States){
            if(e.name==(elem.name+trans)){
              e.isFinal
            }
          }
        }
      }
    }
    val (x, y) = getRandomPos
    var trashState= new State("qT",x,y)
    dfa.States+=trashState
      for (elem <- dfa.States) {
        for(value<-transitionsValues){
          if(getStatesbyValue(elem,value)==null){
            elem.transitionsList+=new Transition(value,trashState.name)
          }
        }
      }


    dfa
  }

}