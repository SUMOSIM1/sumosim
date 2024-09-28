package net.entelijan.sumo

import java.awt.*
import javax.swing.*
import javax.swing.border.Border
import doctus.core.{DoctusColor, DoctusImage}
import doctus.core.comp.{DoctusCard, DoctusText, DoctusTextSwingInputText}
import doctus.jvm.DoctusSchedulerJvm
import doctus.swing.*
import doctus.core.util.*
import gui.example.*
import gui.renderer.*
import reinforcement.db.*
import db.*
import doctus.core.color.*
import gui.controller.*

object JvmGui {

  def open(): Unit = {
    System.setProperty("sun.java2d.opengl", "true")
    try {
      val name = UIManager.getCrossPlatformLookAndFeelClassName
      // val name = UIManager.getSystemLookAndFeelClassName
      // val name = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"
      // val name = "com.sun.java.swing.plaf.motif.MotifLookAndFeel"
      UIManager.setLookAndFeel(name)
    } catch {
      case _: Exception =>
        val cross = UIManager.getCrossPlatformLookAndFeelClassName
        UIManager.setLookAndFeel(cross)
    }

    val homeComps = Panels.home
    val recordedControlComps = Panels.recordedControl
    val recordedComps = Panels.recorded
    val codedComps = Panels.coded

    val cardLayout = new CardLayout()
    val cardPanel = new JPanel(cardLayout)
    cardPanel.add(homeComps.content, Constants.CARD_HOME)
    cardPanel.add(codedComps.content, Constants.CARD_CODED)
    cardPanel.add(
      recordedControlComps.content,
      Constants.CARD_RECORDED_CONTROL
    )
    cardPanel.add(recordedComps.content, Constants.CARD_RECORDED)

    val card: DoctusCard = DoctusSwingCard(cardPanel, cardLayout)

    // TODO make the database client lazy, that you can run coded controllers
    // without database
    val databaseClient = MongoJvmUtil.localClient
    val dbClient = MongoJvmDatabaseClient(databaseClient)

    val util = DoctusUtilJvm()

    CodedController(
      card = card,
      canvas = DoctusCanvasSwing(codedComps.swingCanvas),
      homeButton = DoctusActivatableSwing(codedComps.home),
      startButton = DoctusActivatableSwing(codedComps.start),
      controllers1 = DoctusSelectSwingComboBox[String](codedComps.controller1),
      controllers2 = DoctusSelectSwingComboBox[String](codedComps.controller2),
      scheduler = DoctusSchedulerJvm,
      // TODO make the layout configurable
      // Some(LayoutFactory.robos()),
      // layout = Some(LayoutFactory.sumos()),
      None,
      util
    )

    val rc = RecordedController(
      card = card,
      canvas = DoctusTemplateCanvasSwing(recordedComps.swingCanvas),
      runForwardButton = DoctusActivatableSwing(recordedComps.forward),
      runFastForwardButton = DoctusActivatableSwing(recordedComps.fastForward),
      pauseButton = DoctusActivatableSwing(recordedComps.pause),
      runBackwardButton = DoctusActivatableSwing(recordedComps.backward),
      runFastBackwardButton =
        DoctusActivatableSwing(recordedComps.fastBackward),
      homeButton = DoctusActivatableSwing(recordedComps.home),
      backButton = DoctusActivatableSwing(recordedComps.back),
      scheduler = DoctusSchedulerJvm,
      // TODO make the layout configurable
      // Some(LayoutFactory.robos()),
      // Some(LayoutFactory.sumos()),
      None,
      util = DoctusUtilJvm()
    )

    HomeController(
      card = card,
      dbClient = dbClient,
      codedButton = DoctusActivatableSwing(homeComps.coded),
      recordedButton = DoctusActivatableSwing(homeComps.recorded),
      latestRecordedButton = DoctusActivatableSwing(homeComps.latestRecorded),
      recordedController = rc
    )

    RecordedControlController(
      card = card,
      dbClient = MongoJvmDatabaseClient(databaseClient),
      list = new DoctusSelectSwingList[SimulationOverview](
        recordedControlComps.simulationList
      ),
      homeButton = DoctusActivatableSwing(recordedControlComps.homeButton),
      runButton = DoctusActivatableSwing(recordedControlComps.startButton),
      reloadButton = DoctusActivatableSwing(recordedControlComps.reloadButton),
      infoText = DoctusTextSwingLabel(recordedControlComps.infoText),
      filterText = DoctusTextSwingInputText(recordedControlComps.filterText),
      recordedController = rc
    )

    def scaleInt(value: Int, factor: Double): Int =
      (value.toDouble * factor).toInt

    val frame = new JFrame()
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    frame.setTitle("Canvas Showcase")
    // TODO make a sumosim logo
    val url = getClass.getClassLoader.getResource("logo.png")
    if (url != null) frame.setIconImage(new ImageIcon(url).getImage)

    val screenDim = Toolkit.getDefaultToolkit.getScreenSize
    frame.setContentPane(cardPanel)
    frame.setSize(
      new Dimension(
        scaleInt(screenDim.width, 0.5),
        scaleInt(screenDim.height, 0.85)
      )
    )
    frame.setLocationByPlatform(true)
    frame.setVisible(true)

  }

}

object LayoutFactory {

  def sumos(): SumoLayout = new SumoLayout {

    override def robot1: ImageProvider = ImagesProvider.sumoBlue

    override def robot2: ImageProvider = ImagesProvider.sumoViolet

    override def background: DoctusImage = ImagesProvider.background

    override def util: DoctusUtil = new DoctusUtilJvm()
  }

  def robos(): SumoLayout = new SumoLayout {

    override def robot1: ImageProvider = ImagesProvider.roboRed

    override def robot2: ImageProvider = ImagesProvider.roboBlack

    override def background: DoctusImage = ImagesProvider.background

    override def util: DoctusUtil = new DoctusUtilJvm()

  }

}

object ImagesProvider {

  def sumoBlue: ImageProvider = new SumoBlue {
    def doctusImage(index: Int): DoctusImage = DoctusImageSwing(
      "robot-2d/sumo-blue/img%d.png" format index
    )

    override def mainColor: DoctusColor = DoctusColorBlue
  }
  def sumoViolet: ImageProvider = new SumoViolet {
    def doctusImage(index: Int): DoctusImage = DoctusImageSwing(
      "robot-2d/sumo-violet/img%d.png" format index
    )

    override def mainColor: DoctusColor = DoctusColorMagenta
  }
  def roboRed: ImageProvider = new RoboRed {
    def doctusImage(index: Int): DoctusImage = DoctusImageSwing(
      "robot-2d/robo2/img%d.png" format index
    )

    override def mainColor: DoctusColor = DoctusColorRed
  }
  def roboBlack: ImageProvider = new RoboBlack {
    def doctusImage(index: Int): DoctusImage = DoctusImageSwing(
      "robot-2d/robo1/img%d.png" format index
    )

    override def mainColor: DoctusColor = DoctusColorBlack
  }
  val background: DoctusImage = DoctusImageSwing("robot-2d/bg/pad.png")

}

object Panels {

  private val backSymbolString = "\u21A9"
  private val startSymbolString = "\u25B6"
  private val homeSymbolString = "\u2302"
  private val filterSymbolString = "Y"
  private val sortSymbolString = "\u21C5"
  private val reloadSymbolString = "\u21BA"
  private val forwardSymbolString = ">"
  private val fastForwardSymbolString = ">>"
  private val backwardSymbolString = "<"
  private val fastBackwardSymbolString = "<<"
  private val pauseSymbolString = "||"

  private val util = DoctusUtilJvm()

  private val borderWidth = util.borderWidth.toInt
  private val fontSize = util.fontSize.toInt
  private val font = new Font("Courier", Font.BOLD, fontSize)
  private val bigFont = new Font("Courier", Font.BOLD, (fontSize * 1.4).toInt)

  private def createPanel(
      top: Int = 0,
      left: Int = 0,
      bottom: Int = 0,
      right: Int = 0
  ): JComponent = {
    val _panel = new JPanel()
    _panel.setBorder(createBorder(top, left, bottom, right))
    _panel
  }

  private def createTopPanel(): Container = {
    createPanel(borderWidth, borderWidth, borderWidth, borderWidth)
  }

  private def wrapBorder(
      comp: Component,
      top: Int = 0,
      left: Int = 0,
      bottom: Int = 0,
      right: Int = 0
  ): JComponent = {
    val _panel = createPanel(top, left, bottom, right)
    _panel.setLayout(new BorderLayout())
    _panel.add(comp, BorderLayout.CENTER)
    _panel
  }

  private def createBoxH(
      top: Int = 0,
      left: Int = 0,
      bottom: Int = 0,
      right: Int = 0
  ): Container = {
    val box = createPanel(top, left, bottom, right)
    box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS))
    box
  }

  private def createBoxV(
      top: Int = 0,
      left: Int = 0,
      bottom: Int = 0,
      right: Int = 0
  ): Container = {
    val box = createPanel(top, left, bottom, right)
    box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS))
    box
  }

  private def createComboBox[E](
      font: Font = font,
      width: Int = 60,
      top: Int = 0,
      left: Int = 0,
      bottom: Int = 0,
      right: Int = 0
  ): JComboBox[E] = {
    val combo = new JComboBox[E]()
    combo.setFont(font)
    combo.setPreferredSize(new Dimension(util.adaptLength(width), 0))
    combo.setBorder(createBorder(top, left, bottom, right))
    combo
  }

  private def createButton(text: String, font: Font = font): JButton = {
    val button = new JButton(text)
    button.setFont(font)
    button
  }

  private def createBorder(
      top: Int = 0,
      left: Int = 0,
      bottom: Int = 0,
      right: Int = 0
  ): Border = {
    BorderFactory.createEmptyBorder(top, left, bottom, right)
  }

  private def wrapButton(button: JButton) = {
    val codedButtonCont =
      wrapBorder(button, left = borderWidth, right = borderWidth)
    codedButtonCont.setAlignmentX(0.5f)
    codedButtonCont.setMaximumSize(
      new Dimension(util.adaptLength(60), util.adaptLength(20))
    )
    codedButtonCont
  }

  private def createContainer(
      f: () => Container
  )(components: Component*): Container = {
    val container = f()
    for comp <- components do {
      container.add(comp)
    }
    container
  }

  private def createHboxContainer(components: Component*): Container = {
    createContainer(() => createBoxH(bottom = borderWidth))(components*)
  }

  case class HomeComponents(
      content: Component, // Needed for the DoctusCard
      recorded: JButton,
      latestRecorded: JButton,
      coded: JButton
  )

  def home: HomeComponents = {

    val codedButton = createButton("Coded")
    val codedButtonCont: JComponent = wrapButton(codedButton)

    val recordedButton = createButton("Recorded")
    val recordedButtonCont: JComponent = wrapButton(recordedButton)

    val latestRecordedButton = createButton("Latest Recorded")
    val latestRecordedButtonCont: JComponent = wrapButton(latestRecordedButton)

    val buttonsPanel = createBoxH(top = borderWidth * 5)
    buttonsPanel.add(codedButtonCont)
    buttonsPanel.add(recordedButtonCont)
    buttonsPanel.add(latestRecordedButtonCont)

    val infoPanel = createBoxV(top = borderWidth * 10)

    val title = new JLabel("Sumosim")
    title.setFont(bigFont)
    title.setAlignmentX(0.5f)

    val description = new JLabel("Simulator for sumo robots")
    description.setFont(font)
    description.setAlignmentX(0.5f)

    infoPanel.add(title)
    infoPanel.add(description)

    val content = createTopPanel()

    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS))
    content.add(infoPanel)
    content.add(buttonsPanel)

    HomeComponents(
      content = content,
      recorded = recordedButton,
      latestRecorded = latestRecordedButton,
      coded = codedButton
    )
  }

  case class CodedComponents(
      content: Component, // Needed for the DoctusCard
      controller1: JComboBox[String],
      controller2: JComboBox[String],
      home: JButton,
      start: JButton,
      swingCanvas: DoctusSwingComponent
  )

  def coded: CodedComponents = {

    val home = createButton(homeSymbolString)
    val homeWrapper: JComponent = wrapBorder(home, right = borderWidth)

    val controller1 = createComboBox[String](right = borderWidth)
    val controller2 = createComboBox[String](right = borderWidth)

    val start = createButton(startSymbolString)
    val startWrapper = wrapBorder(start)

    val taskPanel =
      createHboxContainer(homeWrapper, controller1, controller2, startWrapper)

    val doctusComp = DoctusSwingComponentFactory.component

    val content = createTopPanel()
    content.setLayout(new BorderLayout())
    content.add(taskPanel, BorderLayout.NORTH)
    content.add(doctusComp, BorderLayout.CENTER)

    CodedComponents(
      content,
      controller1,
      controller2,
      home,
      start,
      doctusComp
    )
  }

  case class RecordedComponents(
      content: Component, // Needed for the DoctusCard
      swingCanvas: DoctusSwingComponent,
      forward: JButton,
      fastForward: JButton,
      backward: JButton,
      fastBackward: JButton,
      pause: JButton,
      back: JButton,
      home: JButton
  )

  def recorded: RecordedComponents = {

    val forward = createButton(forwardSymbolString)
    val forwardWrapper = wrapBorder(forward, right = borderWidth)
    val fastForward = createButton(fastForwardSymbolString)
    val fastForwardWrapper = wrapBorder(fastForward, right = borderWidth)
    val backward = createButton(backwardSymbolString)
    val backwardWrapper = wrapBorder(backward, right = borderWidth)
    val fastBackward = createButton(fastBackwardSymbolString)
    val fastBackwardWrapper = wrapBorder(fastBackward, right = borderWidth)
    val pause = createButton(pauseSymbolString)
    val pauseWrapper = wrapBorder(pause, right = borderWidth)
    val back = createButton(backSymbolString)
    val backWrapper = wrapBorder(back, right = borderWidth)
    val home = createButton(homeSymbolString)
    val homeWrapper = wrapBorder(home, right = borderWidth)

    val buttonsPanel = createBoxH(bottom = borderWidth)
    buttonsPanel.add(homeWrapper)
    buttonsPanel.add(backWrapper)
    buttonsPanel.add(fastBackwardWrapper)
    buttonsPanel.add(backwardWrapper)
    buttonsPanel.add(pauseWrapper)
    buttonsPanel.add(forwardWrapper)
    buttonsPanel.add(fastForwardWrapper)

    val doctusComp = DoctusSwingComponentFactory.component

    val content = createTopPanel()
    content.setLayout(new BorderLayout())

    content.add(doctusComp, BorderLayout.CENTER)
    content.add(buttonsPanel, BorderLayout.NORTH)
    RecordedComponents(
      content,
      doctusComp,
      forward,
      fastForward,
      backward,
      fastBackward,
      pause,
      back,
      home
    )
  }

  case class RecordedControlComponents(
      content: Component,
      simulationList: JList[String],
      startButton: JButton,
      homeButton: JButton,
      reloadButton: JButton,
      filterText: JTextField,
      sortOrder: JComboBox[String],
      infoText: JLabel
  )

  def recordedControl: RecordedControlComponents = {
    val homeButton = createButton(homeSymbolString)
    val homeWrapper = wrapBorder(homeButton, right = borderWidth)

    val startButton = createButton(startSymbolString)
    val startWrapper = wrapBorder(startButton, left = borderWidth)

    val reloadButton = createButton(reloadSymbolString)
    val reloadWrapper = wrapBorder(reloadButton, left = borderWidth)

    val filterLabel = new JLabel(filterSymbolString)
    filterLabel.setFont(font)
    filterLabel.setBorder(createBorder(right = borderWidth))

    val filterText = new JTextField()
    filterText.setColumns(10)
    filterText.setFont(font)

    val sortLabel = new JLabel(sortSymbolString)
    sortLabel.setFont(font)
    sortLabel.setBorder(
      createBorder(left = borderWidth, right = borderWidth)
    )
    val sortOrder = createComboBox[String]()
    // val model = new DefaultComboBoxModel[String]()
    // model.addElement("             ")
    // sortOrder.setModel(model)
    sortOrder.setFont(font)
    sortOrder.setMaximumSize(new Dimension(2000, 100))

    val taskPanel = new JPanel()
    val taskPanelBorder = createBorder(bottom = borderWidth)
    taskPanel.setBorder(taskPanelBorder)
    taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.LINE_AXIS))
    taskPanel.add(homeWrapper)
    taskPanel.add(filterLabel)
    taskPanel.add(filterText)
    taskPanel.add(sortLabel)
    taskPanel.add(sortOrder)
    taskPanel.add(reloadWrapper)
    taskPanel.add(startWrapper)

    val simList = new JList[String]()
    simList.setFont(font)
    val simScroll = new JScrollPane(simList)

    val centerPanel = new JPanel(new BorderLayout())
    centerPanel.add(simScroll, BorderLayout.CENTER)
    centerPanel.add(taskPanel, BorderLayout.NORTH)

    val title = new JLabel()
    title.setBorder(createBorder(bottom = 3 * borderWidth))
    title.setFont(bigFont)
    title.setText("Replay Simulations")

    val info = new JLabel()
    info.setFont(font)
    info.setText("This is some test info")
    val infoPanel = createBoxV(top = borderWidth)
    infoPanel.add(info)

    val content = createTopPanel()
    content.setLayout(new BorderLayout())

    content.add(centerPanel, BorderLayout.CENTER)
    content.add(title, BorderLayout.NORTH)
    content.add(infoPanel, BorderLayout.SOUTH)

    RecordedControlComponents(
      content,
      simList,
      startButton,
      homeButton,
      reloadButton,
      filterText,
      sortOrder,
      info
    )
  }

}

// TODO Move to doctus
case class DoctusTextSwingLabel(label: JLabel) extends DoctusText {

  override def text: String = label.getText

  override def text_=(txt: String): Unit = label.setText(txt)
}
