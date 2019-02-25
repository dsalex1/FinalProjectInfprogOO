package lightHouseSimulator;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Example {

    public static void main(String[] args) throws IOException {
        LightHouseSimulator m = new LightHouseSimulator();
        m.setMappingMode();
        m.setGapsRatio(1, 0);
        m.setData(ImageIO.read(new File(Example.class.getResource("/lightHouseSimulator/test.png").getFile())));
    }
}
