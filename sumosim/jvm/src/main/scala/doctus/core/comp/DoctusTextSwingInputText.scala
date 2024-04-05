package doctus.core.comp

import java.awt.event.{ActionEvent, ActionListener, KeyEvent, KeyListener}
import javax.swing.JTextField

case class DoctusTextSwingInputText(
    inputText: JTextField
) extends DoctusInputText {

  private var optionF: Option[() => Unit] = None

  override var text: String = ""

  override def onTextChanged(f: () => Unit): Unit = optionF = Some(f)

  inputText.addKeyListener(new KeyListener {
    override def keyTyped(keyEvent: KeyEvent): Unit = ()

    override def keyPressed(keyEvent: KeyEvent): Unit = ()

    override def keyReleased(keyEvent: KeyEvent): Unit = {
      text = inputText.getText
      optionF.foreach(f => f())
    }
  })
  inputText.addActionListener((action: ActionEvent) => {})

}
