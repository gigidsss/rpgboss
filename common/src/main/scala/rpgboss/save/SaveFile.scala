package rpgboss.save

import rpgboss.model.Project
import rpgboss.lib.JsonUtils
import java.io.File
import rpgboss.model.MapLoc
import rpgboss.lib.Utils
import rpgboss.model.resource.RpgMap
import rpgboss.player.HasScriptConstants

case class SavedEventState(mapName: String, eventId: Int, eventState: Int)

case class SaveFile(
    intMap: Map[String, Int] = Map(),
    intArrayMap: Map[String, Array[Int]] = Map(),
    stringArrayMap: Map[String, Array[String]] = Map(),
    mapLocMap: Map[String, MapLoc] = Map(),
    eventStates: Array[SavedEventState] = Array())

case class SaveInfo(isDefined: Boolean, mapTitle: String = "")

object SaveFile extends HasScriptConstants {
  def file(project: Project, slot: Int) =
    new File(project.dir, Utils.generateFilename("save", slot, "json"))

  def read(project: Project, slot: Int): Option[SaveFile] = {
    JsonUtils.readModelFromJson[SaveFile](
        file(project, slot: Int))
  }

  def readInfo(project: Project, slot: Int): SaveInfo = {
    for (saveFile <- read(project, slot);
         mapLoc <- saveFile.mapLocMap.get(PLAYER_LOC)) {
       val map = RpgMap.readFromDisk(project, mapLoc.map)
       return SaveInfo(true, map.metadata.title)
    }

    return SaveInfo(false)
  }

  def write(saveFile: SaveFile, project: Project, slot: Int) =
    JsonUtils.writeModelToJson(file(project, slot), saveFile)
}