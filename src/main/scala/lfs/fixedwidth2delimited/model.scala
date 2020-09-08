package lfs.fixedwidth2delimited

import play.api.libs.json.{JsResult, Json, Reads}

/** Note: conscious decision to keep the names same as json labels. Such approach proved easier to 'grep' for labels. */
object model {
  case class SpecRaw(ColumnNames: Seq[String], Offsets: Seq[String], FixedWidthEncoding: String, IncludeHeader: String, DelimitedEncoding: String)

  object SpecRaw {
    implicit val aReads: Reads[SpecRaw] = Json.reads[SpecRaw]

    def asModel(jsonStr: String): JsResult[SpecRaw] = {
      val parsedJsValue = Json.parse(jsonStr)
      Json.fromJson[SpecRaw](parsedJsValue)
    }
  }

  class Spec(spec: SpecRaw) {
    assert (spec.ColumnNames.size == spec.Offsets.size, s"ColumnNames ${spec.ColumnNames.mkString(", ")} don't match Offsets: ${spec.Offsets.mkString(", ")}")
    val ColumnNames = spec.ColumnNames
    val Offsets = spec.Offsets.map(_.toInt)
    val FixedWidthEncoding = spec.FixedWidthEncoding
    val IncludeHeader = spec.IncludeHeader.toLowerCase == "true"
    val DelimitedEncoding = spec.DelimitedEncoding

    val columnsWithOffsets = ColumnNames.zip(Offsets)
  }
}
