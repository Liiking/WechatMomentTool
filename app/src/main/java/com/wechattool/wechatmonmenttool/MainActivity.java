package com.wechattool.wechatmonmenttool;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.wechattool.wechatmonmenttool.entity.ImagePiece;
import com.wechattool.wechatmonmenttool.glide.GlideRoundTransform;
import com.wechattool.wechatmonmenttool.util.FileUtil;
import com.wechattool.wechatmonmenttool.util.WeiXinShareUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_submit)
    TextView tv_submit;

    private String local_path;
    private boolean isSharing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_icon, R.id.tv_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_icon:// 选图
                selectSinglePicture();
                break;
            case R.id.tv_submit:// 裁剪成九宫格并发布到朋友圈
                if (TextUtils.isEmpty(local_path)) {
                    Toast.makeText(MainActivity.this, "请先选择图片", Toast.LENGTH_SHORT).show();
                    return ;
                }
                if (isSharing) {
                    Toast.makeText(MainActivity.this, "正在处理，请稍后", Toast.LENGTH_SHORT).show();
                    return ;
                }
                isSharing = true;
                try {
                    FileInputStream fis = new FileInputStream(local_path);
                    Bitmap bitmap  = BitmapFactory.decodeStream(fis);
                    List<ImagePiece> pieces = splitImage(bitmap, 3);
                    if (pieces != null && pieces.size() > 0) {
                        Arrays.sort(pieces.toArray());// 按索引排序
                        int len = pieces.size();
                            String[] paths = new String[len];
                            String name = "wechat_" + System.currentTimeMillis() + "_";
                            for (int i = 0;i < len;i++) {
                                paths[i] = FileUtil.saveFile(pieces.get(i).getBitmap(), FileUtil.getRootPath(MainActivity.this), name + i + ".jpg");
                            }
                        WeiXinShareUtil.sharePhotoToWX(MainActivity.this, paths);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    isSharing = false;
                }
                break;
        }
    }

    /**
     * 选取单张图片
     */
    protected void selectSinglePicture() {
        MultiImageSelector.create(MainActivity.this)
                .showCamera(true) // show camera or not. true by default
//                .count(1) // max select image size, 9 by default. used width #.multi()
                .single() // single mode
//                .multi() // multi mode, default mode;
//                .origin(ArrayList<String>) // original select data set, used width #.multi()
                .start(MainActivity.this, 1000);
    }

    /**
     * 更新选取的图片
     */
    private void updateIcon() {
        Glide.with(MainActivity.this)
                .load(local_path)
                .transform(new GlideRoundTransform(MainActivity.this))
                .into(ivIcon);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1000){
            if(resultCode == RESULT_OK){
                // Get the result list of select image paths
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (path != null && path.size() > 0) {
                    local_path = path.get(0);
                    updateIcon();
                }
                // do your logic ....
            }
        }
    }



    /**
     * 裁剪图片为指定块数
     *
     * @param bitmap        要裁剪的图片
     * @param piece         切成piece*piece块
     */
    public static List<ImagePiece> splitImage(Bitmap bitmap, int piece) {
        List<ImagePiece> imagePieces = new ArrayList<ImagePiece>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int pieceWidth = width / piece;
        int pieceHeight = height / piece;

        for (int i = 0; i < piece; i++){
            for (int j = 0; j < piece; j++){
                ImagePiece imagePiece = new ImagePiece();

                int x = j * pieceWidth;
                int y = i * pieceHeight;

                imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y,
                        pieceWidth, pieceHeight));
                imagePiece.setIndex(j + i * piece);// 设置裁剪后的图片索引
                imagePieces.add(imagePiece);
            }
        }
        return imagePieces;
    }
}
