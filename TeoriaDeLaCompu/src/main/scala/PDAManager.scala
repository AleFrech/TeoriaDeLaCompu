import java.util
import scala.collection.mutable.ArrayBuffer

/**
  * Created by AlejandroFrech on 3/11/2016.
  */
 class PDAManager extends NFAManager {
  acceptsEpsilon=true

  override def evaluate(expresion: String): Boolean = {
    val initial = "Z"
    var transitionValues=ArrayBuffer.empty[String]
    var consumed=ArrayBuffer.empty[String]
    var currentState:State=null
    var accepted=true
    for(elem<-States){
      if(elem.isInicial){
        currentState=elem
      }
      for(trans<-elem.transitionsList){
        val pvalues = trans.transitionName.drop(1).dropRight(1)
        val plist = pvalues.split("-")
        if(!transitionValues.contains(plist(0)))
          transitionValues+=plist(0)
      }
    }
    val stack = new util.Stack[String]
    stack.push(initial)
    for(i<- 0 until expresion.length){
      val char = expresion.charAt(i).toString
      val pop = stack.peek()
      stack.pop()
      var tmptransition:Transition=null
      for(trans<-currentState.transitionsList){
        val pvalues = trans.transitionName.drop(1).dropRight(1)
        val plist = pvalues.split("-")
        if(plist(0)==char && plist(1)==pop ){
          tmptransition=trans
        }
      }
      if(tmptransition==null){
        accepted=false
        stack.push(pop)
      }else{
        val pvalues = tmptransition.transitionName.drop(1).dropRight(1)
        val plist = pvalues.split("-")
        val push = plist(2)
        if(!push.isEmpty && push!=" "){
          for(j<- 0 until push.length){
            stack.push(push.substring(push.length-1-j,push.length-j))
          }
        }
        currentState=getState(tmptransition.DestinyStateName)
      }
    }
    while(currentState!=null && !currentState.isFinal && !consumed.contains(currentState.name)){
        consumed+=currentState.name
      val pop = stack.peek()
        stack.pop()
        var tmptransition:Transition=null
          for(trans<-currentState.transitionsList){
            val pvalues = trans.transitionName.drop(1).dropRight(1)
            val plist = pvalues.split("-")
            if(plist(0)==" " && plist(1)==pop ){
              tmptransition=trans
            }
          }
        if(tmptransition!=null){
          val pvalue = tmptransition.transitionName.drop(1).dropRight(1).split("-")
          val push = pvalue(2)
          if(!push.isEmpty){
            for(j<- 0 until push.length){
              stack.push(push.substring(push.length-1-j,push.length-j))
            }
          }
          currentState=getState(tmptransition.DestinyStateName)
        }else{
          currentState=null
        }
    }
    if(currentState==null){
      accepted=false
    }else {
      if (!currentState.isFinal || stack.isEmpty ) {
        accepted = false
      }
    }
       accepted
  }

  def CFLToPDA(cfl:Map[String,ArrayBuffer[String]]):PDAManager={
    val newPDA = new PDAManager()
    for(i<-0 until 3 ){
      val (x, y) = getRandomPos
      newPDA.addState("q"+i,x,y)
    }
    for(elem<-newPDA.States){
      if(elem.name=="q0")
        elem.isInicial=true
      if(elem.name=="q2")
        elem.isFinal=true
    }
    val(variable, _)=cfl.head
    newPDA.addTransition("q0~q1~( -Z-"+variable+"Z)")
    newPDA.addTransition("q1~q2~( -Z-Z)")
    cfl.foreach{
      case(v,p)=> for(prod<-p){
        newPDA.addTransition("q1~q1~( -"+v+"-"+prod+")")
      }
    }
    cfl.foreach{
      case(v,p)=> for(prod<-p){
        if(prod!=" "){
          for(terminal<-prod){
            cfl.foreach{
              case(variables,_)=>
                if(variables!=terminal.toString)
                  newPDA.addTransition("q1~q1~("+terminal.toString+"-"+terminal.toString+"- )")
            }
          }
        }
      }
    }
    newPDA
  }


}
