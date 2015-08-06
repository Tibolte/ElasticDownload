package is.arontibo.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import is.arontibo.library.ElasticDownloadView;
import is.arontibo.library.ProgressDownloadView;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.elastic_download_view)
    ElasticDownloadView mElasticDownloadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
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
        if (id == R.id.action_run_success_animation) {

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mElasticDownloadView.startIntro();
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mElasticDownloadView.success();
                }
            }, 2 * ProgressDownloadView.ANIMATION_DURATION_BASE);

            return true;
        } else if (id == R.id.action_run_fail_animation) {

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mElasticDownloadView.startIntro();
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mElasticDownloadView.setProgress(45);
                }
            }, 2 * ProgressDownloadView.ANIMATION_DURATION_BASE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mElasticDownloadView.fail();
                }
            }, 3 * ProgressDownloadView.ANIMATION_DURATION_BASE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
