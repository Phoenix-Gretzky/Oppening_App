package com.example.openingapp.Adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.openingapp.GlobalClasses.GlobalClassForFunctions
import com.example.openingapp.Models.Links
import com.example.openingapp.R
import com.example.openingapp.databinding.TileForLinksBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class LinksRecyclerAdapter(context: Context, linksLinks: MutableList<Links>) :
    RecyclerView.Adapter<LinksRecyclerAdapter.ViewHolder>() {
    private val mContext: Context
    private val LinksList: MutableList<Links>

    /**
     * Constructs a new [LinksRecyclerAdapter]
     * @param context of the app
     * @param linksLinks is the list of Links, which is the data source of the adapter
     */
    init {
        mContext = context
        LinksList = linksLinks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding: TileForLinksBinding =
            TileForLinksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return LinksList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // this function does the job of setting each tile I have made this to use binding easily
        holder.bindTo(position)

/*

        // Set an OnClickListener to open a website with more information about the selected Links
        holder.cardView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                val newsUri: Uri = Uri.parse(currentLink.url)

                // Create a new intent to view the Links URI
                val websiteIntent = Intent(Intent.ACTION_VIEW, newsUri)
                websiteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Send the intent to launch a new activity
                mContext.startActivity(websiteIntent)
            }
        })
*/

    }


    inner class ViewHolder(binding: TileForLinksBinding) : RecyclerView.ViewHolder(binding.root) {
        var binding: TileForLinksBinding

        init {
            this.binding = binding;
        }

        fun bindTo(position: Int) {

            var currentLink = LinksList[position]
            setColor(binding, currentLink)
            setTextSize(binding, currentLink)
            setImages(binding, currentLink)
            setTexts(binding,currentLink)
            setConstraintProperties(binding, currentLink)
        }

    }

    private fun setConstraintProperties(binding: TileForLinksBinding, currentLink: Links) {

        var gradientDrawable : GradientDrawable= GradientDrawable();
        gradientDrawable.cornerRadii = floatArrayOf(0f,0f,0f,0f,30F,30F,30F,30F)
        gradientDrawable.setColor(Color.parseColor("#e8f1ff"))
        gradientDrawable.setStroke(5, Color.parseColor("#a6c7ff") , 10F, 8F);
        binding.linkConstraint.background =gradientDrawable;


    }

    private fun setTexts(binding: TileForLinksBinding, currentLink: Links) {

        binding.linkHeading.text=currentLink.app;
        binding.clicksText.text="Clicks"
        binding.clicksInt.text=currentLink.totalClicks.toString();
        binding.linkUrl.text=currentLink.webLink;
        binding.date.text=formatDate(currentLink.createdAt);
    }

    private fun setImages(binding: TileForLinksBinding, currentLink: Links) {
        if (currentLink.originalImage == null) {
            binding.linkImage.visibility = View.GONE
        } else {
            binding.linkImage.visibility = View.VISIBLE
            // Load image with glide
            Glide.with(mContext.applicationContext)
                .load(currentLink.originalImage)
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.error_icon)
                .into(binding.linkImage)
        }

        binding.copyUrlIcon

        Glide.with(mContext.applicationContext)
            .load(R.drawable.copy_ic)
            .placeholder(R.drawable.image_loading)
            .error(R.drawable.error_icon)
            .into(binding.copyUrlIcon)



        GlobalClassForFunctions.getInstance().setImageViewSize(binding.linkImage,4,0)
        GlobalClassForFunctions.getInstance().setImageViewSize(binding.copyUrlIcon,1,0)

        binding.copyUrlIcon.setColorFilter(Color.parseColor("#448fff"));

        GlobalClassForFunctions.getInstance().setConstraintLayoutProperties(binding.linkImage,
        Color.TRANSPARENT, 10F,0,5,0,Color.LTGRAY);

    }

    private fun setColor(binding: TileForLinksBinding, currentLink: Links) {
        binding.linkHeading.setTextColor(Color.parseColor("#0b0b0b"));
        binding.clicksInt.setTextColor(Color.parseColor("#0b0b0b"));
        binding.clicksText.setTextColor(Color.parseColor("#a5a8ac"));
        binding.date.setTextColor(Color.parseColor("#a5a8ac"));
        binding.linkUrl.setTextColor(Color.parseColor("#0e6fff"));

    }


    private fun setTextSize(binding: TileForLinksBinding, currentLink: Links) {

        GlobalClassForFunctions.getInstance().setViewSize(1,binding.linkHeading)
        GlobalClassForFunctions.getInstance().setViewSize(1,binding.clicksInt)
        GlobalClassForFunctions.getInstance().setViewSize(2,binding.clicksText)
        GlobalClassForFunctions.getInstance().setViewSize(2,binding.date)

    }


    /**
     * Clear all data (a list of [Links] objects)
     */
    fun clearAll() {
        LinksList.clear()
        notifyDataSetChanged()
    }

    /**
     * Add  a list of [Links]
     * @param linksLinks is the list of Links, which is the data source of the adapter
     */
    fun addAll(linksLinks: List<Links>?) {
        LinksList.clear()
        LinksList.addAll(linksLinks!!)
        notifyDataSetChanged()
    }

    /**
     * Convert date and time in UTC (webPublicationDate) into a more readable representation
     * in Local time
     *
     * @param dateStringUTC is the web publication date of the Links (i.e. 2014-02-04T08:00:00.000Z)
     * @return the formatted date string in Local time(i.e "1 Jan 2022")
     * from a date and time in UTC
     */
    private fun formatDate(dateStringUTC: String?): String {
        // Parse the dateString into a Date object
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
        var dateObject: Date? = null
        try {
            dateObject = simpleDateFormat.parse(dateStringUTC)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        // Initialize a SimpleDateFormat instance and configure it to provide a more readable
        // representation according to the given format, but still in UTC
        val df = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val formattedDateUTC: String = df.format(dateObject)
        // Convert UTC into Local time
        df.setTimeZone(TimeZone.getTimeZone("UTC"))
        var date: Date? = null
        try {
            date = df.parse(formattedDateUTC)
            df.setTimeZone(TimeZone.getDefault())
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return df.format(date)
    }



}
