package services

import java.io.File
import scala.io.Source

object DotEnvLoader {
    def loadDotEnv(path: String = ".env"): Map[String, String] = {
        val file = new File(path)
        if (!file.exists()) {
            Map.empty
        } else {
            val lines = Source.fromFile(file).getLines()
            val envLines = lines.filterNot { line =>
                line.trim.isEmpty || line.startsWith("#")
            }

            envLines.flatMap { line =>
                val idx = line.indexOf('=')
                if (idx > 0) {
                    val key = line.substring(0, idx).trim
                    val value = line.substring(idx + 1).trim
                    Some(key -> value)
                } else {
                    None
                }
            }.toMap
        }
    }
}