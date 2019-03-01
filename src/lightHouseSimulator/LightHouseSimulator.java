package lightHouseSimulator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

enum Mapping {
	DIRECT, MAPPED
}

@SuppressWarnings("serial")
/**
 * This class simulates how an image would be rendered on the LightHouse of the
 * CAU.
 * https://www.kunstgeschichte.uni-kiel.de/de/kunstcampus-kiel/verwaltungshochhaus-projekt-lighthouse
 */
public class LightHouseSimulator extends JPanel {
	private BufferedImage BackgroundImage = null;
	private BufferedImage redOverlay = null;
	private BufferedImage greenOverlay = null;
	private BufferedImage blueOverlay = null;
	private Mapping mode = Mapping.MAPPED;
	private JFrame frame = null;
	private int[] directData = new int[14 * 28 * 3];
	private int windowHeight = 6;
	private int gapHeight = 5;

	/**
	 * Opens a new window displaying a completely turned off LightHouse. The Lights
	 * can then be turned on by providing ImageData via {@code setData}
	 */
	public LightHouseSimulator() {
		super();
		try {
			BackgroundImage = ImageIO.read(new File(getClass().getResource("background.png").getFile()));
			redOverlay = ImageIO.read(new File(getClass().getResource("red.png").getFile()));
			greenOverlay = ImageIO.read(new File(getClass().getResource("green.png").getFile()));
			blueOverlay = ImageIO.read(new File(getClass().getResource("blue.png").getFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame = new JFrame();
		frame.setSize(BackgroundImage.getWidth(this), BackgroundImage.getHeight(this));
		frame.setVisible(true);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public int[] getDirectData() {
		return directData;
	}
	

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		BufferedImage currentImg = new BufferedImage(BackgroundImage.getWidth(), BackgroundImage.getHeight(),
				BufferedImage.TYPE_3BYTE_BGR);

		for (int x = 0; x < BackgroundImage.getWidth(); x++) {
			for (int y = 0; y < BackgroundImage.getHeight(); y++) {
				currentImg.setRGB(x, y, BackgroundImage.getRGB(x, y));
			}
		}
		for (int x = 0; x < 28; x++)
			for (int y = 0; y < 14; y++) {
				blendAt((int) (290 + 18.7037 * x), (int) (84 + 42 * y), directData[(x + y * 28) * 3 + 0],
						directData[(x + y * 28) * 3 + 1], directData[(x + y * 28) * 3 + 2], currentImg);
			}

		g.drawImage(currentImg, 0, 0, this);
	}

	private void blendAt(int destX, int destY, int rFactor, int gFactor, int bFactor, BufferedImage img) {
		for (int x = 0; x < redOverlay.getWidth(); x++)
			for (int y = 0; y < redOverlay.getHeight(); y++) {
				Color backPixel = new Color(img.getRGB(x + destX, y + destY));
				Color redPixel = new Color(redOverlay.getRGB(x, y));
				Color greenPixel = new Color(greenOverlay.getRGB(x, y));
				Color bluePixel = new Color(blueOverlay.getRGB(x, y));

				int r = (255 * backPixel.getRed() + rFactor * redPixel.getRed() + gFactor * greenPixel.getRed()
						+ bFactor * bluePixel.getRed()) / 255;
				int g = (255 * backPixel.getGreen() + rFactor * redPixel.getGreen() + gFactor * greenPixel.getGreen()
						+ bFactor * bluePixel.getGreen()) / 255;
				int b = (255 * backPixel.getBlue() + rFactor * redPixel.getBlue() + gFactor * greenPixel.getBlue()
						+ bFactor * bluePixel.getBlue()) / 255;

				if (r > 255)
					r = 255;
				if (g > 255)
					g = 255;
				if (b > 255)
					b = 255;
				img.setRGB(destX + x, destY + y, r << 16 | g << 8 | b);
			}
	}

	/**
	 * Sets the image data to display on the LightHouse.
	 * <p>
	 * In {@code MAPPED} mode (the default) the Image may have an arbitrary size and
	 * is resampled to 28x14. furthermore the image is masked by the location of the
	 * windows, so some pixels are not shown, according to the emtpy rows on the
	 * LightHouse. How much of horizontal space should be obmitted can be set by
	 * {@code setGapsRatio}.
	 * <p>
	 * In {@code DIRECT} mode, the Image must have a size of 28x14, otherwise an
	 * {@code IllegalArgumentException} is thrown. The pixels of the image provied
	 * directly correspond to the windows of the LightHouse.
	 * 
	 * @param data the Image to be displayed
	 */
	public void setData(BufferedImage data) {
		if (mode == Mapping.MAPPED) {
			setMappedData(data);
		}
		if (mode == Mapping.DIRECT) {
			if (data.getWidth() != 28 || data.getHeight() != 14)
				throw new IllegalArgumentException("Data must have dimensions 28x14 for direct mapping.");
			setDirectData(data);
		}
		this.repaint();
	}

	private void setDirectData(BufferedImage data) {
		for (int x = 0; x < 28; x++)
			for (int y = 0; y < 14; y++) {
				Color c = new Color(data.getRGB(x, y));
				directData[(y * 28 + x) * 3 + 0] = c.getRed();
				directData[(y * 28 + x) * 3 + 1] = c.getGreen();
				directData[(y * 28 + x) * 3 + 2] = c.getBlue();
			}

	}

	/**
	 * This sets the ratio at which pixels are obmitted according to the horizontal
	 * gaps between windows. Invocing {@code setGapsRatio(1, 0)} makes all pixels
	 * visible, obmitting nothing.
	 * 
	 * @param windowHeight the proposed height of a window in ralation to
	 *                     {@code gapHeight}
	 * @param gapHeight    the proposed height of the gap between windows in
	 *                     ralation to {@code windowHeight}
	 */
	public void setGapsRatio(int windowHeight, int gapHeight) {
		this.windowHeight = windowHeight;
		this.gapHeight = gapHeight;
	}

	private void setMappedData(BufferedImage data) {
		BufferedImage scaledData = resize(data, 28, 14 * (windowHeight + gapHeight) - gapHeight);
		for (int x = 0; x < 28; x++)
			for (int y = 0; y < 14; y++) {
				int rSum = 0;
				int gSum = 0;
				int bSum = 0;
				for (int i = 0; i < windowHeight; i++) {
					rSum += new Color(scaledData.getRGB(x, y * (windowHeight + gapHeight) + i)).getRed();
					gSum += new Color(scaledData.getRGB(x, y * (windowHeight + gapHeight) + i)).getGreen();
					bSum += new Color(scaledData.getRGB(x, y * (windowHeight + gapHeight) + i)).getBlue();
				}
				rSum /= windowHeight;
				gSum /= windowHeight;
				bSum /= windowHeight;
				directData[(y * 28 + x) * 3 + 0] = rSum;
				directData[(y * 28 + x) * 3 + 1] = gSum;
				directData[(y * 28 + x) * 3 + 2] = bSum;
			}

	}

	private static BufferedImage resize(BufferedImage img, int width, int height) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}

	/**
	 * This sets the mode of displaying Images to {@code MAPPED} (the default). In
	 * {@code MAPPED} mode the Image may have an arbitrary size and is resampled to
	 * 28x14. furthermore the image is masked by the location of the windows, so
	 * some pixels are not shown, according to the emtpy rows on the LightHouse. How
	 * much of horizontal space should be obmitted can be set by
	 * {@code setGapsRatio}.
	 */
	public void setMappingMode() {
		mode = Mapping.MAPPED;
	}

	/**
	 * This sets the mode of displaying Images to {@code DIRECT}. In {@code DIRECT}
	 * mode, the Image must have a size of 28x14, otherwise an
	 * {@code IllegalArgumentException} is thrown. The pixels of the image provied
	 * directly correspond to the windows of the LightHouse.
	 */
	public void setDirectMode() {
		mode = Mapping.DIRECT;
	}

}