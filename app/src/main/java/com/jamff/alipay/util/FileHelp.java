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
}
