package org.nondoc.android.a2vnc;

import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class RemoteControlView extends View {

    public float factor;

    public RemoteControlView(Context context) {
        super(context);
        factor = (float)1.0;
    }

    @Override
    protected void  onDraw(Canvas canvas) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        Paint paint = new Paint();
        paint.setARGB(128, (int)(255 * factor), (int)(50 * factor), (int)(200 * factor));
        canvas.drawOval(new RectF((float)0.0, (float)0.0, w * factor, h * factor), paint);
    }

}
