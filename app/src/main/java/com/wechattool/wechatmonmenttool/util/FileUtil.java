package com.wechattool.wechatmonmenttool.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qwy on 2015/12/14.
 * 文件工具类
 */
public class FileUtil {

    public static final int SIZE_TYPE_B = 1;// 获取文件大小单位为B的double值
    public static final int SIZE_TYPE_KB = 2;// 获取文件大小单位为KB的double值
    public static final int SIZE_TYPE_MB = 3;// 获取文件大小单位为MB的double值
    public static final int SIZE_TYPE_GB = 4;// 获取文件大小单位为GB的double值
    private static String root = null;
    // 应用的总文件夹名称
    public static final String APP_PATH = "apps";

    /**
     * SD卡是否存在*
     */
    private static boolean hasSD = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

    /**
     * SD卡的路径*
     */
    private static String SD_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator;

    public static String getRootPath(Context context) {
        if (root == null) {
            if (context != null) {
                File file = getCacheDirectory(context, true);
                if (file != null) {
                    root = file.getAbsolutePath() + File.separatorChar + APP_PATH + File.separatorChar;
                    // 目录/data/data/package/cache/
                    Utility.chmod(root);
                }
            }
        }

        if (root != null) {
            try {
                createDirFile(root);
                File file = new File(root, ".nomedia");
                if (!file.exists())
                    file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return root;
    }

    /**
     * 获取缓存目录
     */
    public static String getImageCachePath(Context context) {
        File f = new File(getRootPath(context) + "/imagesCache/");
        if (!f.exists()) {
            f.mkdir();
            Utility.chmod(f.getAbsolutePath());
        }
        return f.getAbsolutePath();
    }

    /**
     * 创建根目录
     *
     * @param path      目录路径
     */
    public static void createDirFile(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /***
     * 获取根文件路径
     *
     * @param context           上下文
     * @param preferExternal    是否使用外部SDcard,true：使用外部SDcard,false:使用Android下包路径的cache路径
     */
    private static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (Exception e) {
            externalStorageState = "";
        }
        if (preferExternal && Environment.MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
            ///storage/emulated/0/Android/data/package/cache/
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
            ///data/data/package/cache/apis/
        }
        return appCacheDir;
    }

    /***
     * 获取SDcard外部文件路径
     *
     * @param context       上下文
     */
    public static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {

                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {

            }
        }
        return appCacheDir;
    }

    /***
     * 判断是否有写外部文件的权限
     *
     * @param context       上下文
     */
    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 读取SD卡中文本文件
     *
     * @param fileName      文件路径
     */
    public static String readSDFile(String fileName) {
        StringBuffer sb = new StringBuffer();
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            int c;
            while ((c = fis.read()) != -1) {
                sb.append((char) c);
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 写入内容到SD卡中的txt文本中
     * str为内容
     */
    public static void writeSDFile(String str, String fileName) {
        try {
            File f = new File(fileName);
            PrintStream out = null;
            FileOutputStream fout = new FileOutputStream(f);
            out = new PrintStream(fout);
            out.print(str);// 将数据变为字符串后保存
            fout.flush();
            fout.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     **/
    public static void  clearsDirectory(File file) {
        if (file.isFile()) {
            file.delete();
            return ;
        }
        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            for (int i = 0; i < childFiles.length; i++) {
                clearsDirectory(childFiles[i]);
            }
        }
    }

    /**
     * 在SD卡上创建文件
     */
    public static File createSDFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 删除SD卡上的文件
     *
     * @param fileName      要删除的文件路径
     */
    public static boolean deleteSDFile(String fileName) {
        File file = new File(fileName);
        return !(!file.exists() || file.isDirectory()) && file.delete();
    }

    /**
     * 删除文件或清空文件夹
     *
     * @param file          要删除的文件或文件夹
     */
    public static void deleteFilesByDirectory(File file) {
        if (file.isFile()) {
            file.delete();
            return ;
        }

        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                deleteFilesByDirectory(childFiles[i]);
            }
            file.delete();
        }
    }

    /**
     * 保存文件
     *
     * @param bm            要保存的图片
     * @param path          保存路径
     * @param fileName      保存的文件名
     */
    public static String saveFile(Bitmap bm, String path, String fileName) throws IOException {
        File file = new File(path,fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(file));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return file.getAbsolutePath();
    }

    /**
     * 保存png图片
     *
     * @param bm            要保存的图片
     * @param path          保存路径
     * @param fileName      保存的文件名
     */
    public static String savePng(Bitmap bm, String path, String fileName) throws IOException {
        File file = new File(path,fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if(bm.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath      文件路径
     * @param sizeType      获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return              double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatFileSize(blockSize, sizeType);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath      文件路径
     * @return              计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatFileSize(blockSize);
    }

    /**
     * 获取指定文件大小
     *
     * @param file      文件
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f         文件
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS     文件长度
     */
    private static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS         文件大小
     * @param sizeType      转换单位
     */
    private static double formatFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZE_TYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZE_TYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZE_TYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZE_TYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 保存网络图片到本地（需权限）
     *
     * @param context               上下文
     * @param bmp                   要保存的图片
     * @param saveResultCallback    保存结果回调
     */
    public static void savePhoto(final Context context, final Bitmap bmp, final SaveResultCallback saveResultCallback) {
        final File sdDir = getSDPath();
        if (sdDir == null) {
            Toast.makeText(context,"设备自带的存储不可用", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                File appDir = new File(sdDir, "out_photo");
                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置以当前时间格式为图片名称
                String fileName = df.format(new Date()) + ".png";
                File file = new File(appDir, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                    saveResultCallback.onSavedSuccess();
                } catch (FileNotFoundException e) {
                    saveResultCallback.onSavedFailed();
                    e.printStackTrace();
                } catch (IOException e) {
                    saveResultCallback.onSavedFailed();
                    e.printStackTrace();
                }

                //保存图片后发送广播通知更新数据库
                Uri uri = Uri.fromFile(file);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            }
        }).start();
    }


    public static File getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir;
    }

    public interface SaveResultCallback {
        void onSavedSuccess();
        void onSavedFailed();
    }

}
