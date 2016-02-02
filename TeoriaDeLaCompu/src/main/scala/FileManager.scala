import java.io.{FileOutputStream, ObjectOutputStream}

/**
  * Created by AlejandroFrech on 2/1/2016.
  */
object FileManager {

  def saveDFAToFile(manager: DFAManager,name:String): Unit ={
    val oos = new ObjectOutputStream(new FileOutputStream(name+".dfa"))
    oos.writeObject(manager)
    oos.close
  }

}
