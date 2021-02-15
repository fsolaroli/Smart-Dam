function drawGraph(data) {

	// set the dimensions and margins of the graph
	var margin = {top: 20, right: 20, bottom: 30, left: 40},
	width = window.innerWidth - margin.left - margin.right,
	height = 500 - margin.top - margin.bottom;
	
	// parse the date / time
	var parseTime = d3.timeParse("%Y-%m-%dT%H:%M:%S");

	// format the data
	data.forEach(function(d) {
		d.time = parseTime(d.time);
		d.value = +d.value;
	});
	
	// set the ranges
	var x = d3.scaleTime().range([0, width]);
	var y = d3.scaleLinear().range([height, 0]);
	
	// Scale the range
	x.domain(d3.extent(data, function(d) { return d.time; }));
	y.domain([0, 5.5]);
	
	// define the line
	var valueline = d3.line()
		.x(function(d) { return x(d.time); })
		.y(function(d) { return y(d.value); });

	var errorArea = d3.area()
		.x(function(d) { return x(d.time); })
		.y0(function(d) { return y(4.60); })
		.y1(function(d) { return y(5.5); });
	
	var preErrorArea = d3.area()
		.x(function(d) { return x(d.time); })
		.y0(function(d) { return y(4); })	
		.y1(function(d) { return y(4.60); });

	var normalArea = d3.area()
		.x(function(d) { return x(d.time); })
		.y0(function(d) { return y(0); })
		.y1(function(d) { return y(4); })
	
	// append the svg obgect to the body of the page
	// appends a 'group' element to 'svg'
	// moves the 'group' element to the top left margin
	var svg = d3.select("#graph").append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
		.append("g")
		.attr("transform",
			"translate(" + margin.left + "," + margin.top + ")");
	
	//Add the level areas
	svg.append("path")
		.data([data])
		.style("fill", "red")
		.attr("opacity", 0.50)
		.attr("d", errorArea);
		svg.append("path")
		.data([data])
		.style("fill", "orange")
		.attr("opacity", 0.50)
		.attr("d", preErrorArea);
		svg.append("path")
		.data([data])
		.style("fill", "green")
		.attr("opacity", 0.50)
		.attr("d", normalArea);

	// Add the valueline path.
	svg.append("path")
		.data([data])
		.attr("class", "line")
		.attr("d", valueline);
 
	svg.selectAll("myCircles")
	.data(data)
	.enter()
	.append("circle")
		.attr("fill", "steelblue")
		.attr("stroke", "none")
		.attr("cx", function(d) { return x(d.time) })
		.attr("cy", function(d) { return y(d.value) })
		.attr("r", 3)

	// Add the X Axis
	svg.append("g")
		.attr("transform", "translate(0," + height + ")")
		.call(d3.axisBottom(x).ticks(20));

	// Add the Y Axis
	svg.append("g")
		.call(d3.axisLeft(y));


	// Mouse interaction
	// This allows to find the closest X index of the mouse:
	var bisect = d3.bisector(function(d) { return d.time; }).left;

	// Create the circle that travels along the curve of chart
	var focus = svg
	.append('g')
	.append('circle')
		.style("fill", "none")
		.attr("stroke", "steelblue")
		.attr('r', 4)
		.style("opacity", 0)

	// Create the text that travels along the curve of chart
	var focusText = svg
	.append('g')
	.append('text')
		.style("opacity", 0)
		.attr("text-anchor", "left")
		.attr("alignment-baseline", "middle")

	  // Create a rect on top of the svg area: this rectangle recovers mouse position
	  svg
	  .append('rect')
	  .style("fill", "none")
	  .style("pointer-events", "all")
	  .attr('width', width)
	  .attr('height', height)
	  .on('mouseover', mouseover)
	  .on('mousemove', mousemove)
	  .on('mouseout', mouseout);
  
	// What happens when the mouse move -> show the annotations at the right positions.
	function mouseover() {
	  focus.style("opacity", 1)
	  focusText.style("opacity",1)
	}
  
	function mousemove() {
	  // recover coordinate we need
	  var x0 = x.invert(d3.mouse(this)[0]);
	  var i = bisect(data, x0, 1);
	  selectedData = data[i]
	  focus
		.attr("cx", x(selectedData.time))
		.attr("cy", y(selectedData.value))
	  focusText
		.html(selectedData.value + " m")
		.attr("x", x(selectedData.time)-30)
		.attr("y", y(selectedData.value)+30)
	  }
	function mouseout() {
	  focus.style("opacity", 0)
	  focusText.style("opacity", 0)
	}
   
}

function loadData() {

	d3.json("http://192.168.178.25:8888", function(error, data) {
		if (error) {
			d3.select(".error").style("visibility", "visible");
			d3.select(".content").style("visibility", "hidden");
		} else {
			d3.select(".error").style("visibility", "hidden");
			d3.select(".content").style("visibility", "visible");
			
			d3.select("#state").text(data.Status);
			
			document.getElementById("graph").innerHTML = "";
			drawGraph(data.DamLevelData);
			if (data.Status.includes("Allarm") || data.Status.includes("Manual")) {
				d3.select("#graph").style("visibility", "visible");
			} else {
				d3.select("#graph").style("visibility", "hidden");
			}
			
			d3.select("#opening").text(data.DamOpenPercentage + "%");
			d3.select("#opening-bar")
				.transition()
				.duration(2000)
				.attr("width", data.DamOpenPercentage + "%")
			if (data.Status == "Allarm" || data.Status.includes("Manual")) {
				d3.select("#openingContainer").style("visibility", "visible");
			} else {
				d3.select("#openingContainer").style("visibility", "hidden");
			}

			if(data.Status.includes("Manual")) {
				d3.select("#opening-bar").style("fill", "red");
			} else {
				d3.select("#opening-bar").style("fill", "grey");
			}

			console.log(data);


		}
	});

	window.setTimeout(loadData, 2500);
}

document.addEventListener('DOMContentLoaded', function(event) {
	loadData();
})