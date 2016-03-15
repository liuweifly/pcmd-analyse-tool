package test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JApplet;

/**
 * 图片处理
 * 
 * @author Administrator
 * CreteDate 2012-2-2 下午04:38:16
 * ImageUtils.java
 * wsn.wscmp.web.common
 */
public class ImageUtils extends JApplet {
    private static final long serialVersionUID = 1L;
    private static ImageUtils instance = null;
    private ImageUtils() {
    }
    public static ImageUtils getInstance() {
        if (instance == null) {
            instance = new ImageUtils();
        }
        return instance;
    }
    /**
     * 取得图片信息
     * 
     * @param srcHeight
     * @param srcWidth
     * @param tileSize
     * @return
     */
    public Map<Integer, Integer[]> getImageMap(int srcHeight, int srcWidth, int tileSize) {
        Map<Integer, Integer[]> imageSizeMap = new TreeMap<Integer, Integer[]>();
        if (srcHeight > tileSize && srcWidth > tileSize) {
            int cols = 0; // 切片横向数量
            int rows = 0; // 切片纵向数量
            // 计算切片的横向
            if (srcHeight % tileSize == 0) {
                rows = srcHeight / tileSize;
            } else {
                rows = (int) Math.floor(srcHeight / tileSize) + 1;
            }
            // 计算切片纵向数量
            if (srcWidth % tileSize == 0) {
                cols = srcWidth / tileSize;
            } else {
                cols = (int) Math.floor(srcWidth / tileSize) + 1;
            }
            if (rows > cols) {
                if (rows >= 10) {
                    imageSizeMap.put(14, new Integer[] { srcWidth, srcHeight });
//                    imageSizeMap.put(4, new Integer[] { (rows - 2) * tileSize, (rows - 2) * tileSize });
//                    imageSizeMap.put(3, new Integer[] { (rows - 4) * tileSize, (rows - 4) * tileSize });
//                    imageSizeMap.put(2, new Integer[] { (rows - 6) * tileSize, (rows - 6) * tileSize });
//                    imageSizeMap.put(1, new Integer[] { (rows - 8) * tileSize, (rows - 8) * tileSize });
                } else {
                    throw new RuntimeException("图片太小");
                }
            } else {
                if (cols >= 10) {
                    imageSizeMap.put(14, new Integer[] { srcWidth, srcHeight });
//                    imageSizeMap.put(4, new Integer[] { (cols - 2) * tileSize, (cols - 2) * tileSize });
//                    imageSizeMap.put(3, new Integer[] { (cols - 4) * tileSize, (cols - 4) * tileSize });
//                    imageSizeMap.put(2, new Integer[] { (cols - 6) * tileSize, (cols - 6) * tileSize });
//                    imageSizeMap.put(1, new Integer[] { (cols - 8) * tileSize, (cols - 8) * tileSize });
                } else {
                    throw new RuntimeException("图片太小");
                }
            }
        }
        return imageSizeMap;
    }
    /**
     * 图像切割
     * 
     * @param image
     *            源图像地址
     * @param File
     *            切片目标文件夹
     * @param tileWidth
     *            目标切片宽度
     * @param tileHeight
     *            目标切片高度
     */
    public void cutImage(Image image, File destDir, int tileWidth, int tileHeight) {
        Image img;
        ImageFilter cropFilter;
        try {
            int srcWidth = image.getWidth(null);
            int srcHeight = image.getHeight(null);
            int cols = 0; // 切片横向数量
            int rows = 0; // 切片纵向数量
            // 计算切片的横向和纵向数量
            if (srcHeight % tileHeight == 0) {
                rows = srcHeight / tileHeight;
            } else {
                rows = (int) Math.floor(srcHeight / tileHeight) + 1;
            }
            if (srcWidth % tileWidth == 0) {
                cols = srcWidth / tileWidth;
            } else {
                cols = (int) Math.floor(srcWidth / tileWidth) + 1;
            }
            /**
             * 按照百度自定义地图格式切图
             * 
             * 切片图的命名规则为tile-1_1.png，tile0_1.png，tile1_1.png
             * 
             * tileX为切片图的横坐标 tileY为切片图的纵坐标
             */
            int tileX = 0;
            int tileY = 0;
            // 循环建立切片，先横向，再纵向
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    // 四个参数分别为图像起点坐标和宽高
                    // 即: CropImageFilter(int x,int y,int width,int height)
                    cropFilter = new CropImageFilter(j * tileWidth, i * tileHeight, tileWidth, tileHeight);
                    img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
                    BufferedImage tag = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB_PRE);
                    Graphics2D g = tag.createGraphics();
                    g.setBackground(Color.WHITE);
                    g.drawImage(img, 0, 0, this); // 绘制缩小后的图
                    g.dispose();
                    tileX = -(cols - 1) / 2 + j;
                    tileY = (rows - 1) / 2 - i;
                    File file = new File(destDir + "/tile" + tileX + "_" + tileY + ".png");
                    ImageIO.write(tag, "png", file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


