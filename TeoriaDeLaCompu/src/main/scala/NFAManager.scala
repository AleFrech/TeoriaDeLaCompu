import java.util.UUID

import scala.collection.mutable.ArrayBuffer

/**
  * Created by AlejandroFrech on 2/1/2016.
  */
class NFAManager extends DFAManager {

  override def addTransition(from_To: String):(State,Transition)={
    if(from_To.isEmpty)
      return (null,null)
    val edge = from_To.split("~")
    if (edge.length < 3)
      return (null,null)
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
      return (null,null)

    var transition = new Transition(edge(2), edge(1))
    fromState.transitionsList += transition
    (fromState, transition)
  }

  override def evaluate(expresion: String): Boolean = {
    if (States.isEmpty)
      return false
    var initial: State = null
    for (elem <- States) {
      if (elem.isInicial) {
        initial = elem
      }
    }
    if(!acceptsEpsilon) {
      evaluateNFA(expresion, initial)
    }else{
      evaluateNFA_E(expresion, initial)
    }
  }

  def evaluateNFA(expresion: String, state: State): Boolean = {
    if (expresion.isEmpty)
      return state.isFinal
    for (trans <- state.transitionsList) {
      if (trans.transitionName == expresion.charAt(0).toString) {
        if (evaluateNFA(expresion.substring(1), getState(trans.DestinyStateName)))
          return true
      }
    }
    false
  }

  def evaluateNFA_E(expresion: String, state: State): Boolean= {
    var statesArray= ArrayBuffer.empty[State]
    statesArray+=state
    var clouserdStates=ArrayBuffer.empty[State]
    for(x<-expresion){
      var clousers=ArrayBuffer.empty[State]
      for(elem<-statesArray){
        clousers+=elem
        clouser(elem,clousers)
      }
       clouserdStates.clear()
      for(elem<-clousers){
        for(elem2<-getStatesbyValue(elem,x.toString)){
          clouserdStates+=elem2
        }
      }
    }
    statesArray.clear()
    for(elem<-clouserdStates){
      statesArray+=elem
      clouser(elem,statesArray)
    }
    for(elem<-statesArray){
      if(elem.isFinal)
        return true
    }
    false
  }


  def clouser(state:State,states: ArrayBuffer[State]): Unit ={
    val statesArray = getStatesbyValue(state, " ")
    for(elem<-statesArray){
      if(!states.contains(elem)){
        states+=elem
        clouser(elem,states)
      }
    }
  }

  override def convertToDFA():DFAManager={
    if(States.isEmpty)
      return null
    val dfa = new DFAManager()
    var initial:State=null
    var transitionsValues=ArrayBuffer.empty[String]
    for(elem<-States){
      if(elem.isInicial)
        initial=elem
      for(trans<-elem.transitionsList){
        if(!transitionsValues.contains(trans.transitionName)){
          transitionsValues+=trans.transitionName
        }
      }
    }
    var entryStates=ArrayBuffer.empty[ArrayBuffer[State]]
    var exitStates=ArrayBuffer.empty[ArrayBuffer[State]]
    entryStates+= ArrayBuffer.empty[State]
    var transtionForStates=ArrayBuffer.empty[String]
    transtionForStates+=""
    if(initial==null){
      exitStates+=ArrayBuffer.empty[State]
    }else{
      exitStates+=ArrayBuffer.empty[State]
      exitStates(0)+=initial
    }
    var posibleStates=ArrayBuffer.empty[ArrayBuffer[State]]
    posibleStates+=exitStates.last
    var i=0
    while(i<posibleStates.size){
      for(v<-transitionsValues){
        var statesUnion= ArrayBuffer.empty[State]
        for(pos<-posibleStates(i)){
          for(trans<-pos.transitionsList){
            if(trans.transitionName==v){
              if(!statesUnion.contains(getState(trans.DestinyStateName))) {
                statesUnion += getState(trans.DestinyStateName)
              }
            }
          }
        }
        entryStates+=posibleStates(i)
        exitStates+=statesUnion
        transtionForStates+=v
        var canAdd=true
        for(x<-posibleStates){
          var exists=false
          if(x.size!=statesUnion.size)
            exists=true
          else{
            for(n<-x.indices){
              if(x(n).name!=statesUnion(n).name){
                exists=true
              }
            }
          }
          if(!exists)
            canAdd=false
        }
        if(canAdd){
          posibleStates+=statesUnion
        }
      }
      i+=1
    }
    for(p<-posibleStates){
      var str=""
      var fnal=false
      for(elem<-p){
        str+=elem.name
        for(el<-States){
          if(el.name==str){
            if(el.isFinal)
              fnal=true
          }
        }
        if(elem.isFinal)
          fnal=true
      }
      val (x,y) = getRandomPos
      dfa.addState(str,x,y)
      dfa.States.last.isFinal=fnal
    }
    for(i<-exitStates.indices){
      var originName=""
      for(s<-entryStates(i)){
        originName+=s.name
      }
      var destinyName=""
      for(s<-exitStates(i)){
        destinyName+=s.name
      }
      dfa.addTransition(originName+"~"+destinyName+"~"+transtionForStates(i))
    }
      dfa.States(0).isInicial=true
      dfa
  }

  override def convertToNFA():NFAManager={
    var states= ArrayBuffer.empty[State]
    var transitionValues=ArrayBuffer.empty[String]
    var toStates=ArrayBuffer.empty[ArrayBuffer[State]]
    var language=ArrayBuffer.empty[String]

    for(elem<-States){
      for(trans<-elem.transitionsList){
        if(!language.contains(trans.transitionName)){
          language+=trans.transitionName
        }
      }
    }
    language.remove(language.indexOf(" "))

    for(elem<-States){
      for(l<-language){
        var posiblities= ArrayBuffer.empty[State]
        var transitionOne=getTrasitions(elem," ")
        transitionOne+=new Transition(" ",elem.name)
        for(t1<-transitionOne){
          val transtionTwo = getTrasitions(getState(t1.DestinyStateName), l)
          for(t2<-transtionTwo){
            var transitionThree=getTrasitions(getState(t2.DestinyStateName)," ")
            transitionThree+=new Transition(" ",t2.DestinyStateName)
            for(t3<-transitionThree){
              if(!posiblities.contains(getState(t3.DestinyStateName))){
                posiblities+=getState(t3.DestinyStateName)
              }
            }
          }
        }
        states+=elem
        transitionValues+=l
        toStates+=posiblities
      }
    }
    val nfa = new NFAManager()
    for(elem<-States){
      nfa.States+=new State(elem.name,elem.posX,elem.posY)
      nfa.States.last.isInicial=elem.isInicial
      nfa.States.last.isFinal=elem.isFinal

    }
    for(i<-states.indices){
      for(s<-toStates(i)){
        nfa.addTransition(states(i).name+"~"+s.name+"~"+transitionValues(i))
      }
    }
    nfa
  }

  def symbolNFA_E(str:String): NFAManager ={
    val nfa_e= new NFAManager
    nfa_e.acceptsEpsilon=true

    val(x,y)=getRandomPos
    var state1= new State(UUID.randomUUID().toString,x,y)
    state1.isInicial=true
    val(x2,y2)=getRandomPos
    var state2= new State(UUID.randomUUID().toString,x2,y2)
    state2.isFinal=true

    var trans=new Transition(str,state2.name)
    state1.transitionsList+=trans

    nfa_e.States+=state1
    nfa_e.States+=state2

     nfa_e
  }

  def concatNFA_E(left:NFAManager,right: NFAManager): NFAManager ={
    var leftFinal:State=null
    var rightInicial:State=null
    for(elem<-left.States){
      if(elem.isFinal){
        leftFinal=elem
        elem.isFinal=false
        leftFinal.isFinal=false
      }
    }
    for(elem<-right.States){
      if(elem.isInicial){
        rightInicial=elem
        elem.isInicial=false
        rightInicial.isInicial=false
      }
    }

    leftFinal.transitionsList+= new Transition(" ",rightInicial.name)
    val nfa_e= new NFAManager
    nfa_e.acceptsEpsilon=true
    nfa_e.States.insertAll(nfa_e.States.size,left.States)
    nfa_e.States.insertAll(nfa_e.States.size,right.States)

    nfa_e
  }

  def OrNFA_E(left:NFAManager,right: NFAManager): NFAManager ={
    var leftInicial:State=null
    var leftFinal:State=null
    var rightInicial:State=null
    var rightFinal:State=null

    for(elem<-left.States){
      if(elem.isInicial){
        leftInicial=elem
        leftInicial.isInicial=false
        elem.isInicial=false
      }
      if(elem.isFinal){
        leftFinal=elem
        leftFinal.isFinal=false
        elem.isFinal=false
      }
    }
    for(elem<-right.States) {
      if (elem.isInicial) {
        rightInicial = elem
        rightInicial.isInicial = false
        elem.isInicial=false
      }
      if (elem.isFinal) {
        rightFinal = elem
        rightFinal.isFinal = false
        elem.isFinal=false
      }
    }

    val(x,y)=getRandomPos
    var state1= new State(UUID.randomUUID().toString,x,y)
    state1.isInicial=true
    val (x2,y2)=getRandomPos
    var state2= new State(UUID.randomUUID().toString,x2,y2)
    state2.isFinal=true


    state1.transitionsList+=new Transition(" ",leftInicial.name)
    state1.transitionsList+=new Transition(" ",rightInicial.name)

    leftFinal.transitionsList+=new Transition(" ",state2.name)
    rightFinal.transitionsList+=new Transition(" ",state2.name)

    val nfa_e= new NFAManager
    nfa_e.acceptsEpsilon=true
    nfa_e.States+=state1
    nfa_e.States.insertAll(nfa_e.States.size,left.States)
    nfa_e.States.insertAll(nfa_e.States.size,right.States)
    nfa_e.States+=state2

     nfa_e
  }

  def KleeneNFA_E(nfa_e:NFAManager): NFAManager={
    var nfa_eInicial:State=null
    var nfa_eFinal:State=null

    for(elem<-nfa_e.States){
      if(elem.isInicial){
        nfa_eInicial=elem
        nfa_eInicial.isInicial=false
        elem.isInicial=false
      }
      if(elem.isFinal){
        nfa_eFinal=elem
        nfa_eFinal.isFinal=false
        elem.isFinal=false
      }
    }

    nfa_eFinal.transitionsList+=new Transition(" ",nfa_eInicial.name)
    val(x,y)=getRandomPos
    var state1= new State(UUID.randomUUID().toString,x,y)
    state1.isInicial=true
    val(x2,y2)=getRandomPos
    var state2= new State(UUID.randomUUID().toString,x2,y2)
    state2.isFinal=true

    nfa_eFinal.transitionsList+=new Transition(" ",state2.name)
    state1.transitionsList+=new Transition(" ",nfa_eInicial.name)
    state1.transitionsList+=new Transition(" ",state2.name)

    val newNFA_E=new NFAManager
    newNFA_E.acceptsEpsilon=true
    newNFA_E.States+=state1
    newNFA_E.States.insertAll(newNFA_E.States.size,nfa_e.States)
    newNFA_E.States+=state2
    newNFA_E
  }

}