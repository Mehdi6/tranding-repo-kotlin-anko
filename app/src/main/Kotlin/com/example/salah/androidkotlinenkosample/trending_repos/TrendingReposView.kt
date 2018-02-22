package com.example.salah.androidkotlinenkosample.trending_repos

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onScrollChange


data class MyData(val total_count: Int, var incomplete_results: Boolean, val items: List<Map<String, Any?>>)

class TrendingReposView: AnkoComponent<TrendingReposActivity> {
    override fun createView(ui: AnkoContext<TrendingReposActivity>) = with(ui){
            relativeLayout {
                lparams(width = matchParent, height = matchParent)
                val lv = listView().lparams {
                    alignParentTop()
                }

                val repoListingAdapter = RepoListingAdapter(lv)
                lv.adapter = repoListingAdapter

                lv.onScrollChange { _, _, _, _, _ ->
                    if (!lv.canScrollVertically(1) and repoListingAdapter.bl.not()) {
                            repoListingAdapter.nextPage()
                            println("Lunch loading now")
                            repoListingAdapter.bl = true
                    }
                }
            }
        }
}

class RepoListingAdapter(private val lv: ListView) : BaseAdapter() {
    private val list: MutableList<Repo> = MutableList<Repo>(0, {index -> Repo(0L, "","",0,"","")})

    private var gitUrl:String = ""
    private var defaultDate:String= "2018-01-01"
    private var currentPage:Int = 1
    var bl:Boolean = true

    init {
        initDataExtraction()
        extractData()
    }

    private fun initDataExtraction(date: String=this.defaultDate)
    {
        this.defaultDate= date
        this.buildGitUrl(date)
    }

    private fun buildGitUrl(date: String=this.defaultDate, page: Int=this.currentPage){
        this.gitUrl = "https://api.github.com/search/repositories?q=created:>$date&sort=stars&order=desc&page=$page"
    }

    private fun extractData()
    {
        val me = this
        doAsync {
            Fuel.get(me.gitUrl)
                    .responseString { _, response, _ ->

                        val mapper: ObjectMapper = jacksonObjectMapper()

                        val data: MyData = mapper.readValue(src = response.data)
                        uiThread{me.buildRepoCards(data = data)}
                    }
        }
    }
    //Building repoCards based on extracted data from Github
    private fun buildRepoCards(data:MyData) //: ArrayList<Repo>
    {
        val newLstData:MutableList<Repo> = MutableList<Repo>(0, {index -> Repo(0L, "","",0,"","")})

        for (item:Map<String, Any?> in data.items) {
            val name:String? = item["name"] as String?
            val description:String? = item["description"] as String?
            val stargazersCount:Int? = item["stargazers_count"] as Int?
            val ownerDes:Map<String, Any?> = item["owner"] as Map<String, Any?>

            val ownerLogin:String? = ownerDes["login"] as String?
            val ownerAvatarUrl:String? = ownerDes["avatar_url"] as String?

            val newRepoCard = Repo(java.util.Random().nextLong(), name= name, description=description, stargazers_count = stargazersCount,
                    owner_login = ownerLogin, owner_avatar_url = ownerAvatarUrl)
            newLstData.add(newRepoCard)
        }

        this.bl = false
        this += newLstData
    }
    fun nextPage()
    {
        this.currentPage++
        this.buildGitUrl()
        this.extractData()
    }

    override fun getView(position: Int, p1: View?, parent: ViewGroup?): View {
        val repo = getItem(position)
        return with(parent!!.context){
            linearLayout {
                orientation = LinearLayout.VERTICAL
                id = java.util.Random().nextInt()
                textView {
                    id = java.util.Random().nextInt()
                    text = repo.name
                    textSize = 24F
                }
                textView {
                    id = java.util.Random().nextInt()
                    text = repo.description
                    textSize = 18F
                }

                linearLayout {
                    val avatar = imageView() {
                        id = java.util.Random().nextInt()

                        //padding = dip(5)
                        //margin = dip(5)
                    }.lparams(width = 150 ,height = 150)

                    Picasso.with(this@with.ctx)
                            .load(repo.owner_avatar_url)
                            .into(avatar)
                    textView {
                        id = java.util.Random().nextInt()
                        text = repo.owner_login
                        textSize = 16F
                    }

                    textView {
                        id = java.util.Random().nextInt()
                        text = "★ ${repo.stargazers_count}k"
                        textSize = 16F
                        //padding = dip(5)
                        leftPadding = dip(100)
                    }
                }
            }
        }
    }

    override fun getItem(position: Int): Repo = list[position]

    override fun getItemId(position: Int): Long = getItem(position).id

    override fun getCount(): Int = list.size

    private operator fun plusAssign(listRepo: MutableList<Repo>) {
        list.addAll(listRepo)
        println("+ list: ${list.size}")
        notifyDataSetChanged()
        lv.invalidate()
    }
    operator fun plusAssign(repo: Repo) {
        list.add(repo)
        println("+ repo ${list.size}")
        notifyDataSetChanged()
        lv.invalidate()
    }
}

data class Repo(val id: Long, val owner_avatar_url: String?, val owner_login: String?, val stargazers_count: Int?, val description: String?
               , val name: String?){
    override fun toString() :String{
        return "Name: $name; Description: $description; Stars: $stargazers_count; Owner: avatar_url: $owner_avatar_url; Login: $owner_login"
    }

}