function drawGraph(data) {

	// set the dimensions and margins of the graph
	var margin = {top: 20, right: 20, bottom: 30, left: 50},
		width = 960 - margin.left - margin.right,
		height = 500 - margin.top - margin.bottom;

	// parse the date / time
	var parseTime = d3.timeParse("%Y-%m-%dT%H:%M:%S");

	// set the ranges
	var x = d3.scaleTime().range([0, width]);
	var y = d3.scaleLinear().range([height, 0]);

	// define the line
	var valueline = d3.line()
		.x(function(d) { return x(d.time); })
		.y(function(d) { return y(d.value); });
	
	// append the svg obgect to the body of the page
	// appends a 'group' element to 'svg'
	// moves the 'group' element to the top left margin
	var svg = d3.select("#graph").append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
	.append("g")
		.attr("transform",
			"translate(" + margin.left + "," + margin.top + ")");
	
	// format the data
	data.forEach(function(d) {
		d.time = parseTime(d.time);
		d.value = +d.value;
	});
	
	// Scale the range of the data
	x.domain(d3.extent(data, function(d) { return d.time; }));
	y.domain([0, d3.max(data, function(d) { return d.value; })]);
	
	// Add the valueline path.
	svg.append("path")
		.data([data])
		.attr("class", "line")
		.attr("d", valueline);
 
	// Add the X Axis
	svg.append("g")
		.attr("transform", "translate(0," + height + ")")
		.call(d3.axisBottom(x));

	// Add the Y Axis
	svg.append("g")
		.call(d3.axisLeft(y));
}

function loadData() {

	d3.json("http://localhost:8888", function(error, data) {
		if (error) {
			document.getElementById("main").innerHTML = `<div class="error">Server offline</div>`;
		} else {
			document.getElementById("main").innerHTML = `
				<div>
					<span>System State: </span>
					<span id ="state"></span>
				</div>
				<div>
					<span>Dam Opening Percentage: </span>
					<span id="opening"></span>
				</div>
				<div id="graph">
				</div>
			`;

			document.getElementById("graph").innerHTML = "";
			
			d3.select("#state").text(data.Status);
			d3.select("#opening").text(data.DamOpenPercentage);

			console.log(data);

			drawGraph(data.DamLevelData);

		}
	});

	window.setTimeout(loadData, 5000);
}

document.addEventListener('DOMContentLoaded', function(event) {
	loadData();
})