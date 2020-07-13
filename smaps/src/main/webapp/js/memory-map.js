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
function drawRegions() {
  fetch('/memorymap')
      .then((response) => {
        return response.json();
      })
      .then((memoryMapJson) => {
        // Get the div that'll hold the regions.
        var memMapDiv = document.getElementById('memory-map-div');
        for (var i = memoryMapJson.length - 1; i >= 0; i--) {
          // Get attributes from the memory map json.
          var text = memoryMapJson[i][0];
          var permissions = memoryMapJson[i][1];

          // Create a new div for this region to go in.
          var regDiv = document.createElement('div');

          // Create the region as a button object.
          var region = document.createElement('button');

          // Set an ID for the region as it's location in the list.
          region.id = i;

          // Style the region.
          region.style['width'] = '25em';
          region.style['height'] = '3em';
          region.style['zIndex'] = '-1';
          region.style['position'] = 'relative';
          region.style['backgroundColor'] = getColor(permissions);
          region.style['borderLeftWidth'] = '0.05em';
          region.style['borderRightWidth'] = '0.05em';
          region.style['borderTopWidth'] = '0.05em';
          if (i == 0) {
            region.style['borderBottomWidth'] = '0.05em';
          } else {
            region.style['borderBottomWidth'] = '0em';
          }
          region.style['borderColor'] = 'black';
          region.style['pointerEvents'] = 'none';

          // Creating the address range text that'll be on the regions.
          var addressRange = document.createTextNode(text);

          // Add the text to the region, and the region to the region div, and
          // add the region div to the memory map div.
          region.appendChild(addressRange);
          regDiv.appendChild(region);
          memMapDiv.appendChild(regDiv);
        }
      });
  scrollToRegion();
}

/*
 * Scrolls the page back up to the top and empties the text box.
 */
function resetScroll() {
  document.getElementById('address-input').value = null;
  window.scrollTo(0, 0);
  // TODO(@sophbohr22): the yellow glow doesn't go away on reset.
}

/* Scrolls the page to the region that is occupying the address that the user
 * entered in the search box.
 */
function scrollToRegion() {
  fetch('/searchaddress')
      .then((response) => {
        return response.json();
      })
      .then((searchAddressJson) => {
        // Get the address as a string.
        var address = searchAddressJson[0];
        console.log(address);

        // Scroll to the region that occupies the address the user entered, if
        // the address is invalid or the search box was blank then just scroll
        // to top.
        // Refill the textbox with the user-entered number.
        document.getElementById('address-input').value = address;

        // Get the region's index in the list, which correspond's to that
        // region's ID in the memory map.
        var index = searchAddressJson[1];

        // If the index is set to -1 it means there wasn't a matching region
        // found, so just reset the scroll to the top.
        if (index == -1) {
          resetScroll();
        } else {
          console.log(index);
          var region = document.getElementById(index);
          region.style['boxShadow'] = '0 0 1.5em 1em rgba(252, 201, 52, 1)';
          region.style['zIndex'] = '1';
          region.scrollIntoView({behavior: 'smooth', block: 'center'});
        }
      });
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
