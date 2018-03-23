package com.example.horselai.gank.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.example.horselai.gank.app.App;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by laixiaolong on 2017/1/23.
 */
public class FileManager
{

/*
    *//**
 * 应用数据在外部存储（内存卡）中的文件根目录
 *//*
    public static final String APP_EXTERNAL_DIR;
    *//**
 * 缓存目录
 *//*
    public static final String APP_CACHE_DIR;
    *//**
 * 文件目录
 *//*
    public static final String APP_FILES_DIR;

    *//**
 * 手机存储根路径
 *//*
    public static final String STORAGE_SDCARD_0 = "/storage/sdcard0/";
    *//**
 * 内存卡存储根路径（注意：可能不存在）
 *//*
    public static final String STORAGE_SDCARD_1 = "/storage/sdcard1/";
    */

    /**
     * 外接的OTG存储
     *//*
    public static final String STORAGE_USB_OTG = "/storage/usbotg/";

    static {
        APP_EXTERNAL_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();

        APP_CACHE_DIR = App.context().getCacheDir().getAbsolutePath() + File.separator;
        APP_FILES_DIR = App.context().getFilesDir().getAbsolutePath() + File.separator;
    }
 */
    static class Builder
    {
        private static final FileManager FILE_MANAGER = new FileManager();
    }

    public static FileManager getInstance()
    {
        return Builder.FILE_MANAGER;
    }


    /**
     * @param context
     * @param cacheDirName 存放缓存的文件夹名称（非绝对路径）
     * @return
     */
    public File createCacheDir(Context context, String cacheDirName)
    {
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(context.getExternalCacheDir(), cacheDirName);
        } else {
            file = new File(context.getCacheDir(), cacheDirName);
        }
        if (!file.exists()) file.mkdirs();
        return file;
    }

    public boolean exists(String absPath)
    {
        return new File(absPath).exists();
    }

    /**
     * 初始化主目录，
     *
     * @param context
     * @param homeDirName 文件夹路径
     * @return 返回主工作目录
     */
    public File initHomeDir(Context context, String homeDirName)
    {
        //File file = new File("/storage/sdcard0/" + homeDirName);
        File file = new File(context.getExternalFilesDir(null), homeDirName);
        createDir(file.getAbsolutePath());
        if (!file.exists() || !file.isDirectory()) {
            file = new File(context.getExternalCacheDir(), homeDirName);
            createDir(file.getAbsolutePath());
        }
        return file;
    }

    /**
     * @param context
     * @param picDir
     * @return
     */
    public File initPicturePath(Context context, String picDir)
    {
        File file = App.getAppHomePath();
        if (file != null) {
            file = new File(file.getAbsoluteFile(), picDir);
            createDir(file.getPath());
            return file;
        }
        //保底操作
        file = new File(context.getFilesDir(), picDir);
        createDir(file.getAbsolutePath());
        return file;
    }

    private FileManager()
    {
        //创建根目录
        //createDir(APP_EXTERNAL_DIR );
    }

    /**
     * 向文件中写入数据
     *
     * @param absPath
     * @throws IOException
     */
    public void writeToFile(@NonNull String absPath, InputStream is) throws IOException
    {
        final File file = new File(absPath);

        final boolean ok = createFile(file.getPath());
        if (!ok) return;

        final byte[] buf = new byte[1024];
        BufferedInputStream bis = new BufferedInputStream(is);
        final FileOutputStream fos = new FileOutputStream(file);
        int read;
        while ((read = bis.read(buf)) != -1) {
            fos.write(buf, 0, read);
        }
        fos.flush();

        Utils.close(fos, bis, is);
    }

    /**
     * 向文件中写入数据
     *
     * @param absPath
     * @param data
     * @throws IOException
     */
    public void writeToFile(@NonNull String absPath, byte[] data) throws IOException
    {
        final File file = new File(absPath);
        writeToFile(file, data);
    }

    /**
     * 向文件中写入数据
     *
     * @param file
     * @param data
     * @throws IOException
     */
    public void writeToFile(@NonNull File file, byte[] data) throws IOException
    {
        if (!Uri.fromFile(file).isAbsolute()) throw new IllegalArgumentException("传入的file不合法！");

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        bout.write(data);
        bout.flush();

        final FileOutputStream fout = new FileOutputStream(file);
        bout.writeTo(fout);
        fout.flush();

        fout.close();
        bout.close();
    }


    /**
     * 向文件写入数据
     *
     * @param absPath
     * @param buffCapacity
     * @return
     * @throws IOException
     */
    public void readFromFile(@NonNull String absPath, @NonNull int buffCapacity, @NonNull FileDataCallback callback)
    {
        final File file = new File(absPath);
        readFromFile(file, buffCapacity, callback);
    }

    /**
     * 向文件写入数据
     *
     * @param file
     * @param buffCapacity
     * @return
     * @throws IOException
     */
    public void readFromFile(@NonNull File file, @NonNull int buffCapacity, @NonNull FileDataCallback callback)
    {
        if (!Uri.fromFile(file).isAbsolute()) throw new IllegalArgumentException("传入的file不合法！");

        final ByteBuffer buffer = ByteBuffer.allocate(buffCapacity);

        try (final FileInputStream fin = new FileInputStream(file); final FileChannel channel = fin.getChannel()) {

            final int read = channel.read(buffer);
            callback.onReadDone(buffer, read);

        } catch (IOException e) {
            callback.onReadFailed(e);
            e.printStackTrace();
        }
    }


    /**
     * 创建文件夹， 注意：如果文件夹已存在，不做任何处理
     *
     * @param absPath 绝对路径
     * @return 创建成功返回true，若创建失败或文件夹已存在，返回false
     */
    public boolean createDir(@NonNull String absPath)
    {
        final File file = new File(absPath);
        if (!Uri.fromFile(file).isAbsolute()) throw new IllegalArgumentException("传入的路径不合法！");

        if (!file.exists()) {
            return file.mkdirs();
        }
        return false;
    }


    /**
     * 创建文件 ， 注意：如果文件夹已存在，会删除原文件后重新创建
     *
     * @param absPath 绝对路径
     * @return 创建成功返回true，若创建失败或文件已存在，返回false
     */
    public boolean createFile(@NonNull String absPath) throws IOException
    {
        final File file = new File(absPath);
        if (!Uri.fromFile(file).isAbsolute()) throw new IllegalArgumentException("传入的路径不合法！");

        if (file.exists() && file.canWrite() && file.canRead()) {
            file.delete();
        }
        return file.createNewFile();

    }

    /**
     * 创建文件， 注意：如果文件夹已存在，则不做任何处理
     *
     * @param absPath 绝对路径
     * @return 创建成功返回true，若创建失败或文件已存在，返回false
     */
    public boolean createFileIfNotExits(@NonNull String absPath) throws IOException
    {
        final File file = new File(absPath);
        if (!Uri.fromFile(file).isAbsolute()) throw new IllegalArgumentException("传入的路径不合法！");

        if (!file.exists()) {
            return file.createNewFile();
        }
        return false;
    }

    /**
     * 删除文件或者文件夹
     *
     * @param absPath 绝对路径
     */
    public void deleteAnyway(@NonNull String absPath)
    {
        final File file = new File(absPath);
        if (!Uri.fromFile(file).isAbsolute()) throw new IllegalArgumentException("传入的路径不合法！");

        file.deleteOnExit();
    }

    /**
     * 清空文件夹中的内容
     *
     * @param dirPath        文件夹绝对路径
     * @param deleteThisPath 是否把当前文件夹也删了
     */
    public void clearFilesInFolder(@NonNull String dirPath, boolean deleteThisPath)
    {
        final File file = new File(dirPath);
        if (!Uri.fromFile(file).isAbsolute()) throw new IllegalArgumentException("传入的路径不合法！");

        if (file.isFile()) {
            file.delete();
            return;
        }
        final File[] files = file.listFiles();
        if (files == null) return;

        for (File f : files) {

            if (f.canRead() && f.canWrite()) {
                f.delete();
            }
        }
        if (deleteThisPath) {
            file.delete();
        }

    }

    /**
     * 获取当前文件或文件夹内文件的大小
     *
     * @param file 文件或文件夹
     * @return 返回指定路径的存储空间大小
     */
    public long calculateFileSize(@NonNull File file)
    {
        if (!Uri.fromFile(file).isAbsolute()) throw new IllegalArgumentException("传入的路径不合法！");

        if (!file.exists()) return 0L;
        long size = 0L;
        if (file.isFile()) {
            size += file.length();
        } else {

            final File[] files = file.listFiles();
            if (files.length == 0) return 0L;

            for (File f : files) {
                size += calculateFileSize(f);
            }
        }
        return size;
    }

    /**
     * 修改文件名
     *
     * @param absPath 需要修改的文件绝对路径
     * @param newName 新名称
     * @return
     */
    public boolean rename(@NonNull String absPath, @NonNull String newName)
    {
        final File file = new File(absPath);
        if (!Uri.fromFile(file).isAbsolute()) throw new IllegalArgumentException("传入的路径不合法！");
        return rename(file, newName);
    }

    /**
     * 修改文件名
     *
     * @param file    要修改的文件
     * @param newName 新名称
     * @return
     */
    public boolean rename(@NonNull File file, @NonNull String newName)
    {
        newName = file.getParent() + File.separator + newName;
        return file.renameTo(new File(newName));
    }


    /**
     * 获取文件绝对路径列表
     *
     * @param dirPath 文件夹绝对路径
     * @param endWith 用于筛选的后缀字符，例如筛选MP3文件可使用 “.mp3”
     * @return
     */
    public List<String> listFiles(@NonNull String dirPath, @NonNull String endWith)
    {
        final File file = new File(dirPath);
        if (!Uri.fromFile(file).isAbsolute()) throw new IllegalArgumentException("传入的路径不合法！");

        final File[] files = file.listFiles();
        final List<String> data = new ArrayList<>();
        String path = "";
        for (File f : files) {
            if (f != null && f.isFile()) path = f.getAbsolutePath();
            if (path.endsWith(endWith)) data.add(path);
        }
        return data;
    }

    /**
     * 打印当前目录下的所有文件
     *
     * @param dirPath
     */
    public void printFilesInThePath(@NonNull String dirPath)
    {
        final File file = new File(dirPath);
        if (!Uri.fromFile(file).isAbsolute()) throw new IllegalArgumentException("传入的路径不合法！");

        final File[] files = file.listFiles();
        if (files == null) {
            System.out.println("printFilesInThePath >>> no file here!");
            return;
        }
        for (File f : files) {

            if (f == null) return;
            if (f.isDirectory()) {
                System.out.println("printFilesInThePath >>> + " + f.getName());
            } else {
                System.out.println("printFilesInThePath >>> - " + f.getName());
            }
        }
    }


    /**
     * 文件读取时的数据回调接口
     */
    public interface FileDataCallback
    {
        /**
         * 读取完成后调用
         *
         * @param data 读取到的数据
         * @param read 读取到的总字节数
         */
        void onReadDone(ByteBuffer data, int read);

        void onReadFailed(Exception e);
    }


    /**
     * Created by dashan on 2016/9/7
     * 格式化单位
     *
     * @param size size
     * @return size
     */
    public String formatSize(long size)
    {

        long kiloByte = size / 1024;
        //        if (kiloByte < 1) {
        //            return size + "Byte";
        //        }

        long megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Long.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        long gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Long.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        long teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Long.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);

        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    public static class ExternalDirNotMountedException extends Exception
    {
        public ExternalDirNotMountedException(String message)
        {
            super(message);
        }
    }

}
