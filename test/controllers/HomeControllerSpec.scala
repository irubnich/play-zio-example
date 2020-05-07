package controllers

import akka.stream.Materializer
import controllers.HomeController.{TestInput, TestOutput}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "HomeController GET" should {

    "show a success result from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) must include("1 + 2 = 3")
    }

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) must include("3")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) must include("3")
    }
  }

  "HomeController POST" should {
    "parse JSON correctly and return a successful result" in {
      implicit val mat: Materializer = inject[Materializer]

      val controller = new HomeController(stubControllerComponents())
      val home = controller.testJson().apply(FakeRequest(POST, "/").withBody(Json.toJson(TestInput("test"))))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsJson(home) mustEqual Json.toJson(TestOutput("Hello, test"))
    }

    "parse an invalid JSON input and return a failure" in {
      implicit val mat: Materializer = inject[Materializer]

      val controller = new HomeController(stubControllerComponents())
      val home = controller.testJson().apply(FakeRequest(POST, "/").withBody(Json.obj("foo" -> "bar")))

      status(home) mustBe BAD_REQUEST
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include("Invalid JSON input.")
    }
  }
}
