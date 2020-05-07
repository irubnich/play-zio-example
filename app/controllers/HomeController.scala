package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json.{Format, JsError, JsSuccess, JsValue, Json}
import application._
import zio._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Simple example of an action that can't fail.
   *
   * In cURL:
   * $ curl localhost:9000
   */
  def index(): Action[AnyContent] = Action.asyncTask { _ =>
    val f = for {
      one <- ZIO.succeed(1)
      two <- ZIO.succeed(2)
    } yield one + two

    f.map(int => Ok(s"1 + 2 = $int"))
  }

  /**
   * Simple example of an action with error handling.
   * This will transform the input if the input is okay, or it will return an error.
   * Note that if the `fold` is removed, ZIO complains that the error must be handled.
   *
   * Try these examples in cURL:
   * $ curl localhost:9000 -H "Content-Type: application/json" -d '{"input": "testing"}'
   * $ curl localhost:9000 -H "Content-Type: application/json" -d '{"foo": "bar"}'
   */
  def testJson(): Action[JsValue] = Action.asyncTask(controllerComponents.parsers.json) { request =>
    import HomeController._

    val response = request.body.validate[TestInput] match {
      case JsSuccess(value, _) => ZIO.succeed(TestOutput(s"Hello, ${value.input}"))
      case JsError(_) => ZIO.fail(TestError("Invalid JSON input."))
    }

    response.fold(
      error => BadRequest(Json.toJson(error)),
      success => Ok(Json.toJson(success))
    )
  }
}

object HomeController {
  case class TestInput(input: String)
  implicit val inputFormat: Format[TestInput] = Json.format[TestInput]

  case class TestOutput(output: String)
  implicit val outFormat: Format[TestOutput] = Json.format[TestOutput]

  case class TestError(error: String)
  implicit val errFormat: Format[TestError] = Json.format[TestError]
}
