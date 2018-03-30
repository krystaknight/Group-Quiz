package edu.umsl.quizlet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.LineGraphSeries;

import edu.umsl.quizlet.database.QuizPersistence;

/**
 * Created by klkni on 5/8/2017.
 */

public class ResultsActivity extends AppCompatActivity {

    private TextView mMyResults;
    private TextView mGroupResults;
    private GraphView mMyGraph;
    private GraphView mGroupGraph;
    private ResultsModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_results_page);
        if(mModel == null){
            mModel = new ResultsModel(this);
        }

        mMyResults= (TextView) findViewById(R.id.my_results);
        mGroupResults = (TextView) findViewById(R.id.group_results);
        mMyGraph = (GraphView) findViewById(R.id.singleUserResultsGraph);
        mGroupGraph = (GraphView) findViewById(R.id.groupResultsGraph);

        mGroupResults.setText(mModel.getGroupResults());
        mMyResults.setText(mModel.getMyResults());

        mMyGraph.addSeries(mModel.getMyData());
        mMyGraph.getViewport().setXAxisBoundsManual(true);
        mMyGraph.getViewport().setMinX(0.5);
        mMyGraph.getViewport().setMaxX(mModel.getMaxX());
        mMyGraph.getViewport().setYAxisBoundsManual(true);
        mMyGraph.getViewport().setMinY(0);
        mMyGraph.getViewport().setMaxY(mModel.getMaxY());
        mMyGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        mMyGraph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        mMyGraph.getGridLabelRenderer().setHumanRounding(true);
        mMyGraph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);

        mGroupGraph.addSeries(mModel.getGroupData());
        mGroupGraph.getViewport().setXAxisBoundsManual(true);
        mGroupGraph.getViewport().setMinX(0.5);
        mGroupGraph.getViewport().setMaxX(mModel.getMaxX());
        mGroupGraph.getViewport().setYAxisBoundsManual(true);
        mGroupGraph.getViewport().setMinY(0);
        mGroupGraph.getViewport().setMaxY(mModel.getMaxY());
        mGroupGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        mGroupGraph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        mGroupGraph.getGridLabelRenderer().setHumanRounding(true);
        mGroupGraph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
    }

    public void goToProfile(View v){
        startActivity(new Intent(this, ProfilePageActivity.class));
        QuizPersistence.sharedInstance(this).nukeAll();
        finish();
    }

}
