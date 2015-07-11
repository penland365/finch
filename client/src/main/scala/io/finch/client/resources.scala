package io.finch
package client

import cats.data.Xor
import com.twitter.finagle.httpx.{HeaderMap, Response}
import com.twitter.io.Buf

sealed trait Resource[A] { 
  val headers: HeaderMap
  val content: A

  def as[B](f: A => B): Resource[B] = Ok(headers, f(content))
  def asString(): String Xor Resource[A] = Utf8.unapply(buf) match {
      case Some(x) => Xor.Right(Ok(headers, x.toString))
      case None    => Xor.Left("could not decode Buf => String")
  }
}

object Resource {
  def fromResponse(resp: Response): Resource[Buf] = resp.statusCode match {
    case 200 => Ok(resp.headerMap, resp.content)
    case 201 => Created(resp.headerMap, resp.content)
    case 202 => Accepted(resp.headerMap, resp.content)
    case _   => Shruggie(resp.headerMap, resp.content) 
  }
}

case class Ok[A](headers: HeaderMap, content: A) extends Resource[A]
case class Created[A](headers: HeaderMap, content: A) extends Resource[A]
case class Accepted[A](headers: HeaderMap, content: A) extends Resource[A]

case class Shruggie[A](headers: HeaderMap, content: A) extends Resource[A]
