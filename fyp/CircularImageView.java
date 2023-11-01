package com.example.fyp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class CircularImageView extends AppCompatImageView {
    private BitmapShader shader;
    private Paint paint;

    public CircularImageView(Context context) {
        super(context);
        init();
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() != null) {
            Bitmap bitmap = drawableToBitmap(getDrawable());
            if (bitmap != null) {
                shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                paint.setShader(shader);

                float radius = Math.min(canvas.getWidth(), canvas.getHeight()) / 2f;
                float scaleX = (float) canvas.getWidth() / bitmap.getWidth();
                float scaleY = (float) canvas.getHeight() / bitmap.getHeight();
                float scale = Math.max(scaleX, scaleY);
                float dx = (canvas.getWidth() - bitmap.getWidth() * scale) / 2f;
                float dy = (canvas.getHeight() - bitmap.getHeight() * scale) / 2f;

                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                matrix.postTranslate(dx, dy);
                shader.setLocalMatrix(matrix);

                canvas.drawCircle(canvas.getWidth() / 2f, canvas.getHeight() / 2f, radius, paint);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    private Bitmap drawableToBitmap(android.graphics.drawable.Drawable drawable) {
        if (drawable instanceof android.graphics.drawable.BitmapDrawable) {
            return ((android.graphics.drawable.BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
