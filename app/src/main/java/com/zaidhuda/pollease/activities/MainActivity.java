package com.zaidhuda.pollease.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zaidhuda.pollease.R;
import com.zaidhuda.pollease.custom.views.PollListView;
import com.zaidhuda.pollease.helpers.PollDataSource;
import com.zaidhuda.pollease.helpers.UserDataSource;
import com.zaidhuda.pollease.objects.Poll;
import com.zaidhuda.pollease.objects.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private User user;
    private String POLLS_URL;
    private String SESSION_URL;
    private String jsonResult, request_url = "";
    private int responseCode;
    private UserDataSource userDataSource;
    private PollDataSource pollDataSource;
    private List<Poll> polls;
    private ListView pollList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        POLLS_URL = getResources().getString(R.string.polls_url);
        SESSION_URL = getResources().getString(R.string.session_url);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PollEditActivity.class);
                startActivity(intent);
            }
        });
        pollList = (ListView) findViewById(R.id.poll_list);

        initializeUser();

        handleIntentFilter();

        populatePollList(pollList);
    }

    private void handleIntentFilter() {
        Uri data = getIntent().getData();
        if (data != null && data.toString().startsWith(POLLS_URL))
            startPollActivity(data.toString());

    }

    private void initializeUser() {
        userDataSource = new UserDataSource(this);
        userDataSource.open();
        user = userDataSource.getUser();

        if (user == null) {
            user = new User(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
            registerSession();
        }
    }

    private void populatePollList(final ListView pollList) {
        pollDataSource = new PollDataSource(this);
        pollDataSource.open();
        polls = pollDataSource.getAllPolls();

        PollListView pollListView = new PollListView(this, polls);
        pollList.setAdapter(pollListView);
        pollList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        pollList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
                final Poll selectedPoll = polls.get(index);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Remove Poll")
                        .setMessage("Do you really want to remove\n\n" + selectedPoll.getQuestion() + "\n\nfrom the list? This action is irreversible.")
                        .setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_warning_black_48dp))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                removePoll(selectedPoll);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            }
        });

        pollList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startPollActivity(polls.get(position).getUrl());
            }
        });
    }

    private void removePoll(Poll poll) {
        pollDataSource.open();
        pollDataSource.deletePoll(poll);
        populatePollList(pollList);
//        new AlertDialog.Builder(MainActivity.this)
//                .setTitle("Delete Vote")
//                .setMessage(poll.getQuestion() + "\n\nDo you want to delete your vote from this poll? This action is irreversible.")
//                .setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_warning_black_48dp))
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
////                        removeVoteFromPoll(selectedPoll);
//                    }
//                })
//                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new_scan) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
            integrator.setOrientationLocked(false);
            integrator.initiateScan();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult.getContents() != null) {
            String re = scanResult.getContents();
            if (re.startsWith(POLLS_URL)) {
                request_url = re;
                startPollActivity(re);
            }
        }
    }

    @Override
    protected void onResume() {
        populatePollList(pollList);
        userDataSource.open();
        pollDataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        userDataSource.close();
        pollDataSource.close();
        super.onPause();
    }

    private void startPollActivity(String request_url) {
        Intent intent = new Intent(this, PollActivity.class);
        intent.putExtra("poll_url", request_url);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void registerSession() {
        POSTSession task = new POSTSession();
        task.execute(request_url);
    }

    public void ProcessSessionJSON() {
        try {
            JSONObject jUser = new JSONObject(jsonResult);
            User tempUser = new Gson().fromJson(jUser.toString(), User.class);
            user.setId(tempUser.getId());
            user.setToken(tempUser.getToken());
            userDataSource.createUser(user);
        } catch (Exception e) {
            showErrorToast("Error logging in");
        }
    }

    private void showErrorToast(final String msg) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class POSTSession extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(SESSION_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(20000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject param = new JSONObject();
                param.put("user", new JSONObject()
                        .put("identifier", user.getIdentifier()));

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(param.toString());
                wr.flush();

                conn.connect();

                responseCode = conn.getResponseCode();
                InputStream response = conn.getInputStream();
                jsonResult = inputStreamToString(response).toString();
            } catch (ProtocolException e) {
                e.printStackTrace();
                showErrorToast("Error logging in");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                showErrorToast("Error logging in");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                showErrorToast("Error logging in");
            } catch (IOException e) {
                e.printStackTrace();
                showErrorToast("Error logging in");
            } catch (JSONException e) {
                e.printStackTrace();
                showErrorToast("Error logging in");
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                showErrorToast("Error logging in");
            }
            return answer;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "", "Logging in, please wait", false);
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (responseCode == HttpURLConnection.HTTP_OK) {
                ProcessSessionJSON();
                Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
            } else
                showErrorToast("Error logging in");
        }
    }
}
