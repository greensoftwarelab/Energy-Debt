import android.view.View;

class ViewHolderCheck {
    TextView timer;
    long startTime;
    long timeLimit = 20000;
    int amount = -1;
    int seconds = 1;
    int goal = 125;
    boolean done = false;

    public View getView (int position, View view, ViewGroup param) { // Noncompliant {{View Holder}}
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.apps, param, false);
        TextView txt = view.findViewById(R.id.label);
        ImageView img = view.findViewById(R.id.logo);
        return row;
    }

    public void clickButton(View view) {
        if (!done) {
            if (amount == -1) {
                startTime = System.currentTimeMillis();
                timerHandler.postDelayed(timerRunnable, 0);
                ((Button) view).setText("CLICK ME!");
                amount++;
            } else if (seconds >= 0) {
                amount++;
                ((TextView) findViewById(R.id.amount)).setText("Times clicked: " + amount);
            }
        } else {
            ((Button) findViewById(R.id.clickButton)).setText("Finish");
            Intent i = new Intent(this, WaitingActivity.class);
            startActivity(i);

        }

    }
}

