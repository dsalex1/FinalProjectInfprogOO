package lightHouseSimulator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

enum Mapping {
	DIRECT, DOWN_SAMPLING_WITH_GAPS
}

@SuppressWarnings("serial")
public class LightHouseSimulator extends Canvas {
	private BufferedImage BackgroundImage = null;
	private BufferedImage redOverlay = null;
	private BufferedImage greenOverlay = null;
	private BufferedImage blueOverlay = null;
	private Mapping mode = Mapping.DOWN_SAMPLING_WITH_GAPS;
	private JFrame frame = null;
	private int[] directData = new int[14 * 28 * 3];
	private int windowHeight = 6;
	private int gapHeight = 5;

	public LightHouseSimulator() {
		super();
		try {
			System.out.println(new File(getClass().getResource("/lightHouseSimulator/background.png").getFile()));
			BackgroundImage = ImageIO
					.read(new File(getClass().getResource("/lightHouseSimulator/background.png").getFile()));
			redOverlay = ImageIO.read(new File(getClass().getResource("/lightHouseSimulator/red.png").getFile()));
			greenOverlay = ImageIO.read(new File(getClass().getResource("/lightHouseSimulator/green.png").getFile()));
			blueOverlay = ImageIO.read(new File(getClass().getResource("/lightHouseSimulator/blue.png").getFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame = new JFrame();
		frame.add(this);
		frame.setSize(BackgroundImage.getWidth(this), BackgroundImage.getHeight(this));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void paint(Graphics g) {

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

	public void blendAt(int destX, int destY, int rFactor, int gFactor, int bFactor, BufferedImage img) {
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

	public void setData(BufferedImage data) {
		if (mode == Mapping.DOWN_SAMPLING_WITH_GAPS) {
			if (Math.abs(data.getWidth() / data.getHeight() - 10.0 / 11) < 0.05)
				throw new IllegalArgumentException(
						"Data must have an aspect ratio of 10:11 for down sampleing mapping.");
			setMappedData(data);
		}
		if (mode == Mapping.DIRECT) {
			if (data.getWidth() != 28 || data.getHeight() != 14)
				throw new IllegalArgumentException("Data must have dimensions 28x14 for direct mapping.");
			setDirectData(data);
		}
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

	public void setMappingMode() {
		mode = Mapping.DOWN_SAMPLING_WITH_GAPS;
	}

	public void setDirectMode() {
		mode = Mapping.DOWN_SAMPLING_WITH_GAPS;
	}

}