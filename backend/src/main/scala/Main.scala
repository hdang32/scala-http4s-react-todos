
@main def hello(): Unit =
  val keyValPattern: Regex = "([0-9a-zA-Z- ]+): ([0-9a-zA-Z-#()/. ]+)".r
  
  val planets =
  List(("Mercury", 57.9), (24f, 108.2), ("Earth", 149.6),
       ("Mars", 227.9), ("Jupiter", 778.3))

  class Body(var body: String)
  case class Message(sender: String, recipient: String, body: Body)
  val message4 = Message("julien@bretagne.fr", "travis@washington.us", Body("Me zo o komz gant ma amezeg"))
  val message5 = message4.copy(sender = message4.recipient, recipient = "claire@bourgogne.fr")
  message5.sender  // travis@washington.us
  message5.recipient // claire@bourgogne.fr
  message5.body  // "Me zo o komz gant ma amezeg"
import org.w3c.dom.Node
import scala.util.matching.Regex

case class Hao(boobs: Boolean)

class DesugaredHao(val boobs: Boolean, val butt: Boolean) {
  override def equals(x: Any): Boolean = ???
  override def hashCode(): Int = ???
  override def toString(): String = ???
}
object DesugaredHao {
  def unapply(hao: DesugaredHao): Option[(Boolean, Boolean)] = Some((hao.boobs, hao.butt))
}


def foo(hao: DesugaredHao) =
  hao match
    case DesugaredHao(boobs, butt) => println(s"$boobs, $butt")
  

sealed trait Node
case class TreeNode(left: Node, right: Node) extends Node
case class Leaf() extends Node

def walkTree(node: Node): Unit = node match
  case TreeNode(left, right) => 
    walkTree(left)
    walkTree(right)
  case Leaf() =>
    println("At leaf")
