package marker

import com.intellij.openapi.editor.Editor

/**
  * Created by runed on 11/27/2016.
  */
trait MarkerCalculatorStrategy {
  def calculateMarkers(editor: Editor, searchText: String): List[Marker]
}
