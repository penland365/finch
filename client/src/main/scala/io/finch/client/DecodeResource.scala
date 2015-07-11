package io.finch
package client

import argonaut._, Argonaut._
import cats.data.Xor
import com.twitter.io.Buf
import com.twitter.io.Buf.Utf8

trait DecodeResource[A] {
  def apply(buf: Buf): DecodeResult[A]
}

object DecodeResource {

  implicit val decodeStringResource: DecodeResource[String] = new DecodeResource[String] {
    def apply(buf: Buf): DecodeResult[String] = Utf8.unapply(buf) match {
      case Some(x) => Xor.Right(x.toString)
      case None    => Xor.Left("could not decode Buf => String")
    }
  }

  //implicit val decodeJsonResource: DecodeResource[Json] = new DecodeResource[Json] {
  //  def apply(buf: Buf): Json = asString(buf)
  //    .parseOption
  //    .getOrElse(jEmptyObject)
  //}
}
