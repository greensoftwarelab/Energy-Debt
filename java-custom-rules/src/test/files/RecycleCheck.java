import android.content.res.TypedArray;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class RecycleCheck {

    public void init(AttributeSet attrs){ // Noncompliant {{Recycle}}
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideLayout);
        return;
    }

    public void init(AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideLayout);
        typedArray.recycle();
        return;
    }

    public Summoner getSummoner(int id) { // Noncompliant {{Recycle}}
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAV, new String[]{userName}, null, null, null);
        return summoner;
    }

    public Summoner getSummoner(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAV, new String[]{userName}, null, null, null);
        cursor.close();
        return summoner;
    }

    public void foo(){
        int a = 0;
        a += 10;
    }

}