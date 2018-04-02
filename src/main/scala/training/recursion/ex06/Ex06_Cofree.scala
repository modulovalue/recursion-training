package training.recursion.ex06

import matryoshka.data._
import matryoshka._
import matryoshka.implicits._
import matryoshka.patterns._
import scalaz._

// -------------------- the DSL --------------------
sealed trait Expr[A]

case class IntValue[A](v: Int)    extends Expr[A]
case class DecValue[A](v: Double) extends Expr[A]
case class Sum[A](a: A, b: A)     extends Expr[A]
case class Square[A](a: A)        extends Expr[A]

sealed trait ExprType
case object IntExpr extends ExprType
case object DecExpr extends ExprType
// -------------------------------------------------

object Ex06_Cofree extends App with Ex06_Traverse {

  def int(i: Int): Fix[Expr] = IntValue[Fix[Expr]](i).embed

  // ---------- labelling expressions with Cofree

  val inferType: Algebra[Expr, Cofree[Expr, ExprType]] = {
    case IntValue(v) => Cofree.apply(IntExpr, IntValue(v))
    case DecValue(v) => Cofree.apply(DecExpr, DecValue(v))
    case Sum(a, b)   => Cofree.apply(IntExpr, IntValue(10000)) // TODO
    case Square(a)   => Cofree.apply(IntExpr, IntValue(10000))
  }

  def toStr(exp: Expr[String]): String = exp match {
    case IntValue(v) => v.toString
    case DecValue(v) => v.toString
    case Sum(d1, d2) => s"($d1 + $d2)"
    case Square(d)   => s"($d^2)"
  }

  val expr: Fix[Expr] = Sum(Square(Square(int(3)).embed).embed, Square(Square(Square(int(5)).embed).embed).embed).embed

  val typedExpr: Cofree[Expr, ExprType] = expr.cata(inferType)

  val toTypedStr: Algebra[EnvT[ExprType, Expr, ?], String] = {
    case _ => ??? // TODO
  }

  println(typedExpr.cata(toTypedStr))
}