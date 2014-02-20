package scala.mars.plugin

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent
import scala.tools.nsc.ast.Printers
import java.io.{StringWriter, PrintWriter, File}

object MarsPlugin {
  val expandNameOpt = "expandName"
}

class MarsPlugin(val global: Global) extends Plugin {
  import MarsPlugin._
  import global._

  val name = "marsplugin"
  val description = "runtime macro expansion plugin"

  var expandName = "test"

  object afterTyper extends PrintPhaseComponent("typer", "patmat")
  val components = List[PluginComponent](afterTyper)

  override def processOptions(options: List[String], error: String => Unit) {
    for (option <- options) {
      if (option.startsWith(expandNameOpt)) {
        expandName = option.substring(expandNameOpt.length)
      } else{
          error("Option not understood: "+option)
      }
    }
  }

  //Phase should be inserted between prevPhase and nextPhase
  //but it possible that not right after prevPhase or not right before nextPhase
  class PrintPhaseComponent(val prevPhase: String, val nextPhase: String) extends PluginComponent {
    val global: MarsPlugin.this.global.type = MarsPlugin.this.global

    override val runsAfter = List[String](prevPhase)
    override val runsBefore = List[String](nextPhase)
    
    val phaseName = "runtimeMacrosExp"
    def newPhase(_prev: Phase): StdPhase = new PrintPhase(_prev)

    class PrintPhase(prev: Phase) extends StdPhase(prev) {
      override def name = MarsPlugin.this.name

      def apply(unit: CompilationUnit) {
        try {
            //regenerate only scala files
            val fileName = unit.source.file.name
            if (fileName.endsWith(".scala")) {
              val tree = unit.body
              println(showCode(tree))
            } else
              println("File " + fileName + " is not processed")
        } catch {
          case e: Exception =>
            e.printStackTrace()
            throw e
        }
      }
    }
  }
}

