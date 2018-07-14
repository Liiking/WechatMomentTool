package com.wechattool.wechatmonmenttool.util;

import android.content.ComponentName;
import android.content.Context;  
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;

/** 
 * Created by qwy on 2018/6/24.
 * 微信分享工具类
 */  
public class WeiXinShareUtil {

    /**
     * 风险图片到朋友圈
     *
     * @param context           上下文
     * @param pieces            要分享的图片
     */
    public static void sharePhotoToWX(Context context, String[] pieces) {
        if (!Utility.hasInstallApk(context, "com.tencent.mm")) {
            Toast.makeText(context, "请先安装微信！", Toast.LENGTH_SHORT).show();
            return;  
        }
        try {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");
            ArrayList<Uri> imageUris = new ArrayList<Uri>();
            for (String f : pieces) {
                File file = new File(f);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    imageUris.add(Uri.fromFile(file));
                } else {
                    // 获取数据,通过provider方式兼容7.0
                    Uri uri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), f.substring(0, f.lastIndexOf("/")), null));
                    imageUris.add(uri);
                }
            }
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}