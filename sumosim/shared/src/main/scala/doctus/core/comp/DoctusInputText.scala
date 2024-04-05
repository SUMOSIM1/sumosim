package doctus.core.comp

trait DoctusInputText extends DoctusText {

  def onTextChanged(f: () => Unit): Unit

}
