package lightHouseSimulator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Example {

    public static void main(String[] args) throws IOException {
        // instanceiate the simulator
        LightHouseSimulator sim = new LightHouseSimulator();

        // set mode to mapping(down sampling the image data)
        sim.setMappingMode();

        // make all pixels render, pretending the gaps where not there
        sim.setGapsRatio(1, 0);

        // loading an image from file
        BufferedImage imgData = ImageIO.read(new File(Example.class.getResource("test.png").getFile()));

        // display this data on the lighthouse
        sim.setData(imgData);
    }
}
