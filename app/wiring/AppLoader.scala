package wiring

import java.util.concurrent.atomic.AtomicInteger

import org.graalvm.polyglot.{Context, Source}
import akka.stream.Materializer
import play.api.mvc._

import scala.concurrent.ExecutionContext
import play.api.ApplicationLoader.{Context => PlayContext}
import play.api._
import play.api.mvc.Results._
import play.api.routing.Router
import play.api.routing.sird._

import scala.concurrent.Future
import scala.util.Random

class AppComponents(playContext: PlayContext) extends BuiltInComponentsFromContext(playContext) with NoHttpFiltersComponents {

  override val httpFilters = Seq(new LoggingFilter())

  val random  = new Random()
  val context = Context.newBuilder().allowAllAccess(true).build()

  val plotFunction = context
    .eval(Source.newBuilder("R", getClass.getClassLoader.getResource("plot.R")).build)
    .as(classOf[Double => String])
  val plotListFunction = context
    .eval(Source.newBuilder("R", getClass.getClassLoader.getResource("plotList.R")).build)
    .as(classOf[(Int, Array[Double]) => String])
  val plotDensityFunction = context
    .eval(Source.newBuilder("R", getClass.getClassLoader.getResource("plotDensity.R")).build)
    .as(classOf[(Int, Array[Double]) => String])
  val plotMatrix = context
    .eval(
      Source
        .newBuilder(
          "R",
          getClass.getClassLoader.getResource("plotMatrix.R")
        )
        .build
    )
    .as(classOf[(Int, Array[Array[Double]]) => String])

  val router: Router = Router.from {

    case GET(p"/") =>
      Action {
        Ok(s"Hello Someone")
      }

    case GET(p"/sum") =>
      Action.async {
        Future {
          val array = List(1.5, 2d, 3d).toArray
          Ok(context.eval("R", "sum").execute(array).asDouble().toString)
        }
      }

    case GET(p"/list/${int(size)}") =>
      Action.async {
        Future {
          val doubles = List.fill(size)(random.nextDouble())
          println(doubles)
          Ok(plotListFunction(size - 1, doubles.toArray)).as("image/svg+xml")
        }
      }

    case GET(p"/density/${int(size)}") =>
      Action.async {
        Future {
          val doubles = List.fill(size)(random.nextDouble())
          println(doubles)
          Ok(plotDensityFunction(size - 1, doubles.toArray)).as("image/svg+xml")
        }
      }

    case GET(p"/matrix/${int(size)}") =>
      Action.async {
        Future {
          val matrix = Array.fill(size, 2)(random.nextDouble())
          println(matrix.map(_.toList).toList.mkString("\n"))
          Ok(plotMatrix(size, matrix)).as("image/svg+xml")
        }
      }

    case GET(p"/surprise") =>
      Action.async {
        Future {
          val doubles = List(0.36315048447390874, 0.5498189141196997, 0.11609019267899845, 0.08605372277203527, 0.08796831674167283, 0.5018591381315751,
            0.3531076715544609, 0.5913408357921895, 0.4081766015697982, 0.5697535999783253, 0.4505864187079873, 0.5868866818998605, 0.36866671935595174,
            0.472092543675696, 0.6553937356252264, 0.09150583724334516, 0.40291530573858025, 0.37262490131833614, 0.4117991287764934, 0.4097081011268534,
            0.2656149679439649, 0.5282239440418138, 0.8775631963174546, 0.696363221603529, 0.05141863322518503, 0.9304757361386297, 0.4447349611632263,
            0.06895849524325293, 0.5493404027007355, 0.47095027679289003, 0.8745629925092736, 0.33315413564249075, 0.5348935042046451, 0.6768165202409387,
            0.3990897725993221, 0.9195667726972713, 0.9858363463266493, 0.8613142432227676, 0.41641334080632797, 0.9431888664946368, 0.12243567645316789,
            0.6126975909312373, 0.2122634500545798, 0.39157805174756244, 0.6194455659391642, 0.9056157530897032, 0.4774543411060942, 0.32392688661373015,
            0.814539628832605, 0.0604989977582948)
          println(doubles)
          Ok(plotDensityFunction(doubles.length - 1, doubles.toArray)).as("image/svg+xml")
        }
      }

    case GET(p"/plot/${double(num)}") =>
      Action.async {
        Future {
          Ok(plotFunction(num)).as("image/svg+xml")
        }
      }

    case GET(p"/plotRandom") =>
      Action.async {
        Future {
          Ok(plotFunction(random.nextDouble()))
            .as("image/svg+xml")
            .withHeaders("Refresh" -> "0.5")
        }
      }

  }

}

class LoggingFilter(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  val atomicInteger = new AtomicInteger(1)

  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    val start = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>
      val requestTime = System.currentTimeMillis - start

      println(s"${Thread.currentThread.getName} - ${atomicInteger
        .getAndIncrement()}: ${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned ${result.header.status}")

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}

class AppLoader extends ApplicationLoader {
  def load(playContext: PlayContext) =
    new AppComponents(playContext).application
}
