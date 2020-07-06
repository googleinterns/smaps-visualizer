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

/* Creates the memory map visualization from the regions list, and colors them
 * based on the permissions.
 */
function drawMemoryMap() {
  // Fetches the Json object from the MemoryMap servlet.
  fetch('/memorymap')
      .then((response) => {
        return response.json();
      })
      .then((memoryMapJson) => {
        // Set the rectangle size values.
        var x = 5;    // X-coordinate of the upper-left corner of the rectangle.
        var y = 5;    // Y-coordinate of the upper-left corner of the rectangle.
        var w = 450;  // Width of the rectangle (pixels).
        var h = 40;   // Height of the rectangle (pixels).

        // Get the number of regions.
        var numRegs = memoryMapJson.length;

        // Get the canvas for putting the region rectangles on.
        var c = document.getElementById('memory-map-canvas');

        // Set the canvas height to be tall enough to display all regions, plus
        // five more for a buffer.
        c.width = 500;
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
          var color = getColor(perms);
          reg.fillStyle = color;
          reg.fill();

          // Increase the y-coordiate to draw the next region rectangle directly
          // below this one.
          y = y + h;
        }

        // Print all the addresses on top of the colored region rectangles.
        drawText(c.width);
      });
}

/* Creates the address range text to overlay on the region rectangles. */
function drawText(width) {
  fetch('/memorymap')
      .then((response) => {
        return response.json();
      })
      .then((memoryMapJson) => {
        for (var i = memoryMapJson.length - 1; i >= 0; i--) {
          // Get this region's address range and permissions.
          var address = memoryMapJson[i][0];
          var perms = memoryMapJson[i][1];

          // If the region has permission ---p, add an annotation for Guard
          // Band.
          if (perms == '---p') {
            address = address + ' (Guard Band)';
          }

          // Get the div from memory-map.html and populate it with the
          // addresses.
          var textDiv = document.getElementById('memory-map-div');
          drawTextHelper(width, address, textDiv);
        }
      });
}

/* Helps the drawText function, takes in the width of the region, the text to
 * print, and the name of the div, and prints to that div.
 */
function drawTextHelper(width, text, div) {
  // Create new <p> tag.
  var paragraph = document.createElement('p');

  // Set the width to be the same width as the region.
  paragraph.style.width = width;

  // Create a text node with the address.
  var textNode = document.createTextNode(text);

  // Add the text node to the <p> tag.
  paragraph.appendChild(textNode);

  // Add the <p> tag to the div.
  div.appendChild(paragraph);
}

/* Creates the key to indicate which color corresponds to which permissions. */
function drawMemoryMapKey() {
  // Get the canvas for putting the key on.
  var c = document.getElementById('key-canvas');

  // Set the canvas width and height.
  c.width = 300;
  c.height = 220;

  // All permissions.
  var perms = ['---p', 'rw-p', 'r-xp', 'r--s', 'r--p', 'rw-s', 'r-xs'];
  var permsSpaced = [
    '- - - p', 'r w - p', 'r - x p', 'r - - s', 'r - - p', 'r w - s', 'r - x s'
  ];

  // Set the swatch size values.
  var x = 1;    // X-coordinate of the upper-left corner of the swatch.
  var y = 1;    // Y-coordinate of the upper-left corner of the swatch.
  var w = 135;  // Width of the swatch (pixels).
  var h = 30;   // Height of the swatch (pixels).

  // Draw all the color swatches indicating permission.
  var swatch = c.getContext('2d');
  for (var i = 0; i < perms.length; i++) {
    // Use black to draw the border of the swatch with line width of 2.
    swatch.beginPath();
    swatch.lineWidth = '1';
    swatch.strokeStyle = 'black';
    swatch.rect(x, y, w, h);
    swatch.stroke();

    // Use color at index i to make the swatch for permission at index i.
    var color = getColor(perms[i]);
    swatch.fillStyle = color;
    swatch.fill();

    // Use black to draw the text on the swatch indicating the permission.
    swatch.fillStyle = 'black';
    swatch.font = '16px Roboto';
    swatch.fillText(permsSpaced[i], 15, y + (h / 2) + 5);
    y = y + h;
  }
}

/* Fills the region rectangle based on the permissions. */
function getColor(permissions) {
  switch (permissions) {
    case '---p':
      return '#BDC1C6';  // Gray, 400 intensity.
    case 'rw-p':
      return '#78D9EC';  // Teal, 300 intensity.
    case 'r-xp':
      return '#81C995';  // Green, 300 intensity.
    case 'r--s':
      return '#FCAD70';  // Orange, 300 intensity.
    case 'r--p':
      return '#C58AF9';  // Purple, 300 intensity.
    case 'rw-s':
      return '#FF8BCB';  // Pink, 300 intensity.
    case 'r-xs':
      return '#669DF6';  // Blue, 400 intensity.
    default:
      return 'White';  // White.
  }
}
