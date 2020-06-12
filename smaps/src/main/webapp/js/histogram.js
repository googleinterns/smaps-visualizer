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
google.charts.load('current', {'packages': ['corechart']});

// Set a callback to run when the Google Visualization API is loaded.
google.charts.setOnLoadCallback(drawHistogram);

var data;
var chart;

/*
 * Callback that creates and populates the data table for region sizes,
 * instantiates the histogram, passes in the data, adn draws it.
 */
function drawHistogram() {
  data = google.visualization.arrayToDataTable([
    ['Region Name', 'Size'], ['region 1', 500], ['region 2', 1500],
    ['region 3', 1500], ['region 4', 0], ['region 5', 0], ['region 6', 2000]
  ]);

  // Set chart options.
  var options = {'title': 'Histogram of Region Sizes'};

  // Instantiate and draw our chart, passing in some options.
  chart =
      new google.visualization.Histogram(document.getElementById('hist_div'));
  chart.draw(data, options);
}