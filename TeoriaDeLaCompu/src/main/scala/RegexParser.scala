import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks

/**
  * Created by AlejandroFrech on 4/1/2016.
  */
class RegexParser {

  var symbol:String=""
  var childs= ArrayBuffer.empty[RegexParser]

  def parse(regex:String): Unit ={
    var str=""
    if(regex.length==1)
      symbol=regex
    else {
      if (regex.charAt(0).toString == "(") {
        val loop=new Breaks
        var pair = 0
        var i=1
        loop.breakable {
          while(i<regex.length){
            if (regex.charAt(i).toString == ")" && pair == 0)
              loop.break()
            else if (regex.charAt(i).toString == "(")
              pair += 1
            else if (regex.charAt(i).toString == ")")
              pair -= 1
            str = str+regex(i).toString
            i+=1
          }
        }
         if((i+1)==regex.length ){
           symbol="()"
           var tmp = new RegexParser
           tmp.parse(str)
           childs+=tmp
         }else if (regex.charAt(i + 1).toString == "*") {
          if ((i + 2) == regex.length) {
            symbol = "*"
            var tmp = new RegexParser
            tmp.parse(str)
            childs += tmp
          } else {
            symbol = regex(i + 2).toString
            splitLeftAndRifghtChild(i,regex)
          }
        } else if (regex.charAt(i + 1).toString == "+" || regex.charAt(i + 1).toString == ".") {
          symbol = regex.charAt(i + 1).toString
          splitLeftAndRifghtChild(i,regex)
        }
      } else {
        if(regex.charAt(1).toString=="*"){
          if (2 == regex.length) {
            symbol = "*"
            var tmp = new RegexParser
            tmp.parse(regex.charAt(0).toString)
            childs += tmp
          } else {
            symbol = regex.charAt(2).toString
            splitLeftAndRifghtChild(0,regex)
          }
        }else if (regex.charAt(1).toString == "+" || regex.charAt(1).toString == ".") {
          symbol = regex.charAt(1).toString
          splitLeftAndRifghtChild(-1,regex)
        }
      }
    }
  }

  def splitLeftAndRifghtChild(cont:Int,regex:String): Unit = {
    var leftStr = ""
    var rightStr = ""
    var j = 0
    while (j < (cont + 2)) {
      leftStr += regex(j).toString
      j += 1
    }
    j += 1
    while (j < regex.length) {
      rightStr += regex(j).toString
      j += 1
    }
    var leftChild = new RegexParser
    leftChild.parse(leftStr)
    var rightChild = new RegexParser
    rightChild.parse(rightStr)
    childs += leftChild
    childs += rightChild
  }

  def getNFA_E(): NFAManager ={
    val nfa_e= new NFAManager
    if(symbol=="()") {
      childs.head.getNFA_E()
    }else if(symbol=="*"){
        val left=childs.head.getNFA_E()
        nfa_e.KleeneNFA_E(left)
    }else if(symbol=="+"){
      val left=childs.head.getNFA_E()
      val right=childs(1).getNFA_E()
      nfa_e.OrNFA_E(left,right)
    }else if(symbol=="."){
      val left=childs.head.getNFA_E()
      val right=childs(1).getNFA_E()
      nfa_e.concatNFA_E(left,right)
    }else{
      nfa_e.symbolNFA_E(symbol)
    }
  }
}
