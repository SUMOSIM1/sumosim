package doctus.core.util

import java.awt.Toolkit

class DoctusUtilJvm extends DoctusUtil {

  override def screenResolution: Int = {
    Toolkit.getDefaultToolkit.getScreenResolution
  }
}
