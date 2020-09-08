package com.aek.imagesharefirebase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aek.imagesharefirebase.model.Post
import com.aek.imagesharefirebase.R
import com.aek.imagesharefirebase.adapter.FeedRecyclerAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var recyclerViewAdapter:FeedRecyclerAdapter

    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val actionBar = supportActionBar
        actionBar?.let { bar ->
            bar.title = "Feed"
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        getFirestoreData()

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = FeedRecyclerAdapter(postList)
        recyclerView.adapter = recyclerViewAdapter
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.options_menu_share_photo -> {
                val intent = Intent(this, ShareActivity::class.java)
                startActivity(intent)
            }
            R.id.options_menu_logout -> {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()

            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun getFirestoreData() {

        database.collection("Post").orderBy(ShareActivity.keyDate, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (snapshot != null && !snapshot.isEmpty) {
                        val documents = snapshot.documents
                        postList.clear()
                        for (doc in documents) {
                            val email = doc.get(ShareActivity.keyEmail) as String
                            val imageUrl = doc.get(ShareActivity.keyImageUrl) as String
                            val comment = doc.get(ShareActivity.keyComment) as String
                            val date = doc.get(ShareActivity.keyDate) as Timestamp

                            postList.add(Post(email, comment, imageUrl, date))

                        }

                        recyclerViewAdapter.notifyDataSetChanged()

                    }
                }

            }

    }
}