package cn.kuwo.player.print;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.github.promeg.pinyinhelper.Pinyin;
import com.orhanobut.logger.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Pos {
    //定义编码方式
    private static String encoding = null;

    private Socket sock = null;
    // 通过socket流进行读写
    private OutputStream socketOut = null;
    private OutputStreamWriter writer = null;

    public final static int WIDTH_PIXEL = 560;
    public final static int IMAGE_SIZE = 260;

    /**
     * 初始化Pos实例
     *
     * @param ip       打印机IP
     * @param port     打印机端口号
     * @param encoding 编码
     * @throws IOException
     */
    public Pos(String ip, int port, String encoding) throws IOException {
        sock = new Socket(ip, port);
        sock.setSoTimeout(8000);
        socketOut = new DataOutputStream(sock.getOutputStream());
        this.encoding = encoding;
        writer = new OutputStreamWriter(socketOut, encoding);
    }

    /**
     * 关闭IO流和Socket
     *
     * @throws IOException
     */
    public void closeIOAndSocket() throws IOException {
        writer.close();
        socketOut.close();
        sock.close();
    }

    /**
     * 打印二维码
     *
     * @param qrData 二维码的内容
     * @throws IOException
     */
    public void qrCode(String qrData) throws IOException {
        int moduleSize = 8;
        int length = qrData.getBytes(encoding).length;

        //打印二维码矩阵
        writer.write(0x1D);// init
        writer.write("(k");// adjust height of barcode
        writer.write(length + 3); // pl
        writer.write(0); // ph
        writer.write(49); // cn
        writer.write(80); // fn
        writer.write(48); //
        writer.write(qrData);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3);
        writer.write(0);
        writer.write(49);
        writer.write(69);
        writer.write(48);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3);
        writer.write(0);
        writer.write(49);
        writer.write(67);
        writer.write(moduleSize);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3); // pl
        writer.write(0); // ph
        writer.write(49); // cn
        writer.write(81); // fn
        writer.write(48); // m

        writer.flush();

    }

    /**
     * 进纸并全部切割
     *
     * @return
     * @throws IOException
     */
    public void feedAndCut() throws IOException {
        writer.write(0x1D);
        writer.write(86);
        writer.write(65);
        //        writer.write(0);
        //切纸前走纸多少
        writer.write(100);
        writer.flush();

        //另外一种切纸的方式
        //        byte[] bytes = {29, 86, 0};
        //        socketOut.write(bytes);
    }

    public void feedAndCut1() throws IOException {

        //另外一种切纸的方式
        byte[] bytes = {29, 86, 0};
        socketOut.write(bytes);
    }

    /**
     * 打印换行
     *
     * @return length 需要打印的空行数
     * @throws IOException
     */
    public void printLine(int lineNum) throws IOException {
        for (int i = 0; i < lineNum; i++) {
            writer.write("\n");
        }
        writer.flush();
    }

    /**
     * 打印换行(只换一行)
     *
     * @throws IOException
     */
    public void printLine() throws IOException {
        writer.write("\n");
        writer.flush();
    }

    /**
     * 打印空白(一个Tab的位置，约4个汉字)
     *
     * @param length 需要打印空白的长度,
     * @throws IOException
     */
    public void printTabSpace(int length) throws IOException {
        for (int i = 0; i < length; i++) {
            writer.write("\t");
        }
        writer.flush();
    }

    /**
     * 打印空白（一个汉字的位置）
     *
     * @param length 需要打印空白的长度,
     * @throws IOException
     */
    public void printWordSpace(int length) throws IOException {
        for (int i = 0; i < length; i++) {
            writer.write("  ");
        }
        writer.flush();
    }

    /**
     * 打印位置调整
     *
     * @param position 打印位置  0：居左(默认) 1：居中 2：居右
     * @throws IOException
     */
    public void printLocation(int position) throws IOException {
        writer.write(0x1B);
        writer.write(0x61);
        writer.write(position);
        writer.flush();
    }
    public void printCenter() throws IOException{
        writer.write(0x1B);
        writer.write(0x61);
        writer.write(1);
        writer.flush();
    }
    /**
     * 绝对打印位置
     *
     * @throws IOException
     */
    public void printLocation(int light, int weight) throws IOException {
        writer.write(0x1B);
        writer.write(0x24);
        writer.write(light);
        writer.write(weight);
        writer.flush();
    }

    /**
     * 打印文字
     *
     * @param text
     * @throws IOException
     */
    public void printText(String text) throws IOException {
        String s = text;
        byte[] content = s.getBytes("gbk");
        socketOut.write(content);
        socketOut.flush();
    }

    /**
     * 新起一行，打印文字
     *
     * @param text
     * @throws IOException
     */
    public void printTextNewLine(String text) throws IOException {
        //换行
        writer.write("\n");
        writer.flush();

        String s = text;
        byte[] content = s.getBytes("gbk");
        socketOut.write(content);
        socketOut.flush();
    }

    /**
     * 初始化打印机
     *
     * @throws IOException
     */
    public void initPos() throws IOException {
        writer.write(0x1B);
        writer.write(0x40);
        writer.flush();
    }

    /**
     * 加粗
     *
     * @param flag false为不加粗
     * @return
     * @throws IOException
     */
    public void bold(boolean flag) throws IOException {
        if (flag) {
            //常规粗细
            writer.write(0x1B);
            writer.write(69);
            writer.write(0xF);
            writer.flush();
        } else {
            //加粗
            writer.write(0x1B);
            writer.write(69);
            writer.write(0xC);
            writer.flush();
        }
    }

    public void fontSizeSetBig(int num) throws IOException {
        byte realSize = 0;
        switch (num) {
            case 1:
                realSize = 0;
                break;
            case 2:
                realSize = 17;
                break;
            case 3:
                realSize = 34;
                break;
            case 4:
                realSize = 51;
                break;
            case 5:
                realSize = 68;
                break;
            case 6:
                realSize = 85;
                break;
            case 7:
                realSize = 102;
                break;
            case 8:
                realSize = 119;
                break;
        }
        writer.write(29);
        writer.write(33);
        writer.write(realSize);
        writer.flush();
    }

    public void printBitmap(Bitmap bmp) throws IOException {
        bmp = compressPic(bmp);
        byte[] bmpByteArray = draw2PxPoint(bmp);
        printRawBytes(bmpByteArray);
    }

    public void printRawBytes(byte[] bytes) throws IOException {
        socketOut.write(bytes);
        socketOut.flush();
    }

    /**
     * 对图片进行压缩（去除透明度）
     */
    public static Bitmap compressPic(Bitmap bitmap) {
        // 获取这个图片的宽和高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 指定调整后的宽度和高度
        int newWidth = 240;
        int newHeight = 240;
        Bitmap targetBmp = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas targetCanvas = new Canvas(targetBmp);
        targetCanvas.drawColor(0xffffffff);
        targetCanvas.drawBitmap(bitmap, new Rect(0, 0, width, height), new Rect(0, 0, newWidth, newHeight), null);
        return targetBmp;
    }

    /**
     * 灰度图片黑白化，黑色是1，白色是0
     *
     * @param x   横坐标
     * @param y   纵坐标
     * @param bit 位图
     * @return
     */
    public static byte px2Byte(int x, int y, Bitmap bit) {
        if (x < bit.getWidth() && y < bit.getHeight()) {
            byte b;
            int pixel = bit.getPixel(x, y);
            int red = (pixel & 0x00ff0000) >> 16; // 取高两位
            int green = (pixel & 0x0000ff00) >> 8; // 取中两位
            int blue = pixel & 0x000000ff; // 取低两位
            int gray = RGB2Gray(red, green, blue);
            if (gray < 128) {
                b = 1;
            } else {
                b = 0;
            }
            return b;
        }
        return 0;
    }

    /**
     * 图片灰度的转化
     */
    private static int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b);  //灰度转化公式
        return gray;
    }

    public static byte[] draw2PxPoint(Bitmap bmp) {
        //用来存储转换后的 bitmap 数据。为什么要再加1000，这是为了应对当图片高度无法
        //整除24时的情况。比如bitmap 分辨率为 240 * 250，占用 7500 byte，
        //但是实际上要存储11行数据，每一行需要 24 * 240 / 8 =720byte 的空间。再加上一些指令存储的开销，
        //所以多申请 1000byte 的空间是稳妥的，不然运行时会抛出数组访问越界的异常。
        int size = bmp.getWidth() * bmp.getHeight() / 8 + 1000;
        byte[] data = new byte[size];
        int k = 0;
        //设置行距为0的指令
        data[k++] = 0x1B;
        data[k++] = 0x33;
        data[k++] = 0x00;
        // 逐行打印
        for (int j = 0; j < bmp.getHeight() / 24f; j++) {
            //打印图片的指令
            data[k++] = 0x1B;
            data[k++] = 0x2A;
            data[k++] = 33;
            data[k++] = (byte) (bmp.getWidth() % 256); //nL
            data[k++] = (byte) (bmp.getWidth() / 256); //nH
            //对于每一行，逐列打印
            for (int i = 0; i < bmp.getWidth(); i++) {
                //每一列24个像素点，分为3个字节存储
                for (int m = 0; m < 3; m++) {
                    //每个字节表示8个像素点，0表示白色，1表示黑色
                    for (int n = 0; n < 8; n++) {
                        byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
                        data[k] += data[k] + b;
                    }
                    k++;
                }
            }
            data[k++] = 10;//换行
        }
        return data;
    }

    public void printTwoColumn(String title, String content) throws IOException {
        int iNum = 0;
        byte[] byteBuffer = new byte[100];
        byte[] tmp;

        tmp = getGbk(title);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp = setLocation(getOffset(content));
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp = getGbk(content);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);

        print(byteBuffer);
    }

    public int getOffset(String str) {
        return WIDTH_PIXEL - getStringPixLength(str);
    }

    public byte[] getGbk(String stText) throws IOException {
        byte[] returnText = stText.getBytes("GBK"); // 必须放在try内才可以
        return returnText;
    }

    public byte[] setLocation(int offset) throws IOException {
        byte[] bs = new byte[4];
        bs[0] = 0x1B;
        bs[1] = 0x24;
        bs[2] = (byte) (offset % 256);
        bs[3] = (byte) (offset / 256);
        return bs;
    }

    private int getStringPixLength(String str) {
        int pixLength = 0;
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (Pinyin.isChinese(c)) {
                pixLength += 24;
            } else {
                pixLength += 12;
            }
        }
        return pixLength;
    }

    public void print(byte[] bs) throws IOException {
        socketOut.write(bs);
    }

    public void printLargeText(String text) throws IOException {

        writer.write(0x1b);
        writer.write(0x21);
        writer.write(48);

        writer.write(text);

        writer.write(0x1d);
        writer.write(0x21);
        writer.write(1);

        writer.flush();
    }

    public void printThreeColumn(String left, String middle, String right) throws IOException {
        int iNum = 0;
        byte[] byteBuffer = new byte[200];
        byte[] tmp = new byte[0];

        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length + 30;

        tmp = getGbk(left);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        int pixLength = getStringPixLength(left) % WIDTH_PIXEL;
        if (pixLength > WIDTH_PIXEL / 2 || pixLength == 0) {
            middle = "\n\t\t\t\t\t\t\t" + middle;
        }

        tmp = setLocation(192);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp = getGbk(middle);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp = setLocation(getOffset(right));
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp = getGbk(right);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);

        print(byteBuffer);
    }

    public void printFourColumn(String left, String middle1, String middle2, String right) throws IOException {
        int iNum = 0;
        byte[] byteBuffer = new byte[200];
        byte[] tmp = new byte[0];

        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length + 30;

        tmp = getGbk(left);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        int pixLength = getStringPixLength(left) % WIDTH_PIXEL;
        if (pixLength > WIDTH_PIXEL / 3 || pixLength == 0) {
            middle1 = "\n\t\t\t\t\t\t\t" + middle1;
        }

        tmp = setLocation(192);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp = getGbk(middle1);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp =  getGbk("    "+middle2);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp = setLocation(getOffset(right));
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp = getGbk(right);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);

        print(byteBuffer);
    }

    public void openCashbox() throws IOException {
        writer.write(27);
        writer.write(112);
        writer.write(0);
        writer.write(10);
        writer.write(10);

        writer.flush();
    }

    //    public void setVisitor() throws IOException {
//        writer.write(0x1b);
//        writer.write(0x40);
//        writer.write(0xc);
//
//        writer.flush();
//    }
    public void sendKexianCmd() throws IOException {
//先复位
        writer.write(0x1b);
        writer.write(0x40);
        byte[] clearcmd = {0xc};
        writer.write(0xc);
        writer.flush();
    }
}


