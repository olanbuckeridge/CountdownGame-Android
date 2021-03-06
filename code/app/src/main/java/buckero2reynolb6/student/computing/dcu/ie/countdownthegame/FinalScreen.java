package buckero2reynolb6.student.computing.dcu.ie.countdownthegame;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;

public class FinalScreen extends AppCompatActivity
        implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private Button submitScoreBtn;
    private Button showLeaderboardBtn;
    private Button signInBtn;
    private Button signOutbtn;
    public GoogleApiClient mGoogleApiClient;


    private Toast toast;
    private long lastBackPressTime = 0;

    @Override
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            toast = Toast.makeText(this, "Press back again to return to the home screen.", Toast.LENGTH_SHORT);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            if (toast != null) {
                toast.cancel();
            }
            LetterRound.CountF = 0;
            HomeActivity.totalTally1 = 0;
            HomeActivity.totalTally2 = 0;
            HomeActivity.CountM = 0;
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onPause ()
    {
        super.onPause();
    }

    @Override
    public void onResume ()
    {
        super.onResume();
    }


    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_screen);


        SharedPreferences settings = getSharedPreferences("CountdownTheGame", Context.MODE_PRIVATE);
        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeScreen();
            }
        });
        TextView totalTally, resultView;
        resultView = (TextView) findViewById(R.id.resultView);
        totalTally = (TextView) findViewById(R.id.totalTally);
        totalTally.setText(""+ HomeActivity.totalTally1);
        resultView.setText("Congratulations, you scored " + HomeActivity.totalTally1 + " points.");

        submitScoreBtn = (Button) findViewById(R.id.submitScoreBtn);
        showLeaderboardBtn = (Button) findViewById(R.id.showLeaderboardBtn);
        signInBtn = (Button) findViewById(R.id.signInBtn);
        signOutbtn = (Button) findViewById(R.id.signOutBtn);

        //hide buttons
        submitScoreBtn.setVisibility(View.INVISIBLE);
        showLeaderboardBtn.setVisibility(View.INVISIBLE);
        signInBtn.setVisibility(View.VISIBLE);
        signOutbtn.setVisibility(View.INVISIBLE);

        // set onClickLister
        submitScoreBtn.setOnClickListener(this);
        showLeaderboardBtn.setOnClickListener(this);
        signInBtn.setOnClickListener(this);
        signOutbtn.setOnClickListener(this);

        // initialize google Api Client.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Get player's information.
        Player player = Games.Players.getCurrentPlayer(mGoogleApiClient);

        // Display Name.
        String displayName = "???";
        if (player != null){
            displayName = player.getDisplayName();
        }
        submitScoreBtn.setVisibility(View.VISIBLE);
        showLeaderboardBtn.setVisibility(View.VISIBLE);
        signInBtn.setVisibility(View.INVISIBLE);
        signOutbtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100){
            if(resultCode != RESULT_OK){
                return;
            }
            if(!mGoogleApiClient.isConnected()){
                mGoogleApiClient.connect();
            }
        }
    }
    @Override
    public void onConnectionFailed( ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();

        //Not signed in.
        if (errorCode == connectionResult.SIGN_IN_REQUIRED){
            try{
                connectionResult.startResolutionForResult(this, 100);
            } catch(IntentSender.SendIntentException e){
                mGoogleApiClient.connect();
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signInBtn:
                mGoogleApiClient.connect();
                break;
            case R.id.signOutBtn:
                Games.signOut(mGoogleApiClient);

                if (mGoogleApiClient.isConnected()){
                    mGoogleApiClient.disconnect();
                }

                //Show & Hide Buttons.
                submitScoreBtn.setVisibility(View.INVISIBLE);
                showLeaderboardBtn.setVisibility(View.INVISIBLE);
                signInBtn.setVisibility(View.VISIBLE);
                signOutbtn.setVisibility(View.INVISIBLE);

                //Message
                Toast.makeText(this, "Sign Out", Toast.LENGTH_SHORT).show();
                break;

            case R.id.submitScoreBtn:
                //Submit score
                Games.Leaderboards.submitScore(mGoogleApiClient,
                        getString(R.string.leaderboard_id), HomeActivity.totalTally1);
                Toast.makeText(this, "Submit!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.showLeaderboardBtn:
                //show leaderboard
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
                        mGoogleApiClient, getString(R.string.leaderboard_id)), 1);
                break;
        }
    }

    public void HomeScreen() {
        LetterRound.CountF = 0;
        HomeActivity.totalTally1 = 0;
        HomeActivity.CountM = 0;
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
