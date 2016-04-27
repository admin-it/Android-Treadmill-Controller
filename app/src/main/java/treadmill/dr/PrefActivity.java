package treadmill.dr;

        import android.content.Intent;
        import android.graphics.drawable.ColorDrawable;
        import android.os.Bundle;
        import android.preference.PreferenceActivity;
        import android.preference.PreferenceFragment;
        import android.support.v7.app.ActionBar;
<<<<<<< HEAD:app/src/main/java/command/fedex/myapplication/PrefActivity.java
        import android.support.v7.app.ActionBarActivity;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.LayoutInflater;
        import android.widget.LinearLayout;

public class PrefActivity extends AppCompatPreferenceActivity{
=======
        import android.view.MenuItem;

public class PrefActivity extends AppCompatPreferenceActivity {
>>>>>>> 1d18a5e3b3004e82e173b05e03fa9db80cda38ab:app/src/main/java/treadmill/dr/PrefActivity.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        setupActionBar();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
<<<<<<< HEAD:app/src/main/java/command/fedex/myapplication/PrefActivity.java
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
=======
            if (actionBar != null) {
            // Show the Up button in the action bar.

           actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
>>>>>>> 1d18a5e3b3004e82e173b05e03fa9db80cda38ab:app/src/main/java/treadmill/dr/PrefActivity.java
    }
}