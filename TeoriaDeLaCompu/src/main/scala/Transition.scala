/**
  * Created by AlejandroFrech on 1/27/2016.
  */
class Transition(Tname:String,Sname:String){
    var transitionName:String=Tname
    var DestinyStateName:String=Sname
    var isDeleted=false
    var isbuckle=false
    var transitionComponents= new TransitionComponents()
}
