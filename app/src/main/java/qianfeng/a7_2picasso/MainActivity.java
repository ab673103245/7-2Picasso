package qianfeng.a7_2picasso;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    private ImageView iv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = ((ImageView) findViewById(R.id.iv));
        iv2 = ((ImageView) findViewById(R.id.iv2));

    }


    public void downImg(View view) { // 下载图片



        Transformation transformation = new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) { // 这里进行图片的二次处理

                int width = source.getWidth();
                int height = source.getHeight();

                width = height = Math.min(width, height);

                Bitmap blankBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(blankBitmap);

                Paint paint = new Paint();
                paint.setAntiAlias(true);


                canvas.drawCircle(width / 2, height / 2, width / 2, paint);

                // 画笔的模式在你画第二层图之前，就必须设置啊！
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)); // 果然画笔只能放这里啊！！果然画笔只能放两层图层之间啊！

                canvas.drawBitmap(source, 0, 0, paint);

                // 还要记得对source进行回收啊！因为其他google都已经优化过了
                if (source != null && !source.isRecycled()) {
                    source.recycle();
                }

                return blankBitmap;
            }

            @Override
            public String key() { // key是用来识别当前图片的标志，Transformation key must not be null.
                return "picasso";  // 卧槽这里设置为null 真的会报错的!!!!
            }
        };
        // !!! Picasso.with:下载图片的位置是放在 内部存储的Cache文件夹下
        // 使用Picasso.with()图片默认存储在内部存储的Cache文件夹中,此种方式不能修改图片存储路径
        Picasso.with(this).load("http://a1.peoplecdn.cn/5cdf2e6d0d5be1bb09eddc4d545c0671.jpg")
                // Picasso:只有3种图片放缩模式：centerCrop(),centerInside(),fix()  resize()是指定剪裁后的图片的大小

                .centerCrop()  // !!!使用centerCrop、centerInside()必须和resize属性一起配合使用，但是使用fix()就一定不能和resize属性一起使用。这是区别！

                // resize(): 剪裁图片的大小
                .resizeDimen(R.dimen.iv_width, R.dimen.iv_height) // 这是在values/dimens.xml中设置的

                // 对下载的图片进行二次处理
                .transform(transformation)

                // 下载图片失败时，显示的图片
                .error(R.mipmap.ic_launcher)

                // 占位图，下载没完成之前显示的图片
                .placeholder(R.mipmap.ic_launcher)

                // 这个iv，是ImageView或者ImageView的子类(自定义ImageView)都可以
                .into(iv, new Callback() {  // 记得这里的Callback是下载图片成功或失败时调用的
                    @Override
                    public void onSuccess() { // 下载图片成功时，回调这个方法
                        Toast.makeText(MainActivity.this, "下载图片成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() { // 下载图片失败时，回调这个方法
                        Toast.makeText(MainActivity.this, "下载图片失败", Toast.LENGTH_SHORT).show();
                    }
                });



    }

    public void downImg2(View view) {  // 可以设置下载后的图片存放的位置的Picasso

        // 这种方式是可以指定Picasso下载的图片缓冲的位置的
        Picasso picasso = new Picasso.Builder(this)

                .downloader(new OkHttp3Downloader(this.getExternalCacheDir()))

                .build()
                // ListView在滚动的时候，就提醒Picasso暂停下载图片
//                .pauseTag()
                // ListView在停止滚动的时候，就下载图片加载给用户
//                .resumeTag()
                ;

        // 这里一样可以使用上面第一个button中所标注的方法
        picasso.load("http://a1.peoplecdn.cn/5cdf2e6d0d5be1bb09eddc4d545c0671.jpg").into(iv);


    }
}
