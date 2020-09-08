package lfs.fixedwidth2delimited

import java.io.FileOutputStream

import com.typesafe.scalalogging.Logger
import lfs.fixedwidth2delimited.model.{Spec, SpecRaw}
import lfs.fixedwidth2delimited.utils.splitPerOffsets
import org.rogach.scallop.ScallopConf
import play.api.libs.json._

import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

/**
 * Assumptions:
 * Offsets - used upon String's resulting from file load via FixedWidthEncoding. Ie. not at byte, but char level
 */

object FixedWidth2Delimited  extends App {
  val log = Logger("FixedWidth2Delimited")

  class CliConf extends ScallopConf(args) {
    val delimiter    = opt[String](default = Some(","))
    val specFilename = opt[String]()
    val inFilename   = trailArg[String]()
    val outFilename  = trailArg[String]()
    verify()
  }
  val cliConf = new CliConf()

  run(cliConf.specFilename.toOption, cliConf.inFilename(), cliConf.outFilename(), cliConf.delimiter()) match {
    case Success(_)   => log.info("FixedWidth2Delimited succeeded!")
    case Failure(exc) => log.error("Failed FixedWidth2Delimited", exc)
  }

  private [fixedwidth2delimited] def run(specFilename: Option[String], inFilename: String, outFilename: String, delimiter: String): Try[Int] = {
    for {
      specContents <- Using {
        specFilename.map(Source.fromFile).getOrElse(Source.fromResource("spec.json"))
      } { source => source.mkString("") }
      specRaw <- SpecRaw.asModel(specContents) match {
        case JsSuccess(model, _) => Success(model)
        case JsError(errs) => Failure(new Exception(errs.map(_._2).mkString(", ")))
      }
      spec <- Try(new Spec(specRaw))
      inFileContents <- Using(Source.fromFile(inFilename, spec.FixedWidthEncoding)) { source => source.mkString }
      inRows <- {
        val rowLength = spec.Offsets.sum
        // validate overall file length in a multiple of row length
        if (inFileContents.length % rowLength != 0)
          Failure(new Exception(s"Invalid input file contents size: ${inFileContents.length}, expected multiples of $rowLength"))
        else {
          val encodedRows = inFileContents.sliding(rowLength, rowLength).map(slice => splitPerOffsets(slice, spec.Offsets))
          val allRows = if (spec.IncludeHeader)
            Iterator(spec.ColumnNames.toVector) ++ encodedRows
          else
          encodedRows
          Success(allRows)
        }
      }
    } yield {
      val outContents = inRows.map(_.mkString(delimiter)).mkString("\n").getBytes(spec.DelimitedEncoding)
      val outStream = new FileOutputStream(outFilename)
      try {
        outStream.write(outContents)
        outContents.length
      } finally
        outStream.close()
    }
  }
}
