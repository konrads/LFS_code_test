package lfs.fixedwidth2delimited

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UilsTest extends AnyFlatSpec with Matchers {
  "splitPerOffsets" should "split accordingly" in {
    utils.splitPerOffsets("abcdefghijklmnopqrstuvwxyz", Seq(2, 3, 5, 7)) shouldBe (Vector("ab", "cde", "fghij", "klmnopq"))
  }
}
