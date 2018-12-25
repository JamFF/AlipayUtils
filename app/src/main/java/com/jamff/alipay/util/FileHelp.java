package com.jamff.alipay.util;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.jamff.alipay.Constant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * description:
 * author: JamFF
 * time: 2018/12/14 22:37
 */
public class FileHelp {

    public static boolean createFolderIfNotExist(String strFolder) {

        File basefile = new File(Constant.ROOT_PATH);
        if (!basefile.exists()) {
            basefile.mkdir();
        }
        File file = new File(strFolder);

        if (!file.exists()) {
            return file.mkdir();
        }
        return true;
    }

    /**
     * @param msg      写入的消息
     * @param parent   父目录
     * @param filename 文件名
     * @param isAppend 是否是追加
     * @return 是否保存成功
     */
    public static boolean saveFile(String msg, String parent, String filename, boolean isAppend) {
        if (TextUtils.isEmpty(msg)) {
            return false;
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(parent);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try {
                FileWriter fw = new FileWriter(dir + File.separator + filename, isAppend);
                PrintWriter pw = new PrintWriter(fw, true);
                pw.println(msg);
                return true;
            } catch (IOException e) {
                // Log.e(TAG, "saveFile: 发生异常");
                e.printStackTrace();
                return false;
            }
        } else {
            // Log.e(TAG, "saveFile: 没有内存卡");
            return false;
        }
    }

    public static long getSDAvailableSize() {
        if (isSdCardMounted()) {
            StatFs stat = new StatFs(Constant.SD_CARD);
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();

            return blockSize * availableBlocks;
        }
        return 0;
    }

    public static boolean isSdCardMounted() {
        String storageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(storageState);
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 路径存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 路径不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            // 删除目录失败
            return false;
        }
        // 删除当前目录
        return dirFile.delete();
    }
}
