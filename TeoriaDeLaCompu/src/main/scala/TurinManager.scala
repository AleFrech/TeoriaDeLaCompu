/**
lo  * Created by AlejandroFrech on 3/28/2016.
  */
class TurinManager extends DFAManager{

   def evaluateTurin(expresion: String):(Boolean, String)  = {
    if(expresion.isEmpty)
      return (false,expresion)

    var tape= "B"+expresion+"B"
    var pointer=1
    var currentState:State=null
    for(elem<-States) {
      if (elem.isInicial) {
        currentState = elem
      }
    }
    while(!currentState.isFinal){
      if(pointer==tape.length){
        pointer-=1
      }else if(pointer== -1){
        pointer+=1
      }
      var tmpTrans:Transition=null
      for(trans<-currentState.transitionsList){
        if(trans.transitionName.charAt(1)==tape.charAt(pointer)){
          tmpTrans=trans
        }
      }
      if(tmpTrans==null){
        return(false,tape)
      }
      val stringbuilder = new StringBuilder(tape)
      stringbuilder(pointer)=tmpTrans.transitionName.charAt(3)
      tape=stringbuilder.toString()
      val direction = tmpTrans.transitionName.charAt(5).toString
      if(direction=="L")
        pointer-=1
      else if(direction=="R")
        pointer+=1

      currentState=getState(tmpTrans.DestinyStateName)
    }
    (true,tape)
  }

}
