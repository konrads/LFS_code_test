package lfs.fixedwidth2delimited

import lfs.fixedwidth2delimited.model.{Spec, SpecRaw}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ModelTest extends AnyFlatSpec with Matchers {
  val sampleSpec =
    """
      |{
      |  "ColumnNames": [
      |    "f1",
      |    "f2",
      |    "f3",
      |    "f4",
      |    "f5",
      |    "f6",
      |    "f7",
      |    "f8",
      |    "f9",
      |    "f10"
      |  ],
      |  "Offsets": [
      |    "5",
      |    "12",
      |    "3",
      |    "2",
      |    "13",
      |    "7",
      |    "10",
      |    "13",
      |    "20",
      |    "13"
      |  ],
      |  "FixedWidthEncoding": "windows-1252",
      |  "IncludeHeader": "True",
      |  "DelimitedEncoding": "utf-8"
      |}
      |
      |""".stripMargin

  "FixedWidth2Delimited" should "read spec.json" in {
    val specRaw = SpecRaw.asModel(sampleSpec)
    specRaw.isSuccess shouldBe true
    val spec = new Spec(specRaw.get)
    spec.ColumnNames.size shouldBe 10
    spec.Offsets.size shouldBe 10
    spec.columnsWithOffsets.size shouldBe 10
    spec.FixedWidthEncoding shouldBe "windows-1252"
    spec.IncludeHeader shouldBe true
    spec.DelimitedEncoding shouldBe "utf-8"
  }
}
