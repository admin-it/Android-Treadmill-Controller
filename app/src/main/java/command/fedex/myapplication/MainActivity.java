package command.fedex.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;

public class MainActivity extends AppCompatActivity {
    boolean running = false;
    Line l = new Line();
    LineGraph li;
    Activity act;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Starting bluetooth communication", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMaxValue(9);
        np.setMinValue(0);
        NumberPicker np2 = (NumberPicker) findViewById(R.id.numberPicker2);
        np2.setMaxValue(7);
        np2.setMinValue(0);
        np.setOrientation(NumberPicker.HORIZONTAL);
        act = this;
        l.setUsingDips(true);
        li = (LineGraph) findViewById(R.id.linegraph);
        li.setUsingDips(true);
        li.addLine(l);
//        li.setOnPointClickedListener(new LineGraph.OnPointClickedListener() {
//
//            @Override
//            public void onClick(int lineIndex, int pointIndex) {
//                Toast.makeText(act,
//                        "Line " + lineIndex + " / Point " + pointIndex + " clicked",
//                        Toast.LENGTH_SHORT)
//                        .show();
//            }
//        });
        running = true;
        li.setClickable(false);
        li.setRangeY(0, 10);
        li.setLineToFill(0);
        startGraph();
    }

    AsyncTask<Void, Void, Void> graphThread;
    public void startGraph() {
        graphThread = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
                li.setRangeX(i - 10, i);
                li.invalidate();
            }
            int i = 0;
            @Override
            protected Void doInBackground(Void... params) {
                //int i = 0;
                while(!isCancelled()) {
                    i += 3;
                    int y = (int) (Math.random() * 10);
                    LinePoint p = new LinePoint();
                    p.setX(i);
                    p.setY(y);
                    p.setColor(getResources().getColor(R.color.colorAccent));
                    p.setSelectedColor(getResources().getColor(R.color.colorPrimary));
                    l.addPoint(p);
                    publishProgress();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        graphThread.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        graphThread.cancel(true);
        graphThread = null;
        //running = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //running = true;
        startGraph();
    }
}
