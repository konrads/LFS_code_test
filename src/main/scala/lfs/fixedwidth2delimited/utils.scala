package lfs.fixedwidth2delimited

object utils {
  def splitPerOffsets(s: String, offsets: Seq[Int]): Vector[String] = {
    val (split, _) = offsets.foldLeft((Vector.empty[String], s)) {
      case ((soFar, s2), offset) =>
        val (head, tail) = s2.splitAt(offset)
        (soFar :+ head, tail)
    }
    split
  }
}
