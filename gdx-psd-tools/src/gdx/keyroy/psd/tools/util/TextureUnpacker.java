package gdx.keyroy.psd.tools.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;

import javax.imageio.ImageIO;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StreamUtils;

public class TextureUnpacker {
	private final Array<Page> pages = new Array<Page>();
	private final Array<Region> regions = new Array<Region>();

	public TextureUnpacker(File packFile, File imagesDir, boolean flip) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(packFile)), 64);
			Page pageImage = null;
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				if (line.trim().length() == 0)
					pageImage = null;
				else if (pageImage == null) {
					// FileHandle file = imagesDir.child(line);
					File file = new File(imagesDir, line);
					BufferedImage bufferedImage = ImageIO.read(file);

					float width = 0, height = 0;
					if (readTuple(reader) == 2) { // size is only optional for
													// an atlas packed with an
													// old TexturePacker.
						width = Integer.parseInt(tuple[0]);
						height = Integer.parseInt(tuple[1]);
						readTuple(reader);
					}
					Format format = Format.valueOf(tuple[0]);

					readTuple(reader);
					TextureFilter min = TextureFilter.valueOf(tuple[0]);
					TextureFilter max = TextureFilter.valueOf(tuple[1]);

					String direction = readValue(reader);
					TextureWrap repeatX = TextureWrap.ClampToEdge;
					TextureWrap repeatY = TextureWrap.ClampToEdge;
					if (direction.equals("x"))
						repeatX = TextureWrap.Repeat;
					else if (direction.equals("y"))
						repeatY = TextureWrap.Repeat;
					else if (direction.equals("xy")) {
						repeatX = TextureWrap.Repeat;
						repeatY = TextureWrap.Repeat;
					}
					pageImage = new Page(bufferedImage, width, height, min.isMipMap(), format, min, max,
							repeatX, repeatY);
					pages.add(pageImage);
				} else {
					boolean rotate = Boolean.valueOf(readValue(reader));

					readTuple(reader);
					int left = Integer.parseInt(tuple[0]);
					int top = Integer.parseInt(tuple[1]);

					readTuple(reader);
					int width = Integer.parseInt(tuple[0]);
					int height = Integer.parseInt(tuple[1]);

					Region region = new Region();
					region.page = pageImage;
					region.left = left;
					region.top = top;
					region.width = width;
					region.height = height;
					region.name = line;
					region.rotate = rotate;

					if (readTuple(reader) == 4) { // split is optional
						region.splits = new int[] { Integer.parseInt(tuple[0]), Integer.parseInt(tuple[1]),
								Integer.parseInt(tuple[2]), Integer.parseInt(tuple[3]) };

						if (readTuple(reader) == 4) { // pad is optional, but
														// only present with
														// splits
							region.pads = new int[] { Integer.parseInt(tuple[0]), Integer.parseInt(tuple[1]),
									Integer.parseInt(tuple[2]), Integer.parseInt(tuple[3]) };

							readTuple(reader);
						}
					}

					region.originalWidth = Integer.parseInt(tuple[0]);
					region.originalHeight = Integer.parseInt(tuple[1]);

					readTuple(reader);
					region.offsetX = Integer.parseInt(tuple[0]);
					region.offsetY = Integer.parseInt(tuple[1]);

					region.index = Integer.parseInt(readValue(reader));

					if (flip)
						region.flip = true;

					regions.add(region);
				}
			}
		} catch (Exception ex) {
			throw new IllegalArgumentException("Error reading pack file: " + packFile, ex);
		} finally {
			if (reader != null) {
				StreamUtils.closeQuietly(reader);
			}
		}

		regions.sort(indexComparator);
	}

	public final Array<Page> getPages() {
		return pages;
	}

	public final Array<Region> getRegions() {
		return regions;
	}

	static final String[] tuple = new String[4];

	/** Returns the number of tuple values read (1, 2 or 4). */
	static int readTuple(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		int colon = line.indexOf(':');
		if (colon == -1)
			throw new IllegalArgumentException("Invalid line: " + line);
		int i = 0, lastMatch = colon + 1;
		for (i = 0; i < 3; i++) {
			int comma = line.indexOf(',', lastMatch);
			if (comma == -1)
				break;
			tuple[i] = line.substring(lastMatch, comma).trim();
			lastMatch = comma + 1;
		}
		tuple[i] = line.substring(lastMatch).trim();
		return i + 1;
	}

	static String readValue(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		int colon = line.indexOf(':');
		if (colon == -1)
			throw new IllegalArgumentException("Invalid line: " + line);
		return line.substring(colon + 1).trim();
	}

	static final Comparator<Region> indexComparator = new Comparator<Region>() {
		public int compare(Region region1, Region region2) {
			int i1 = region1.index;
			if (i1 == -1)
				i1 = Integer.MAX_VALUE;
			int i2 = region2.index;
			if (i2 == -1)
				i2 = Integer.MAX_VALUE;
			return i1 - i2;
		}
	};

	public static class Page {
		public BufferedImage texture;
		public final float width, height;
		public final boolean useMipMaps;
		public final Format format;
		public final TextureFilter minFilter;
		public final TextureFilter magFilter;
		public final TextureWrap uWrap;
		public final TextureWrap vWrap;

		public Page(BufferedImage bufferedImage, float width, float height, boolean useMipMaps,
				Format format, TextureFilter minFilter, TextureFilter magFilter, TextureWrap uWrap,
				TextureWrap vWrap) {
			this.width = width;
			this.height = height;
			this.texture = bufferedImage;
			this.useMipMaps = useMipMaps;
			this.format = format;
			this.minFilter = minFilter;
			this.magFilter = magFilter;
			this.uWrap = uWrap;
			this.vWrap = vWrap;
		}
	}

	public static class Region {
		public Page page;
		public int index;
		public String name;
		public float offsetX;
		public float offsetY;
		public int originalWidth;
		public int originalHeight;
		public boolean rotate;
		public int left;
		public int top;
		public int width;
		public int height;
		public boolean flip;
		public int[] splits;
		public int[] pads;

		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append(left).append(",");
			buffer.append(top).append(",");
			buffer.append(width).append(",");
			buffer.append(height);
			return buffer.toString();
		}
	}

	public static void main(String[] args) {
		File dir = FileUtil.getRoot();
		TextureUnpacker unpacker = new TextureUnpacker(new File(dir, "xianshiguanka.atlas"), dir, false);
		System.out.println("ok " + unpacker.regions.size);
	}
}
