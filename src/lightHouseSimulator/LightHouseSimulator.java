package lightHouseSimulator;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
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

	private BufferedImage currentImg;
	
	private BufferedImage bufferImg = null;

	private BufferedImage backgroundImage = null;
	private BufferedImage redOverlay = null;
	private BufferedImage greenOverlay = null;
	private BufferedImage blueOverlay = null;
	private BufferedImage currentFrame = null;
	private Mapping mode = Mapping.MAPPED;
	
	private JFrame frame = null;
	private boolean killRenderThread = false;
	private Thread renderThread = null;
	
	private int[] directData = new int[14 * 28 * 3];
	private int windowHeight = 6;
	private int gapHeight = 5;

	/**
	 * Opens a new window displaying a completely turned off LightHouse. The Lights
	 * can then be turned on by providing ImageData via {@code setData}
	 */
	public LightHouseSimulator() {
		super();
		init();
	}
	
	protected void init() {
		try {
			backgroundImage = ImageIO.read(new File(getClass().getResource("background.png").getFile()));

			currentImg = new BufferedImage(backgroundImage.getWidth(), backgroundImage.getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			redOverlay = ImageIO.read(new File(getClass().getResource("red.png").getFile()));
			greenOverlay = ImageIO.read(new File(getClass().getResource("green.png").getFile()));
			blueOverlay = ImageIO.read(new File(getClass().getResource("blue.png").getFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		frame = new JFrame();
		frame.setSize(backgroundImage.getWidth(this), backgroundImage.getHeight(this));
		frame.setVisible(true);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

	}
	
	public void exit() {
		killRenderThread = true;
		if(renderThread != null)
			renderThread.interrupt();
		WindowEvent wev = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
		frame.setVisible(false);
		frame.dispose();
	}
	

	public int[] getDirectData() {
		return directData;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(currentFrame, 0, 0, this);
	}

	public void render() {
		if(renderThread == null) {
			renderThread = new Thread(new Runnable() {
				public void run() {
					while(!killRenderThread) {
						try {
							Thread.sleep(1000);
							break;
						} catch (InterruptedException e) {
							if(killRenderThread) break;
						}
						currentImg = new BufferedImage(currentImg.getWidth(), currentImg.getHeight(), BufferedImage.TYPE_INT_RGB);
						Graphics2D g2d = (Graphics2D) currentImg.createGraphics();
		
						g2d.drawImage(backgroundImage, 0, 0, null);
		
						AdditiveComposite comp = new AdditiveComposite();
		
						for (int x = 0; x < 28; x++)
							for (int y = 0; y < 14; y++) {
								if (directData[(x + y * 28) * 3 + 0] > 0) {
									g2d.setComposite(comp.setFactor(directData[(x + y * 28) * 3 + 0] / 255f));
									g2d.drawImage(redOverlay, (int) (290 + 18.7037 * x), (84 + 42 * y), null);
								}
								if (directData[(x + y * 28) * 3 + 1] > 0) {
									g2d.setComposite(comp.setFactor(directData[(x + y * 28) * 3 + 1] / 255f));
									g2d.drawImage(greenOverlay, (int) (290 + 18.7037 * x), (84 + 42 * y), null);
								}
								if (directData[(x + y * 28) * 3 + 2] > 0) {
									g2d.setComposite(comp.setFactor(directData[(x + y * 28) * 3 + 2] / 255f));
									g2d.drawImage(blueOverlay, (int) (290 + 18.7037 * x), (84 + 42 * y), null);
								}
		
							}
						g2d.dispose();
		
						/*
						 * BufferedImage currentImg = new BufferedImage(BackgroundImage.getWidth(),
						 * BackgroundImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
						 * 
						 * for (int x = 0; x < BackgroundImage.getWidth(); x++) { for (int y = 0; y <
						 * BackgroundImage.getHeight(); y++) { currentImg.setRGB(x, y,
						 * BackgroundImage.getRGB(x, y)); } } for (int x = 0; x < 28; x++) for (int y =
						 * 0; y < 14; y++) { blendAt((int) (290 + 18.7037 * x), (int) (84 + 42 * y),
						 * directData[(x + y * 28) * 3 + 0], directData[(x + y * 28) * 3 + 1],
						 * directData[(x + y * 28) * 3 + 2], currentImg); }
						 */
		
						currentFrame = currentImg;
					}
				}
			});
			killRenderThread = false;
			renderThread.start();
		}
		renderThread.interrupt();
	}
	
	public void updateFrame() {
		this.repaint();
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
		setDataSync(data);
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
	 * @param img an array of RGB values
	 * @param width the width of the image
	 * @param height the height of the image
	 */
	public void setData(int[] rgb, int width, int height) {
		if(bufferImg == null || bufferImg.getWidth() != width || bufferImg.getHeight() != height) {
			bufferImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}
		int[] outPixels = ((DataBufferInt)bufferImg.getRaster().getDataBuffer()).getData();
		System.arraycopy(rgb, 0, outPixels, 0, rgb.length);
		setData(bufferImg);
		
	}

	public void setDataSync(BufferedImage data) {
		if (mode == Mapping.MAPPED) {
			setMappedData(data);
		}
		if (mode == Mapping.DIRECT) {
			if (data.getWidth() != 28 || data.getHeight() != 14)
				throw new IllegalArgumentException("Data must have dimensions 28x14 for direct mapping.");
			setDirectData(data);
		}
		render();
		updateFrame();
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
		// image.SCALE_FAST results in pixlated images, looks nice too but no
		// interpolation at all
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
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

class AdditiveComposite implements Composite {
	private float factor;

	public AdditiveComposite() {
		this.factor = 0f;
	}

	public AdditiveComposite(float factor) {
		this.factor = factor;
	}

	public AdditiveComposite setFactor(float factor) {
		this.factor = factor;
		return this;
	}

	public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
		return new AdditiveCompositeContext(factor);
	}
}

class AdditiveCompositeContext implements CompositeContext {

	private float factor;

	public AdditiveCompositeContext(float factor) {
		this.factor = factor;
	}

	public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

		int[] pxSrc = new int[src.getNumBands()];
		int[] pxDst = new int[dstIn.getNumBands()];
		int chans = Math.min(src.getNumBands(), dstIn.getNumBands());

		for (int x = 0; x < dstIn.getWidth(); x++) {
			for (int y = 0; y < dstIn.getHeight(); y++) {
				pxSrc = src.getPixel(x, y, pxSrc);
				pxDst = dstIn.getPixel(x, y, pxDst);

				for (int i = 0; i < 3 && i < chans; i++) {
					pxDst[i] = Math.min(255, (int) (pxSrc[i] * factor) + (pxDst[i]));
					dstOut.setPixel(x, y, pxDst);
				}
			}
		}
	}

	@Override
	public void dispose() {
	}
}