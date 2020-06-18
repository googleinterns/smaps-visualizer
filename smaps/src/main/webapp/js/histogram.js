/**
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Load the Visualization API and the histogram package.
google.charts.load('current', {'packages': ['corechart', 'controls']});

// Set a callback to run when the Google Visualization API is loaded.
setBounds();
google.charts.setOnLoadCallback(drawHistogramCust);

var lowerBound;
var upperBound;

function setBounds() {
  fetch('/histdash')
      .then((response) => {
        return response.json();
      })
      .then((boundsJson) => {
        lowerBound = boundsJson[0];
        upperBound = boundsJson[1];

        document.getElementById('lower-bound').value = lowerBound;
        document.getElementById('upper-bound').value = upperBound;
      });
}

/*
 * Callback that creates and populates the data table for region sizes,
 * instantiates the small histogram, passes in the data, and draws it.
 */
function drawHistogramCust() {
  // Fetch the Json object from the Histogram servlet.
  fetch('/histogram')
      .then((response) => {
        return response.json();
      })
      .then((histogramJson) => {
        // Converts histogram Json 2D array into a data table for histogram.
        var data = google.visualization.arrayToDataTable(histogramJson);

        // Create a dashboard.
        var dashboard = new google.visualization.Dashboard(
            document.getElementById('dashboard-div'));

        // Create a range slider, passing some options
        // var lowerBound = document.getElementById('lower-bound').value;
        // var upperBound = document.getElementById('upper-bound').value;
        var histogramRangeSlider = new google.visualization.ControlWrapper({
          'controlType': 'NumberRangeFilter',
          'containerId': 'filter-div',
          'options': {
            'filterColumnLabel': 'Size',
            'minValue': lowerBound,
            'maxValue': upperBound,
            ui: {
              cssClass: 'hist-slider',
              snapToData: true,
              ticks: 1,
              unitIncrement: 5,
              blockIncrement: 100000
            }
          }
        });

        // Set chart options.
        var options = {
          title: 'Histogram of Region Sizes',
          titleTextStyle: {color: '#5F6368', fontName: 'Roboto', fontSize: 18},
          width: 1800,
          height: 900,
          colors: ['#4285F4'],
          legend: {position: 'none'},
          hAxis: {title: 'Size in kB'},
          vAxis: {title: 'Number of Regions'}
        };

        var histogram = new google.visualization.ChartWrapper({
          chartType: 'Histogram',
          dataTable: data,
          options: options,
          containerId: 'chart-div',
          view: {columns: [0, 1]}
        });

        // Establish dependencies
        dashboard.bind(histogramRangeSlider, histogram);

        // Instantiate and draw chart, passing in some options.
        dashboard.draw(data, options);
      });
}
