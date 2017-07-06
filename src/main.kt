import kotlin.browser.*
import org.w3c.dom.*
import kotlinx.html.*
import kotlinx.html.dom.*
import kotlinx.html.js.*

data class Stats(val low: Int, val avg: Int, val high: Int, val color: String)

@JsName("tooltipHtmlJs")
fun tooltipHtml (name: String, values: Stats) : HTMLDivElement
{
    return document.create.div {
        h4 { +name }
        table {
            tr {
                td { +"Low" }
                td { +values.low.toString() }
            }
            tr {
                td { +"Avg" }
                td { +values.avg.toString() }
            }
            tr {
                td { +"High" }
                td { +values.high.toString() }
            }
            tr {
                td { +"Variability" }
                td { +variability(values) }
            }
        }
    }
}

fun variability(state: Stats) : String
{
    return when(state.high - state.low)
    {
        in 1 .. 20 -> "Low"
        in 21 .. 50  -> "Medium"
        else -> "High"
    }
}

fun styleTable(state: Stats) = when
{
    (state.high - state.low < 33) && (state.avg < 33) -> "low" // always cold
    (state.high - state.low > 33) && (state.avg > 33) && (state.avg < 66) -> "middle"
    (state.high - state.low < 33) && (state.avg > 66) -> "high" // always hot
    else -> ""
}

fun drawTable(tableDiv: HTMLDivElement, data: dynamic)
{
    val states = js("Object.keys(data);")
    states.sort()

    val table = document.create.table{
        thead {
            tr {
                td { +"Name"}
                td { +"Low"}
                td { +"Average"}
                td { +"High"}
                td { +"Variability"}
            }
        }
    }

    for(i in 1 .. 50)
    {
        var tr = document.create.tr {
            td { +states[i].toString()}
            td { +data[states[i]].low.toString()}
            td { +data[states[i]].avg.toString()}
            td { +data[states[i]].high.toString()}
            td(classes = styleTable(data[states[i]])) { +variability(data[states[i]])}
        }

        table.appendChild(tr)
    }

    tableDiv.appendChild(table)
}

fun main(args: Array<String>) {
    js("""
    var sampleData ={};	/* Sample random data. */
	["HI", "AK", "FL", "SC", "GA", "AL", "NC", "TN", "RI", "CT", "MA",
	"ME", "NH", "VT", "NY", "NJ", "PA", "DE", "MD", "WV", "KY", "OH",
	"MI", "WY", "MT", "ID", "WA", "DC", "TX", "CA", "AZ", "NV", "UT",
	"CO", "NM", "OR", "ND", "SD", "NE", "IA", "MS", "IN", "IL", "MN",
	"WI", "MO", "AR", "OK", "KS", "LS", "VA"]
		.forEach(function(d){
			var low=Math.round(100*Math.random()),
				mid=Math.round(100*Math.random()),
				high=Math.round(100*Math.random());
			sampleData[d]={low:d3.min([low,mid,high]), high:d3.max([low,mid,high]),
					avg:Math.round((low+mid+high)/3), color:d3.interpolate("#0295D5", "#F88909")(low/100)};
		});
    """)

    drawTable(document.getElementById("table") as HTMLDivElement, js("sampleData"))

    js("uStates.draw(\"#statesvg\", sampleData, _.tooltipHtmlJs);")

    val d3_kt: dynamic = js("d3.select(self.frameElement)")
    d3_kt.style("height", "600px")
}