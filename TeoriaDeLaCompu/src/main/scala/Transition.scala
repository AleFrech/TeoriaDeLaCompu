/**
  * Created by AlejandroFrech on 1/27/2016.
  */
@SerialVersionUID(100L)
class Transition(Tname:String,Sname:String)extends Serializable {
    var transitionName:String=Tname
    var DestinyStateName:String=Sname
    var isDeleted=false
    var transitionComponents= new TransitionComponents()
}
