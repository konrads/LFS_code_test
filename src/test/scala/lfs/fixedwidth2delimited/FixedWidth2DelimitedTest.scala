package lfs.fixedwidth2delimited

import java.io.FileOutputStream

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class FixedWidth2DelimitedTest extends AnyFlatSpec with Matchers {
  def writeSampleInput(filename: String, lines: Int=3, encoding: String="utf-8", sample: Option[String]=None) = {
    val sampleContent = sample.getOrElse(("MichÃ¨le HuÃ\u00A0?!" * 100).take(lines*15))
    val outStream = new FileOutputStream(filename)
    try {
      outStream.write(sampleContent.getBytes(encoding))
    } finally
      outStream.close()
  }

  def writeSampleSpec(filename: String, FixedWidthEncoding: String, DelimitedEncoding: String, IncludeHeader: String) = {
    val specJson = s"""
      |{
      |  "ColumnNames": [
      |    "f1",
      |    "f2",
      |    "f3"
      |  ],
      |  "Offsets": [
      |    "3",
      |    "5",
      |    "7"
      |  ],
      |  "FixedWidthEncoding": "$FixedWidthEncoding",
      |  "IncludeHeader": "$IncludeHeader",
      |  "DelimitedEncoding": "$DelimitedEncoding"
      |}
      |""".stripMargin
    val outStream = new FileOutputStream(filename)
    try {
      outStream.write(specJson.getBytes())
    } finally
      outStream.close()
  }

  def validate(desc: String, FixedWidthEncoding: String, DelimitedEncoding: String, lines: Int, IncludeHeader: String, expectedOutputSize: Int) = {
    val tempRoot = getClass.getResource("/").getFile
    val qfSpecFilename = s"${tempRoot}spec_$desc.json"
    val qfInFilename = s"${tempRoot}in_$desc"
    val qfOutFilename = s"${tempRoot}out_$desc.csv"
    writeSampleSpec(qfSpecFilename, FixedWidthEncoding, DelimitedEncoding, IncludeHeader)
    writeSampleInput(qfInFilename, lines, FixedWidthEncoding)
    FixedWidth2Delimited.run(Some(qfSpecFilename), qfInFilename, qfOutFilename, ",") shouldBe Try(expectedOutputSize)
  }

  "FixedWidth2Delimited" should "work with empty file" in {
    validate("empty-file", "utf-8", "utf-8", 0, "False", 0)
  }

  it should "work with header only" in {
    validate("header-only", "utf-8", "utf-8", 0, "True", 8)
  }

  it should "work with windows-1252 input/output" in {
    validate("windows-1252-in-out", "windows-1252", "windows-1252", 1, "False", 15 + 2/*delimiters*/)
  }

  it should "work with utf-8 input/output" in {
    validate("utf-8-in-out", "utf-8", "utf-8", 1, "False", 19 + 2 /*delimiters*/)
  }

  it should "work with full run, with headers, different encodings" in {
    validate("full-run", "windows-1252", "utf-8", 3, "True", 8 + (19 + 2/*delimiters*/)*3 + 3/*CRLFs*/)
  }
  it should "work supplied spec.json" in {
    val tempRoot = getClass.getResource("/").getFile
    val qfSpecFilename = getClass.getResource("/spec.json").getFile
    val qfInFilename = s"${tempRoot}in_org"
    val qfOutFilename = s"${tempRoot}out_org.csv"
    writeSampleInput(qfInFilename, encoding="windows-1252", sample=Some("1234567"*7*2*5))
    FixedWidth2Delimited.run(Some(qfSpecFilename), qfInFilename, qfOutFilename, ",") shouldBe Try(30 /*header*/ + 107/*98 row length + 9 delimiters*/ *5 + 5 /*CRLFs*/)
  }
}
