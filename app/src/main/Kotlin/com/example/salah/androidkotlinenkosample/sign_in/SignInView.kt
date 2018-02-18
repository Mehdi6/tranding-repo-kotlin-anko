package com.example.salah.androidkotlinenkosample.sign_in

import android.graphics.Path
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListView
import com.example.salah.androidkotlinenkosample.R
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.contentFrameLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onScrollChange
import java.util.*


data class MyData(val total_count: Int, var incomplete_results: Boolean, val items: List<Map<String, Any?>>)

class SignInView: AnkoComponent<SignInActivity> {
    fun addRepoCard(name: String){

    }
    override fun createView(ui: AnkoContext<SignInActivity>) = with(ui){
                relativeLayout {
                    lparams(width = matchParent, height = matchParent)
                    val lv = listView().lparams{
                        alignParentTop()
                    }
                    /*
                    val lstRepos = listOf<Repo>(Repo(java.util.Random().nextLong(), "blabl", "login", 10, "description", "name"),
                            Repo(java.util.Random().nextLong(), "blabl", "login", 10, "description", "name"),
                            Repo(java.util.Random().nextLong(), "blabl", "login", 10, "description", "name"),
                            Repo(java.util.Random().nextLong(), "blabl", "login", 10, "description", "name"),
                            Repo(java.util.Random().nextLong(), "blabl", "login", 10, "description", "name"),
                            Repo(java.util.Random().nextLong(), "blabl", "login", 10, "description", "name"),
                            Repo(java.util.Random().nextLong(), "blabl", "login", 10, "description", "name"))
                            */
                    val poleAd = PoleAdapter(lv)
                    //poleAd += Repo(java.util.Random().nextLong(), "blabl", "login", 10, "description", "name")
                    poleAd.initDataExtraction()
                    poleAd.extractData()
                    lv.adapter = poleAd

                    button {
                        id = java.util.Random().nextInt()
                        text = "add"
                        onClick{
                            poleAd += Repo(java.util.Random().nextLong(), "blabl", "login", 10, "description", "name")
                        }
                    }

                    lv.onScrollChange {
                        _, _, _, _, _ ->
                        if (!lv.canScrollVertically(1) and !poleAd.bl){
                            poleAd.bl = true
                            poleAd.nextPage()
                            println("Lunch loading now")
                        }
                    }
                }
        }
}


class PoleAdapter(private val lv: ListView) : BaseAdapter() {
    var list: MutableList<Repo> = MutableList<Repo>(0, {index -> Repo(0L, "","",0,"","")})

    var Data:ArrayList<Repo> = ArrayList()
    private var gitUrl:String = ""
    private var defaultDate:String= "2018-01-01"
    private var currentPage:Int = 1
    var bl:Boolean = true

    fun initDataExtraction(date: String=this.defaultDate)
    {
        this.defaultDate= date
        this.buildGitUrl(date)
    }

    fun buildGitUrl(date: String=this.defaultDate, page: Int=this.currentPage){
        this.gitUrl = "https://api.github.com/search/repositories?q=created:>$date&sort=stars&order=desc&page=$page"
    }

    fun extractData()
    {
        Fuel.get(this.gitUrl)
                .responseString { _, response, result ->

                    val mapper: ObjectMapper = jacksonObjectMapper()

                    var data:MyData = mapper.readValue(src = response.data)
                    this += this.buildRepoCards(data = data)
                    this.bl = false

                    val (bytes, error) = result
                    if (bytes != null) {
                        //println(bytes)
                        //println(error)
                    }
                    //Thread.sleep(500)

                }
    }
    //Building repoCards based on extracted data from Github
    fun buildRepoCards(data:MyData) : ArrayList<Repo>
    {
        val mapper:ObjectMapper = jacksonObjectMapper()

        val numberItems:Int = data.items.size
        //this.Data = ArrayList<RepoCard>(initialCapacity = numberItems)
        //println(numberItems)
        val newLstData:ArrayList<Repo> = ArrayList<Repo>()

        for (item:Map<String, Any?> in data.items) {

            val name:String? = item.get("name") as String?
            val description:String? = item.get("description") as String?
            val stargazersCount:Int? = item.get("stargazers_count") as Int?
            //println("Name: $name, Description: $description, stars: $stargazersCount")
            val ownerDes:Map<String, Any?> = item["owner"] as Map<String, Any?>

            val ownerLogin:String? = ownerDes["login"] as String?
            val ownerAvatarUrl:String? = ownerDes["avatar_url"] as String?

            val newRepoCard: Repo = Repo(java.util.Random().nextLong(), name= name, description=description, stargazers_count = stargazersCount,
                    owner_login = ownerLogin, owner_avatar_url = ownerAvatarUrl)
            newLstData.add(newRepoCard)
        }
        this.Data.addAll(newLstData)
        return newLstData
    }
    fun nextPage()
    {
        this.currentPage++
        this.buildGitUrl()
        this.extractData()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val repo = getItem(p0)
        return with(p2!!.context){
            linearLayout {
                orientation = LinearLayout.VERTICAL
                id = java.util.Random().nextInt()
                textView {
                    text = repo.name
                    textSize = 24F
                }
                textView {
                    text = repo.description
                    textSize = 18F
                }

                linearLayout {
                    val avatar = imageView() {
                        //id = R.id.imageId
                        //padding = dip(5)
                        //margin = dip(5)
                    }
                    Picasso.with(this@with.ctx)
                            .load(repo.owner_avatar_url)
                            .into(avatar)
                    textView {
                        //id = R.id.labelText
                        text = repo.owner_login
                        textSize = 16F
                    }

                    textView {
                        //id = R.id.stars
                        text = "★ ${repo.stargazers_count}k"
                        textSize = 16F
                        //padding = dip(5)
                        leftPadding = dip(100)
                    }
                }
            }
        }
    }

    override fun getItem(p0: Int): Repo = list[p0]

    override fun getItemId(p0: Int): Long = getItem(p0).id

    override fun getCount(): Int = list.size

    operator fun plusAssign(listRepo: MutableList<Repo>) {
        listRepo.forEach{repo -> list.add(repo)}
        notifyDataSetChanged()
        lv.invalidate()
    }
    operator fun plusAssign(repo: Repo) {
        list.add(repo)
        println(list.size)
        notifyDataSetChanged()
        lv.invalidate()
    }
}

class Repo(val id: Long, val owner_avatar_url: String?, val owner_login: String?, val stargazers_count: Int?, val description: String?
               , val name: String?){
    override fun toString() :String{
        return "Name: $name; Description: $description; Stars: $stargazers_count; Owner: avatar_url: $owner_avatar_url; Login: $owner_login"
    }

}