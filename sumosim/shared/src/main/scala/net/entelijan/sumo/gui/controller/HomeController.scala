package net.entelijan.sumo.gui.controller

import doctus.core.DoctusActivatable
import doctus.core.comp.DoctusCard
import net.entelijan.sumo.reinforcement.db.DatabaseClient

case class HomeController(
    card: DoctusCard,
    dbClient: DatabaseClient,
    codedButton: DoctusActivatable,
    recordedButton: DoctusActivatable,
    latestRecordedButton: DoctusActivatable,
    recordedController: RecordedController
) {

  codedButton.onDeactivated { () =>
    card.show(Constants.CARD_CODED)
  }

  recordedButton.onDeactivated { () =>
    card.show(Constants.CARD_RECORDED_CONTROL)
  }

  latestRecordedButton.onDeactivated { () =>
    val maxOverview =
      dbClient.overviews.max((o1, o2) => o1.startedAt.compareTo(o2.startedAt))
    println(s" max overview $maxOverview")
    val id: String = maxOverview.id
    val detail = dbClient.detail(id)
    println(s"loaded ${detail.startedAt} ${detail.simulationName}")
    card.show(Constants.CARD_RECORDED)
    recordedController.init(detail)
  }
}
