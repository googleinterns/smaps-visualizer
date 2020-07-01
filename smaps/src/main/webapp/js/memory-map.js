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

function drawMemoryMap() {
  // Fetches the Json object from the MemoryMap servlet.
  fetch('/memorymap')
      .then((response) => {
        return response.json();
      })
      .then((memoryMapJson) => {
        // Set the rectangle size values.
        var x = 20;   // X-coordinate of the upper-left corner of the rectangle.
        var y = 20;   // Y-coordinate of the upper-left corner of the rectangle.
        var w = 500;  // Width of the rectangle (pixels).
        var h = 40;   // Height of the rectangle (pixels).

        // Get the number of regions.
        var numRegs = memoryMapJson.length;

        // Get the canvas for putting the region rectangles on.
        var c = document.getElementById('memory-map-canvas');

        // Set the canvas height to be tall enough to display all regions, plus
        // five more for a buffer.
        c.width = 550;
        c.height = h * (numRegs + 5);

        // Draw all the region rectangles.
        var reg = c.getContext('2d');
        for (var i = memoryMapJson.length - 1; i >= 0; i--) {
          // Get this region's address range and permissions.
          var text = memoryMapJson[i][0];
          var perms = memoryMapJson[i][1];

          // Use black to draw the border of the rectangle with line width of 2.
          reg.beginPath();
          reg.lineWidth = '2';
          reg.strokeStyle = 'black';
          reg.rect(x, y, w, h);
          reg.stroke();

          // Use permissions to determine color to fill in the region rectangle.
          if (perms == '---p') {
            reg.fillStyle = '#BDC1C6';  // Gray, 400 intensity.
          } else if (perms == 'rw-p') {
            reg.fillStyle = '#78D9EC';  // Teal, 300 intensity.
          } else if (perms == 'r-xp') {
            reg.fillStyle = '#81C995';  // Green, 300 intensity.
          } else if (perms == 'r--s') {
            reg.fillStyle = '#FCAD70';  // Orange, 300 intensity.
          } else if (perms == 'r--p') {
            reg.fillStyle = '#C58AF9';  // Purple, 300 intensity.
          } else if (perms == 'rw-s') {
            reg.fillStyle = '#FF8BCB';  // Pink, 300 intensity.
          } else if (perms == 'r-xs') {
            reg.fillStyle = '#669DF6';  // Blue, 400 intensity.
          }
          reg.fill();

          // Use black to draw the text within the region rectangle.
          reg.fillStyle = 'black';
          reg.font = '16px Roboto';
          reg.fillText(text, (w / 2), y + (h / 2));

          // Increase the y-coordiate to draw the next region rectangle directly
          // below this one.
          y = y + h;
        }
      });
}

function drawMemoryMapKey() {
  // Get the canvas for putting the key on.
  var c = document.getElementById('key-canvas');

  // Set the canvas width and height.
  c.width = 300;
  c.height = 220;

  // The perms and colors arrays match up by index.
  var perms = [
    '- - - p', 'r w - p', 'r - x p', 'r - - s', 'r - - p', 'r w - s', 'r - x s'
  ];

  // Gray, Teal, Pink, Orange, Purple, Green, Blue.
  var colors = [
    '#BDC1C6', '#78D9EC', '#81C995', '#FCAD70', '#C58AF9', '#FF8BCB', '#669DF6'
  ];

  // Set the swatch size values.
  var x = 1;    // X-coordinate of the upper-left corner of the swatch.
  var y = 1;    // Y-coordinate of the upper-left corner of the swatch.
  var w = 135;  // Width of the swatch (pixels).
  var h = 30;   // Height of the swatch (pixels).

  // Draw all the color swatches indicating permission.
  var swatch = c.getContext('2d');
  for (var i = 0; i < colors.length; i++) {
    // Use black to draw the border of the swatch with line width of 2.
    swatch.beginPath();
    swatch.lineWidth = '1';
    swatch.strokeStyle = 'black';
    swatch.rect(x, y, w, h);
    swatch.stroke();

    // Use color at index i to make the swatch for permission at index i.
    swatch.fillStyle = colors[i];
    swatch.fill();

    // Use black to draw the text on the swatch indicating the permission.
    swatch.fillStyle = 'black';
    swatch.font = '16px Roboto';
    swatch.fillText(perms[i], 15, y + (h / 2) + 5);
    y = y + h;
  }
}
