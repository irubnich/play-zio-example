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
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(): Action[AnyContent] = Action.asyncTask { _ =>
    val f = for {
      one <- ZIO.succeed(1)
      two <- ZIO.succeed(2)
    } yield one + two

    f.map(int => Ok(int.toString))
  }

  def testJson(): Action[JsValue] = Action.asyncTask(controllerComponents.parsers.json) { request =>
    import HomeController._

    val response = request.body.validate[TestInput] match {
      case JsSuccess(value, _) => ZIO.succeed(TestOutput(value.input))
      case JsError(errors) => ZIO.fail(TestError(errors.toString()))
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
