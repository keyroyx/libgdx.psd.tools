package psd;

/**
 * PSD 的文件描述
 * @author roy
 */
public class PsdFile extends Folder {
	// 是否使用了 TexturePacker
	public boolean used_texture_packer;
	// 最大大小
	public int maxWidth, maxHeight;
	// 文件名
	public String psdName;
}
