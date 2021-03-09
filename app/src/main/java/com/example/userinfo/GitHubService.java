package com.example.userinfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface GitHubService {
    @GET("repos/{owner}/{repo}/contributors")
    Call<List<Contributor>> repoContributors(
            @Path("owner") String owner,
            @Path("repo") String repo);

    @GET("/search/users")
    Call<GitResult> getUsers(@Query("q") String name);
    // GET /users/:username

    @GET("/users/{username}")
    Call<User> getUser(
            @Path("username") String userName
    );
    // GET /users/:username/repos

    @GET("users/{username}/repos")
    Call<List<Repos>> getRepos(@Path("username") String userName);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();



}
