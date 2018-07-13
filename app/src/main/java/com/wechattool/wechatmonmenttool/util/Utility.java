package com.wechattool.wechatmonmenttool.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.io.IOException;
import java.util.List;

/**
 * Created by qwy on 2016/1/20.
 * 工具类
 */
public class Utility {

    /**
     * 修改文件或文件夹权限
     *
     * @param path      要修改权限的路径
     */
    public static void chmod(String path) {
        String[] command = {"chmod", "777", path};
        ProcessBuilder builder = new ProcessBuilder(command);
        try {
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据应用包名判断是否已安装应用
     *
     * @param packageName       包名
     */
    public static boolean hasInstallApk(Context context, String packageName) {
        PackageInfo info = getInstallApk(context, packageName);
        return info != null;
    }

    public static PackageInfo getInstallApk(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName)) {
                return pinfo.get(i);
            }
        }
        return null;
    }

}
