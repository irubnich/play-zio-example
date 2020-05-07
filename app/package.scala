import play.api.mvc.{Action, ActionBuilder, BodyParser, Result}
import zio.{Runtime, UIO}

package object application {
  implicit class ActionBuilderOps[+R[_], B](ab: ActionBuilder[R, B]) {
    case class AsyncTaskBuilder() {
      def apply(cb: R[B] => UIO[Result]): Action[B] = {
        ab.async { c =>
          val value: UIO[Result] = cb(c)
          Runtime.default.unsafeRunToFuture(value)
        }
      }

      def apply[A](bp: BodyParser[A])(cb: R[A] => UIO[Result]): Action[A] = {
        ab.async[A](bp) { c =>
          val value: UIO[Result] = cb(c)
          Runtime.default.unsafeRunToFuture(value)
        }
      }
    }

    def asyncTask: AsyncTaskBuilder = AsyncTaskBuilder()
  }
}
