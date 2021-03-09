package com.example.userinfo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar mProgressBar;
    private TextView mTextView;
    private EditText eTUser;
    private EditText eTRepo;
    Button btnUser;
    Button btnRepos;
    Button btnUsers;
    Button btnSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);
        eTUser = (EditText) findViewById(R.id.eTUser);
        eTRepo = (EditText) findViewById(R.id.eTRepo);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnUser = (Button) findViewById(R.id.btnUser);
        btnRepos = (Button) findViewById(R.id.btnRepos);
        btnUsers = (Button) findViewById(R.id.btnUsers);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        mProgressBar.setVisibility(View.INVISIBLE);

        btnUser.setOnClickListener(this);
        btnRepos.setOnClickListener(this);
        btnUsers.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        String result = eTUser.getText().toString();
        String resultRepo = eTRepo.getText().toString();
        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        switch (v.getId()){
            case R.id.btnUser:
                mProgressBar.setVisibility(View.VISIBLE);
                final Call<User> call = gitHubService.getUser(result);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        // response.isSuccessfull() is true if the response code is 2xx
                        if (response.isSuccessful()) {
                            User user = response.body();

                            // Получаем json из github-сервера и конвертируем его в удобный вид
                            mTextView.setText("Аккаунт Github: " + user.getName() +
                                    "\nСайт: " + user.getBlog() +
                                    "\nКомпания: " + user.getCompany());

                            mProgressBar.setVisibility(View.INVISIBLE);
                        } else {
                            int statusCode = response.code();

                            // handle request errors yourself
                            ResponseBody errorBody = response.errorBody();
                            try {
                                mTextView.setText(errorBody.string());
                                mProgressBar.setVisibility(View.INVISIBLE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable throwable) {
                        mTextView.setText("Что-то пошло не так: " + throwable.getMessage());
                    }
                });
        break;
        case R.id.btnRepos:
            mProgressBar.setVisibility(View.VISIBLE);
            final Call<List<Repos>> call1 = gitHubService.getRepos(result);

            call1.enqueue(new Callback<List<Repos>>() {
                             @Override
                             public void onResponse(Call<List<Repos>> call1, Response<List<Repos>> response) {
                                 // response.isSuccessfull() is true if the response code is 2xx
                                 if (response.isSuccessful()) {
                                     // Выводим массив имён
                                    mTextView.setText(response.body().toString() + "\n");
                                     for (int i = 0; i < response.body().size(); i++) {
                                         // Выводим имена по отдельности
                                         mTextView.append(response.body().get(i).getName() + "\n");
                                     }

                                     mProgressBar.setVisibility(View.INVISIBLE);
                                 } else {
                                     int statusCode = response.code();
                                     // Обрабатываем ошибку
                                     ResponseBody errorBody = response.errorBody();
                                     try {
                                         mTextView.setText(errorBody.string());
                                         mProgressBar.setVisibility(View.INVISIBLE);
                                     } catch (IOException e) {
                                         e.printStackTrace();
                                     }
                                 }
                             }

                             @Override
                             public void onFailure(Call<List<Repos>> call1, Throwable throwable) {
                                 mTextView.setText("Что-то пошло не так: " + throwable.getMessage());
                             }
                         }
            );
            break;
            case R.id.btnUsers:
                final Call<List<Contributor>> call2 = gitHubService.repoContributors(result, resultRepo);
                call2.enqueue(new Callback<List<Contributor>>() {
                    @Override
                    public void onResponse(Call<List<Contributor>> call2, Response<List<Contributor>> response) {
                        final TextView textView = (TextView) findViewById(R.id.textView);
                        textView.setText(response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<List<Contributor>> call2, Throwable throwable) {
                        final TextView textView = (TextView) findViewById(R.id.textView);
                        textView.setText("Что-то пошло не так: " + throwable.getMessage());
                    }
                });
                break;
            case R.id.btnSearch:
                mProgressBar.setVisibility(View.VISIBLE);
                // часть слова
                final Call<GitResult> call3 =
                        gitHubService.getUsers(result);

                call3.enqueue(new Callback<GitResult>() {
                    @Override
                    public void onResponse(Call<GitResult> call3, Response<GitResult> response) {
                        // response.isSuccessful() is true if the response code is 2xx
                        if (response.isSuccessful()) {
                            GitResult result = response.body();

                            // Получаем json из github-сервера и конвертируем его в удобный вид
                            // Покажем только первого пользователя
                            mTextView.setText(" ");
                            for (int i = 0; i < result.getItems().size(); i++){
                                String user = "Аккаунт Github: " + result.getItems().get(i).getLogin();
                                mTextView.append(user + "\n");
                            }

                            Log.i("Git", String.valueOf(result.getItems().size()));

                            mProgressBar.setVisibility(View.INVISIBLE);
                        } else {
                            int statusCode = response.code();

                            // handle request errors yourself
                            ResponseBody errorBody = response.errorBody();
                            try {
                                mTextView.setText(errorBody.string());
                                mProgressBar.setVisibility(View.INVISIBLE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<GitResult> call, Throwable throwable) {
                        mTextView.setText("Что-то пошло не так: " + throwable.getMessage());
                    }
                });
                break;
        }
    }
}