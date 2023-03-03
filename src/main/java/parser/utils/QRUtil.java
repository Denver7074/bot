package parser.utils;

import lombok.experimental.UtilityClass;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

@UtilityClass
public class QRUtil {
    public static byte[] generateQR(String href){
        return QRCode.from(href)
                        .withSize(300, 300)
                        .to(ImageType.PNG)
                        .stream()
                        .toByteArray();
    }
}
