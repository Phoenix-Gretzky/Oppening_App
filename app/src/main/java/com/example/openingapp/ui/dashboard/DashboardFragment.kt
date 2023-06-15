package com.example.openingapp.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.adapters.ImageViewBindingAdapter.setImageDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.openingapp.Adapters.LinksRecyclerAdapter
import com.example.openingapp.GlobalClasses.GlobalClassForFunctions
import com.example.openingapp.Models.BaseDataClass
import com.example.openingapp.Models.Links
import com.example.openingapp.NetworkCalls.AsyncTasks
import com.example.openingapp.databinding.ActivityMain2Binding
import com.example.openingapp.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DashboardFragment(main2Binding: ActivityMain2Binding) : Fragment() {

    lateinit var binding: FragmentDashboardBinding;
    lateinit var chart: LineChart;
    var main2Binding: ActivityMain2Binding
    var tabSelected = 0;

    init {
        this.main2Binding = main2Binding;
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    lateinit var mcontext: Context;
    lateinit var dashboardViewModel: DashboardViewModel;

    lateinit var linksData: BaseDataClass;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        mcontext = binding.root.context;

        GlobalClassForFunctions.getInstance().launchBasicGlobalFunctions(mcontext)
        main2Binding.floatingBtn.bringToFront();
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setPropertiesForFragment();
        chart = binding.chart;

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }

    private fun setPropertiesForFragment() {


        initializeViewModel()
        SetViewProperties()
    }


    private fun initializeViewModel() {
        dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

    }

    //region set view properties
    private fun SetViewProperties() {
        try {


            ObserveViewModel()

            // Set Text To The Views In The Layout
            SetTextToTheViewsInTheLayout()

            // set size of the view
            SetViewSizeOfTheWidgetsInTheLayout()

            // lOAD Images into the view
            LoadImagesIntoTheViewInTheWidgets()

            // set color to the view in the layout
            SetColorToTheViewsInTheLayout()
            //set tab buttons
            setTabButtons()

            // set background to the view in the properties
            SetBackgroundToTheViewsInTheLayout()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun ObserveViewModel() {
        try {
            //
            dashboardViewModel.observableLiveDataForLinksApi.observe(viewLifecycleOwner) {

                linksData = it;
                println("this is data " + linksData);

                if (tabSelected == 0)
                    setReyclerForLinks(linksData.data!!.topLinks)

                if (tabSelected == 1)
                    setReyclerForLinks(linksData.data!!.recentLinks)


                if (it.jsonObjectForChart != null)
                    setChartData(it.jsonObjectForChart!!);


                var text = "To Be Set"

                binding.todaysClicks.value.text = "" + linksData.todayClicks;
                binding.topLocation.value.text =
                    if (!linksData.topLocation.equals("")) linksData.topLocation else text
                binding.topSource.value.text =
                    if (!linksData.topSource.equals("")) linksData.topSource else text
                binding.bestTime.value.text = text

                chart.postDelayed(Runnable {
                    GlobalClassForFunctions.getInstance().hideProgresDialogue(mcontext);
                }, 1000)
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun formatDates(dateArray: ArrayList<String>): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Sort the date array in ascending order
        val sortedDates = dateArray.sortedBy { dateFormat.parse(it) }

        // Get the first and last dates from the sorted array
        val firstDate = sortedDates.firstOrNull()
        val lastDate = sortedDates.lastOrNull()

        // Format the first and last dates
        val formattedFirstDate = formatDate(firstDate)
        val formattedLastDate = formatDate(lastDate)

        // Return the formatted date range
        return "$formattedFirstDate - $formattedLastDate"
    }

    fun formatDate(date: String?): String {
        if (date == null) return ""

        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

        val parsedDate = inputFormat.parse(date)
        return outputFormat.format(parsedDate ?: Date())
    }

    private fun setChartData(jsonObjectForChart: JSONObject) {
        try {
            val jsonObject = jsonObjectForChart
            val entries: MutableList<Entry> = ArrayList()
            val keys: MutableIterator<String> = jsonObject.keys()
            var dates: ArrayList<String> = ArrayList();
            var maxYValue = Float.MIN_VALUE
            var index = 0
            while (keys.hasNext()) {
                val key = keys.next()
                dates.add(key);
                val value: Int = jsonObject.getInt(key)
                if (value > maxYValue) {
                    maxYValue = value.toFloat()
                }
                entries.add(Entry(index.toFloat(), value.toFloat()))
                index++
            }

            //setting the Textview For selected dates

            binding.dateFilterTextview.text = formatDates(dates);

            // Create a dataset with the entries
            val dataSet = LineDataSet(entries, "Data")
            dataSet.setDrawFilled(true)
            dataSet.setDrawValues(false)
            dataSet.setDrawCircleHole(false)
            dataSet.setDrawCircles(false)
            dataSet.lineWidth = 2F
            dataSet.color = Color.parseColor("#0e6fff")
            dataSet.fillDrawable =
                resources.getDrawable(com.example.openingapp.R.drawable.gradient_bg_for_graph) // Use the gradient drawable

            // Set up the chart data and appearance
            val lineData = LineData(dataSet)
            chart.setData(lineData)
            chart.getDescription().setEnabled(false)
            chart.getLegend().setEnabled(false)
            chart.extraTopOffset = 50F
            chart.extraBottomOffset = 20F
            chart.isHighlightPerTapEnabled = false
            chart.isDoubleTapToZoomEnabled = false
            chart.isHighlightPerDragEnabled = false


            val xAxis: XAxis = chart.getXAxis()
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.labelRotationAngle = 90F
            xAxis.valueFormatter = IndexAxisValueFormatter(dates)


            val yAxis: YAxis = chart.getAxisLeft()
            yAxis.axisMaximum = maxYValue
            yAxis.axisMinimum = 0F;
            yAxis.setDrawGridLinesBehindData(true)
            chart.getAxisRight().setEnabled(false)
            chart.invalidate()

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        }

        private fun setReyclerForLinks(links: ArrayList<Links>) {
            val lm: LinearLayoutManager = object : LinearLayoutManager(mcontext) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            //stopping the scroll of recycler view so that we can view all after clicking on the view all button
            binding.linksRecycler.layoutManager = lm;

            binding.linksRecycler.adapter = LinksRecyclerAdapter(mcontext, links);
        }


        //region set size of the view
        private fun SetViewSizeOfTheWidgetsInTheLayout() {
            try {
                //set text font
                var global = GlobalClassForFunctions.getInstance();

                global.setViewSize(
                    -2,
                    binding.textDashboard,
                    binding.nameTextView,
                )
                global.setViewSize(
                    -1,
                    binding.todaysClicks.key,
                    binding.todaysClicks.value,
                    binding.topLocation.key,
                    binding.topLocation.value,
                    binding.topSource.key,
                    binding.topSource.value,
                    binding.bestTime.key,
                    binding.bestTime.value,

                    )
                global.setViewSize(
                    1,
                    binding.greetingsTextview,
                    binding.AnalyticsImageTextBtn.text,
                    binding.viewLinksImageTextBtn.text,
                    binding.TalkWithUsImageTextBtn.text,
                    binding.FAQImageTextBtn.text,
                    binding.chartHeading
                )
                global.setViewSize(1, binding.tab1, binding.tab2)



                global.setImageViewSize(binding.imageView, 3, 0)
                global.setImageViewSize(binding.searchBtn, 3, 0)


                global.setImageViewSize(binding.AnalyticsImageTextBtn.image, 2, 0)
                global.setImageViewSize(binding.viewLinksImageTextBtn.image, 2, 0)
                global.setImageViewSize(binding.TalkWithUsImageTextBtn.image, 2, 0)
                global.setImageViewSize(binding.FAQImageTextBtn.image, 2, 0)


                global.setImageViewSize(binding.todaysClicks.image.imageViewforHeading, 1, 0)
                global.setImageViewSize(binding.topLocation.image.imageViewforHeading, 1, 0)
                global.setImageViewSize(binding.topSource.image.imageViewforHeading, 1, 0)
                global.setImageViewSize(binding.bestTime.image.imageViewforHeading, 1, 0)


            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        //endregion

        private fun setGreetingBasedOnTime(): String {
            val c: Calendar = Calendar.getInstance()
            val timeOfDay: Int = c.get(Calendar.HOUR_OF_DAY)

            if (timeOfDay >= 0 && timeOfDay < 12) {
                return "Good Morning";
            } else if (timeOfDay >= 12 && timeOfDay < 16) {
                return "Good Afternoon"
            } else if (timeOfDay >= 16 && timeOfDay < 21) {
                return "Good Evening"
            } else {
                return "Good Night"
            }
        }

        //region Set Text To The Views In The Layout
        private fun SetTextToTheViewsInTheLayout() {
            try {

                binding.textDashboard.text = "Dashboard"

                binding.greetingsTextview.text = setGreetingBasedOnTime();
                binding.tab1.text = "Top Links"
                binding.tab2.text = "Recent Links"

                binding.nameTextView.text = "Ajay Manva \u270B"

                binding.chartHeading.text = "Overview"

                binding.AnalyticsImageTextBtn.text.text = "View Analytics";

                binding.viewLinksImageTextBtn.text.text = "View All Links"

                binding.TalkWithUsImageTextBtn.text.text = "Talk with us"

                binding.FAQImageTextBtn.text.text = "Frequently Asked Questions"


                binding.todaysClicks.key.text = "Today's clicks"
                binding.topLocation.key.text = "Top Location"
                binding.topSource.key.text = "Top source"
                binding.bestTime.key.text = "Best Time"


            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        //endregion


        //region set background to the view in the properties
        private fun SetBackgroundToTheViewsInTheLayout() {

            try {

                var global = GlobalClassForFunctions.getInstance()

                global.setConstraintLayoutProperties(
                    binding.imageView,
                    Color.parseColor("#2b80ff"),
                    30F,
                    0,
                    0,
                    0,
                    0
                )
                global.setConstraintLayoutProperties(
                    binding.MainContentConstraint,
                    Color.parseColor("#f5f5f5"),
                    60F,
                    0,
                    0,
                    1,
                    0
                )
                global.setConstraintLayoutProperties(
                    binding.chartConstraint,
                    Color.WHITE,
                    20F,
                    0,
                    0,
                    0,
                    0
                )
                global.setConstraintLayoutProperties(
                    binding.AnalyticsLayout,
                    0,
                    30F,
                    0,
                    3,
                    0,
                    Color.parseColor("#dddddd")
                )
                global.setConstraintLayoutProperties(
                    binding.ViewLinksLayout,
                    0,
                    30F,
                    0,
                    3,
                    0,
                    Color.parseColor("#dddddd")
                )
                global.setConstraintLayoutProperties(
                    binding.searchBtn,
                    0,
                    30F,
                    0,
                    3,
                    0,
                    Color.parseColor("#dddddd")
                )
                global.setConstraintLayoutProperties(
                    binding.TalkWithUsLayout,
                    Color.parseColor("#e0f0e2"),
                    30F,
                    0,
                    3,
                    0,
                    Color.parseColor("#b0e6b8")
                )
                global.setConstraintLayoutProperties(
                    binding.FAQLayout,
                    Color.parseColor("#e8f1ff"),
                    30F,
                    0,
                    3,
                    0,
                    Color.parseColor("#a2c7ff")
                )

                global.setConstraintLayoutProperties(
                    binding.dateFilterLayout,
                    0,
                    20F,
                    0,
                    3,
                    0,
                    Color.parseColor("#999ca0")
                )


            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        //endregion


        //region lOAD Images into the view
        private fun LoadImagesIntoTheViewInTheWidgets() {
            try {


                binding.imageView.setImageDrawable(mcontext.getDrawable(com.example.openingapp.R.drawable.setting_bolt_ic));
                binding.searchBtn.setImageDrawable(mcontext.getDrawable(com.example.openingapp.R.drawable.search_2_ic));
                binding.AnalyticsImageTextBtn.image.setImageDrawable(mcontext.getDrawable(com.example.openingapp.R.drawable.trending_ic));
                binding.viewLinksImageTextBtn.image.setImageDrawable(mcontext.getDrawable(com.example.openingapp.R.drawable.hyperlink_ic));
                binding.TalkWithUsImageTextBtn.image.setImageDrawable(mcontext.getDrawable(com.example.openingapp.R.drawable.whatsapp_ic));
                binding.FAQImageTextBtn.image.setImageDrawable(mcontext.getDrawable(com.example.openingapp.R.drawable.question_ic));

                binding.todaysClicks.image.imageViewforHeading.setImageDrawable(
                    mcontext.getDrawable(
                        com.example.openingapp.R.drawable.pointer_ic
                    )
                )
                binding.topLocation.image.imageViewforHeading.setImageDrawable(
                    mcontext.getDrawable(
                        com.example.openingapp.R.drawable.location_pin_2_ic
                    )
                )
                binding.topSource.image.imageViewforHeading.setImageDrawable(
                    mcontext.getDrawable(
                        com.example.openingapp.R.drawable.global_ic
                    )
                )
                binding.bestTime.image.imageViewforHeading.setImageDrawable(mcontext.getDrawable(com.example.openingapp.R.drawable.clock_ic))

                GlobalClassForFunctions.getInstance().setConstraintLayoutProperties(
                    binding.todaysClicks.image.linearLayout,
                    Color.parseColor("#ebe6f9"), 100F, 0, 0, 0, 0
                )
                GlobalClassForFunctions.getInstance().setConstraintLayoutProperties(
                    binding.topLocation.image.linearLayout,
                    Color.parseColor("#e2edff"), 100F, 0, 0, 0, 0
                )
                GlobalClassForFunctions.getInstance().setConstraintLayoutProperties(
                    binding.topSource.image.linearLayout,
                    Color.parseColor("#ffe9ec"), 100F, 0, 0, 0, 0
                )
                GlobalClassForFunctions.getInstance().setConstraintLayoutProperties(
                    binding.bestTime.image.linearLayout,
                    Color.parseColor("#fcf2dd"), 100F, 0, 0, 0, 0
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        //endregion


        // set color to the view in the layout
        private fun SetColorToTheViewsInTheLayout() {
            try {


                binding.textDashboard.setTextColor(Color.WHITE)
                binding.greetingsTextview.setTextColor(Color.parseColor("#999ca0"));
                binding.chartHeading.setTextColor(Color.parseColor("#999ca0"));
                binding.nameTextView.setTextColor(Color.BLACK)
                binding.AnalyticsImageTextBtn.text.setTextColor(Color.BLACK)
                binding.viewLinksImageTextBtn.text.setTextColor(Color.BLACK)
                binding.TalkWithUsImageTextBtn.text.setTextColor(Color.BLACK)
                binding.FAQImageTextBtn.text.setTextColor(Color.BLACK)



                binding.imageView.setColorFilter(Color.WHITE)
                binding.searchBtn.setColorFilter(Color.GRAY)
                binding.AnalyticsImageTextBtn.image.setColorFilter(Color.BLACK)
                binding.viewLinksImageTextBtn.image.setColorFilter(Color.BLACK)
                binding.TalkWithUsImageTextBtn.image.setColorFilter(Color.parseColor("#52d366"))
                binding.FAQImageTextBtn.image.setColorFilter(Color.parseColor("#1070ff"))

                binding.todaysClicks.image.imageViewforHeading.setColorFilter(Color.parseColor("#5c33cf"))
                binding.topLocation.image.imageViewforHeading.setColorFilter(Color.parseColor("#287eff"))
                binding.topSource.image.imageViewforHeading.setColorFilter(Color.parseColor("#ff4e64"))
                binding.bestTime.image.imageViewforHeading.setColorFilter(Color.parseColor("#ffb829"))


            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        // endregion


        //set buttons layout
        private fun setTabButtons() {
            try {

                var SelectedPosition = 0;
                //set properties
                /*   GlobalClassForFunctions.getInstance().setConstraintLayoutProperties(tabHeadingLayout.textViewForHeading1,Color.parseColor("#FFFFFF"),500f,
                       CConstants.getInt_NoShape(),2,0,Color.parseColor("#0052A8"))
                   tabHeadingLayout.textViewForHeading1.setTextColor(Color.parseColor("#0052A8"))
                   tabHeadingLayout.textViewForHeading1.setText(lstFilterTabs.get(i))
    */

                binding.tab1.setOnClickListener(View.OnClickListener {
                    SelectedPosition = 0;
                    GlobalClassForFunctions.getInstance().setConstraintLayoutProperties(
                        it,
                        Color.parseColor("#0e6fff"),
                        100F,
                        0,
                        0,
                        0,
                        0
                    )
                    GlobalClassForFunctions.getInstance().setConstraintLayoutProperties(
                        binding.tab2,
                        Color.TRANSPARENT,
                        100F,
                        0,
                        0,
                        0,
                        0
                    )
                    (it as TextView).setTextColor(Color.WHITE)
                    binding.tab2.setTextColor(Color.LTGRAY)
                    binding.tab1.highlightColor = Color.parseColor("#0e6fff");
                    tabSelected = SelectedPosition;
                    apiCall()

                });

                binding.tab2.setOnClickListener(View.OnClickListener {
                    SelectedPosition = 1;
                    GlobalClassForFunctions.getInstance().setConstraintLayoutProperties(
                        it,
                        Color.parseColor("#0e6fff"),
                        100F,
                        0,
                        0,
                        0,
                        0
                    )
                    GlobalClassForFunctions.getInstance().setConstraintLayoutProperties(
                        binding.tab1,
                        Color.TRANSPARENT,
                        100F,
                        0,
                        0,
                        0,
                        0
                    )
                    (it as TextView).setTextColor(Color.WHITE)
                    binding.tab1.setTextColor(Color.LTGRAY)
                    tabSelected = SelectedPosition;
                    apiCall()

                });

                binding.tab1.performClick();
                //endregion


            } catch (exception: java.lang.Exception) {
                exception.printStackTrace()
            }
        }

        private fun apiCall() {
            AsyncTasks.AsynTaskForGettingBaseData(mcontext, dashboardViewModel).execute();
        }


    }