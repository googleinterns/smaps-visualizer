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

        // Get the number of regions
        var numRegs = memoryMapJson.length;

        // Get the canvas for putting the region rectangles on.
        var c = document.getElementById('myCanvas');

        // Set the canvas height to be tall enough to display all regions, plus
        // ten more for a buffer.
        c.width = 1000;
        c.height = h * (numRegs + 10);

        // Draw all the region rectangles.
        var reg = c.getContext('2d');
        for (var i = memoryMapJson.length - 1; i >= 0; i--) {
          // Get this region's address space and permissions.
          var text = memoryMapJson[i][0];
          var perms = memoryMapJson[i][1];

          // Use black to draw the border of the rectangle with line width of 2.
          reg.beginPath();
          reg.lineWidth = '2';
          reg.strokeStyle = 'black';
          reg.rect(x, y, w, h);
          reg.stroke();

          // Use permission to determine color to fill in the region rectangle.
          if (perms == '---p') {
            reg.fillStyle = '#D7AEFB';
          } else if (perms == 'rw-p') {
            reg.fillStyle = '#A1E4F2';
          } else if (perms == 'r-xp') {
            reg.fillStyle = '#FBA9D6';
          } else if (perms == 'r--s') {
            reg.fillStyle = '#FDC69C';
          } else if (perms == 'r--p') {
            reg.fillStyle = '#FDE293'
          } else if (perms == 'rw-s') {
            reg.fillStyle = '#A8DAB5';
          } else if (perms == 'r-xs') {
            reg.fillStyle = 'F6AEA9';
          }
          reg.fill();

          // Use black to draw the text within the region rectangle.
          reg.fillStyle = 'black';
          reg.font = '15px Roboto';
          reg.fillText(text, (w / 2), y + (h / 2));
          y = y + h;
        }
      });
}
