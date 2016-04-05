import scala.collection.mutable.ArrayBuffer

/**
  * Created by AlejandroFrech on 1/27/2016.
  */
class State(pname:String,posx:Double,posy:Double) {
    var name=pname
    var isDeleted=false
    var isInicial=false
    var isFinal=false
    var stateComponents= new StateComponents()
    var transitionsList =  ArrayBuffer.empty[Transition]
    var posX=posx
    var posY=posy
}
