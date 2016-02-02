import scala.collection.mutable.ArrayBuffer

/**
  * Created by AlejandroFrech on 1/27/2016.
  */
@SerialVersionUID(100L)
class State(pname:String)extends Serializable {
    var name=pname
    var isDeleted=false
    var isInicial=false
    var isFinal=false
    var stateComponents= new StateComponents()
    var transitionsList =  ArrayBuffer.empty[Transition]
}
