package io.digitalcat.publictransportation.services.identity.impl.util

import com.typesafe.config.ConfigFactory
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson}
import play.api.libs.json.{Format, Json}

object JwtTokenUtil {
  val secret = ConfigFactory.load().getString("jwt.secret")
  val algorithm = JwtAlgorithm.HS512
  val five_minutes = 5*60
  val one_day = 24*60*60

  def tokenize[C](content: C)(implicit format: Format[C]): Token = {
    Json.toJson(content).toString()

    val authClaim = JwtClaim(Json.toJson(content).toString()).expiresIn(five_minutes).issuedNow
    val refreshClaim = JwtClaim(Json.toJson(content).toString()).expiresIn(one_day).issuedNow

    val authToken = JwtJson.encode(authClaim, secret, algorithm)
    val refreshToken = JwtJson.encode(refreshClaim, secret, algorithm)

    Token(
      authToken = authToken,
      refreshToken = refreshToken
    )
  }
}

case class Token(authToken: String, refreshToken: String)
object Token {
  implicit val format: Format[Token] = Json.format
}
