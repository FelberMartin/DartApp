import android.content.Context
import android.util.AttributeSet
import com.example.dartapp.views.chart.CoordinateBasedChart
import com.example.dartapp.views.chart.DataSet

class BarChart(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    CoordinateBasedChart(context, attrs, defStyleAttr) {
    override var data: DataSet
        get() = TODO("Not yet implemented")
        set(value) {}


}