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

// Set the bounds for the slider and text boxes.
setBounds();
// Set a callback to run when the Google Visualization API is loaded.
google.charts.setOnLoadCallback(drawHistogramCust);

var lowerBound;  // Lower bound of sizes, either preset or chosen by user.
var upperBound;  // Upper bound of sizes, either preset or chosen by user.

/*
 * Sets the global variables lowerBound and upperBound to be the
 * values sent in from the histogram servlet.
 */
function setBounds() {
  // Fetches the Json object from the Histogram servlet.
  fetch('/histogram')
      .then((response) => {
        return response.json();
      })
      .then((histogramJson) => {
        // The bounds are in the first index of the Json, so histogramJson[0].
        lowerBound = histogramJson[0][0];
        upperBound = histogramJson[0][1];

        // Set the textbox values to be the chosen bounds.
        document.getElementById('lower-bound').value = lowerBound;
        document.getElementById('upper-bound').value = upperBound;
      });
}

/*
 * Callback that creates and populates the data table for region sizes,
 * instantiates the histogram, passes in the data, and draws it.
 */
function drawHistogramCust() {
  // Fetches the Json object from the Histogram servlet.
  fetch('/histogram')
      .then((response) => {
        return response.json();
      })
      .then((histogramJson) => {
        // Converts histogram Json 2D array into a data table for histogram. The
        // histogram data is in the second index of the Json, so
        // histogramJson[1].
        var data = google.visualization.arrayToDataTable(histogramJson[1]);

        // Creates the dashboard.
        var dashboard = new google.visualization.Dashboard(
            document.getElementById('dashboard-div'));

        // Creates the range slider, passing in some options.
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

        // Sets the chart options.
        var options = {
          title: 'Histogram of Region Sizes',
          titleTextStyle: {color: '#5F6368', fontName: 'Roboto', fontSize: 18},
          width: 2000,
          height: 900,
          colors: ['#4285F4'],
          legend: {position: 'none'},
          hAxis: {title: 'Size in KiB'},
          vAxis: {title: 'Number of Regions'}
        };

        // Sets the settings for the histogram.
        var histogram = new google.visualization.ChartWrapper({
          chartType: 'Histogram',
          dataTable: data,
          options: options,
          containerId: 'chart-div',
          view: {columns: [0, 1]}
        });

        // Establishes dependencies.
        dashboard.bind(histogramRangeSlider, histogram);

        // Instantiates and draws the chart, passing in some options.
        dashboard.draw(data, options);
      });
}
