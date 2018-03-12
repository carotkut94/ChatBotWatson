package com.death.xorbot;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.google.gson.Gson;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    List<ResponseModel> responseModelList;
    BotAdapter botAdapter;
    RecyclerView conversation;
    EditText userInput;
    ImageButton speakButton;
    ConversationService myConversationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        responseModelList = new ArrayList<>();
        myConversationService =
                new ConversationService(
                        "2017-11-06",
                        getString(R.string.username),
                        getString(R.string.password)
                );
        conversation = findViewById(R.id.conversation);
        userInput = findViewById(R.id.user_input);
        speakButton = findViewById(R.id.speak);

        botAdapter = new BotAdapter(responseModelList, this);
        conversation.setAdapter(botAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        conversation.setLayoutManager(linearLayoutManager);

        userInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    callWatson(myConversationService);
                }
                return false;
            }
        });

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
    }

    public void addToAdapter(String data) {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setMine(false);
        responseModel.setResponse(data);
        responseModelList.add(responseModel);
        botAdapter.notifyDataSetChanged();
        if (!isLastVisible())
            conversation.smoothScrollToPosition(botAdapter.getItemCount() - 1);
    }

    public void addToAdapter(String data, Movies movies, String category) {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setMine(false);
        responseModel.setResponse(data);
        responseModel.setCollection(true);
        responseModel.setMovie(true);
        responseModel.setMovies(movies);
        responseModel.setMovieQueryType(category);
        responseModelList.add(responseModel);
        botAdapter.notifyDataSetChanged();
        if (!isLastVisible())
            conversation.smoothScrollToPosition(botAdapter.getItemCount() - 1);

    }


    boolean isLastVisible() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) conversation.getLayoutManager());
        int pos = layoutManager.findLastCompletelyVisibleItemPosition();
        int numItems = conversation.getAdapter().getItemCount();
        return (pos >= numItems);
    }

    public void addLoadingToAdapter() {
        userInput.setEnabled(false);
        ResponseModel responseModel = new ResponseModel();
        responseModel.setMine(false);
        responseModel.setLoading(true);
        responseModel.setResponse("I am adding things up..");
        responseModelList.add(responseModel);
        botAdapter.notifyDataSetChanged();
    }

    public void removeLoading() {
        botAdapter.removeLastElement();
        userInput.setEnabled(true);
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something !");
        try {
            startActivityForResult(intent, 19);
        } catch (ActivityNotFoundException a) {
            Log.e("ERROR", a.getLocalizedMessage());
            Toast.makeText(getApplicationContext(),
                    "Speech not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 19: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    userInput.setText(result.get(0));
                    callWatson(myConversationService);
                }
                break;
            }
        }
    }

    public void callWatson(ConversationService myConversationService) {
        final String inputText = userInput.getText().toString();
        ResponseModel responseModel = new ResponseModel();
        responseModel.setMine(true);
        responseModel.setResponse(inputText);
        responseModelList.add(responseModel);
        botAdapter.notifyDataSetChanged();
        if (!isLastVisible())
            conversation.smoothScrollToPosition(botAdapter.getItemCount() - 1);
        userInput.setText("");
        addLoadingToAdapter();
        MessageRequest request = new MessageRequest.Builder()
                .inputText(inputText)
                .build();

        myConversationService
                .message(getString(R.string.workspace), request)
                .enqueue(new ServiceCallback<MessageResponse>() {
                    @Override
                    public void onResponse(MessageResponse response) {
                        // More code here
                        Log.e("Response", response.toString());
                        if (response.getIntents().get(0).getIntent()
                                .endsWith("RequestQuote")) {
                            String quotesURL =
                                    "https://api.forismatic.com/api/1.0/" +
                                            "?method=getQuote&format=text&lang=en";
                            Fuel.get(quotesURL)
                                    .responseString(new Handler<String>() {
                                        @Override
                                        public void success(Request request,
                                                            Response response, String quote) {
                                            removeLoading();
                                            addToAdapter(quote);
                                        }

                                        @Override
                                        public void failure(Request request,
                                                            Response response,
                                                            FuelError fuelError) {
                                        }
                                    });
                        } else if (response.getIntents().get(0).getIntent()
                                .endsWith("Joke")) {
                            HashMap<String, String> headerValue = new HashMap<>();
                            headerValue.put("Accept", "text/plain");
                            Fuel.get("https://icanhazdadjoke.com/").header(headerValue)
                                    .responseString(new Handler<String>() {
                                        @Override
                                        public void success(Request request,
                                                            Response response, String joke) {
                                            removeLoading();
                                            addToAdapter(joke);
                                        }

                                        @Override
                                        public void failure(Request request,
                                                            Response response,
                                                            FuelError fuelError) {
                                            Log.e("ERROR", fuelError.toString());
                                        }

                                    });
                        } else if (response.getIntents().get(0).getIntent()
                                .endsWith("PopularMovies")) {
                            final String response_christopher = response.getText().get(0);
                            Fuel.get(Constants.POPULAR_MOVIES)
                                    .responseString(new Handler<String>() {
                                        @Override
                                        public void success(Request request, Response response, String s) {
                                            removeLoading();
                                            Movies movies = new Gson().fromJson(s, Movies.class);
                                            addToAdapter(response_christopher, movies, "PopularMovies");
                                        }

                                        @Override
                                        public void failure(Request request, Response response, FuelError fuelError) {

                                        }
                                    });
                        } else if (response.getIntents().get(0).getIntent()
                                .endsWith("TopMovies")) {
                            final String response_christopher = response.getText().get(0);
                            Fuel.get(Constants.TOP_RATED_MOVIES)
                                    .responseString(new Handler<String>() {
                                        @Override
                                        public void success(Request request, Response response, String s) {
                                            removeLoading();
                                            Movies movies = new Gson().fromJson(s, Movies.class);
                                            addToAdapter(response_christopher, movies, "TopMovies");
                                        }

                                        @Override
                                        public void failure(Request request, Response response, FuelError fuelError) {

                                        }
                                    });
                        } else if (response.getIntents().get(0).getIntent()
                                .endsWith("StackOverflow")) {
                            final String response_christopher = response.getText().get(0);
                            new ParseWeb().execute(Constants.STACK_OVERFLOW_QUERY + inputText, response_christopher);
                        } else {
                            final String outputText = response.getText().get(0);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    removeLoading();
                                    ResponseModel responseModel = new ResponseModel();
                                    responseModel.setMine(false);
                                    responseModel.setResponse(outputText);
                                    responseModelList.add(responseModel);
                                    botAdapter.notifyDataSetChanged();
                                    if (!isLastVisible())
                                        conversation.smoothScrollToPosition(botAdapter.getItemCount() - 1);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                    }
                });

    }

    class ParseWeb extends AsyncTask<String, List<StackOverflow>, List<StackOverflow>> {

        String christopher_response;
        @Override
        protected List<StackOverflow> doInBackground(String... strings) {
            Document doc = null;
            List<StackOverflow> overflows = new ArrayList<>();
            StackOverflow stackOverflow = null;
            christopher_response = strings[1];
            try {
                doc = Jsoup.connect(strings[0]).userAgent("AI for problems").get();
                Log.e("DATA", doc.title());
                Log.e("DATA HTML", doc.toString());
                Element element = doc.select("div.search-results.js-search-results").first();
                if (element != null) {
                    Elements elements = element.select("div.summary");
                    Elements stats = element.select("div.statscontainer");
                    Log.e("Length", "" + elements.size());
                    if (elements.size() > 0) {
                        for (int i = 0; i < elements.size(); i++) {
                            stackOverflow = new StackOverflow();
                            stackOverflow.setQuestion(elements.get(i).getElementsByClass("result-link").text());
                            stackOverflow.setExcerpt(elements.get(i).getElementsByClass("excerpt").text());
                            stackOverflow.setAsked_by(elements.get(i).getElementsByClass("started fr").text());
                            stackOverflow.setVotes(stats.get(i).getElementsByClass("vote-count-post ").text());
                            stackOverflow.setAnswers_count(stats.get(i).getElementsByClass("status answered-accepted").text());
                            stackOverflow.setLink(elements.get(i).getElementsByTag("a").attr("href"));
                            overflows.add(stackOverflow);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return overflows;
        }

        @Override
        protected void onPostExecute(List<StackOverflow> s) {
            super.onPostExecute(s);
            if(s!=null) {
                ResponseModel responseModel = new ResponseModel();
                responseModel.setMine(false);
                responseModel.setResponse(christopher_response);
                responseModel.setCollection(true);
                responseModel.setStackOverflow(true);
                responseModel.setMovie(false);
                responseModel.setStackOverflow(s);
                responseModelList.add(responseModel);
                botAdapter.notifyDataSetChanged();
                if (!isLastVisible())
                    conversation.smoothScrollToPosition(botAdapter.getItemCount() - 1);

                removeLoading();
            }
        }
    }
}
