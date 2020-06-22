import android.graphics.RectF;

class DrawAllocationCheck {

    @Override
    protected void onDraw(Canvas canvas){
        RectF rectF = new android.graphics.RectF(); // Noncompliant {{Draw Allocation}}

        if(!clockwise) {
            rectF.set(X2 - r, Y2 - r, X2 + r, Y2 + r);
        }
    }

}