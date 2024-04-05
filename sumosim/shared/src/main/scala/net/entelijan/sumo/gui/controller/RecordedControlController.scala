package net.entelijan.sumo.gui.controller

import doctus.core.DoctusActivatable
import doctus.core.comp.{
  DoctusCard,
  DoctusInputText,
  DoctusSelect,
  DoctusText,
  SelectItemDescription
}
import net.entelijan.sumo.reinforcement.db.{DatabaseClient, SimulationOverview}

case class RecordedControlController(
    card: DoctusCard,
    dbClient: DatabaseClient,
    list: DoctusSelect[SimulationOverview],
    homeButton: DoctusActivatable,
    runButton: DoctusActivatable,
    reloadButton: DoctusActivatable,
    infoText: DoctusText,
    filterText: DoctusInputText,
    recordedController: RecordedController
) {

  case class SearchItem(
      simulationOverview: SimulationOverview,
      searchString: String
  )
  var searchItems = Seq.empty[SearchItem]

  private def extract(so: SimulationOverview, index: Int): String = {
    index match {
      case 0 => so.startedAt
      case 1 => so.simulationName
      case 2 => so.robot1Name
      case 3 => so.robot2Name
    }
  }

  filterText.onTextChanged(() => {
    setItems()
  })

  homeButton.onActivated { () =>
    card.show(Constants.CARD_HOME)
  }

  reloadButton.onDeactivated { () =>
    loadAndSetItems()
  }

  runButton.onDeactivated { () =>
    try {
      if (list.selectedItem.isEmpty) {
        infoText.text = "No simulation selected"
      } else {
        infoText.text = ""
        list.selectedItem.map { selected =>
          val detail = dbClient.detail(selected.id)
          card.show(Constants.CARD_RECORDED)
          recordedController.init(detail)
        }
      }
    } catch {
      case e: Throwable =>
        infoText.text = s"ERROR: ${e.getMessage}"
        throw e
    }
  }

  def adaptSearchText(in: String): String = in.toLowerCase().trim()

  def createSearchString(simOverview: SimulationOverview): String = {

    adaptSearchText(simOverview.startedAt) +
      adaptSearchText(simOverview.simulationName) +
      adaptSearchText(simOverview.robot1Name) +
      adaptSearchText(simOverview.robot2Name)
  }

  def matches(searchString: String, filterTextString: String): Boolean =
    searchString.indexOf(filterTextString) >= 0

  def setItems(): Unit = {
    val itemDesc = SelectItemDescription(
      List(20, 20, 20, 20),
      extract
    )
    val filterTextString = adaptSearchText(filterText.text)
    val filterTextStrings = filterTextString.split("\\s")
    println(s"## filtering by $filterTextString")
    val filteredItems = searchItems.filter(si =>
      filterTextStrings.forall(matches(si.searchString, _))
    )
    println(s"## found ${filteredItems.size} items")
    val filteredOverviews = filteredItems.map { si => si.simulationOverview }
    list.setItems(filteredOverviews, itemDesc)
  }

  def loadAndSetItems(): Unit = {
    val simOverviews = dbClient.overviews.sortBy { o => o.startedAt }.reverse
    searchItems = simOverviews.map { so =>
      SearchItem(simulationOverview = so, searchString = createSearchString(so))
    }
    setItems()
  }

  loadAndSetItems()
}
