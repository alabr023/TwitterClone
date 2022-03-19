package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button

    lateinit var client: TwitterClient

    lateinit var counter: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        btnTweet = findViewById(R.id.btnTweet)

        client = TwitterApplication.getRestClient(this)

        counter = findViewById(R.id.counter)
        counter.text = "0/280"

        val etCompose = findViewById(R.id.etTweetCompose) as EditText
        etCompose.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Fires right as the text is being changed (even supplies the range of text)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Fires right before text is changing
            }

            override fun afterTextChanged(s: Editable) {
                // Fires right after the text has changed
                counter.text = "" + s.toString().length + "/" + "280"
                btnTweet.isEnabled = !(s.toString().length > 280 || s.toString().isEmpty())
                if (s.toString().length > 280) {
                    counter.setTextColor(Color.RED)
                }
                else{
                    counter.setTextColor(Color.BLACK)
                }
            }
        })

        btnTweet.setOnClickListener {

            val tweetContent = etCompose.text.toString()

            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweets not allowed!", Toast.LENGTH_SHORT).show()
            }

            else if (tweetContent.length > 280) {
                Toast.makeText(this, "Tweet is too long! Limit is 280 characters", Toast.LENGTH_SHORT).show()
            }
            else {
                client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i(TAG, "Successfully published")

                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()

                    }

                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish tweet", throwable)
                    }

                })
            }

        }
    }
        companion object {
            val TAG = "ComposeActivity"
        }


}