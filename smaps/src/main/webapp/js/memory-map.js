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

// Prints error to memory-map.html if there is one.
printError();

/* Sets the error message on memory-map.html if one is sent by
 * SearchAddress.java.
 */
function printError() {
  fetch('/searchaddress')
      .then((response) => {
        return response.json();
      })
      .then((searchAddressJson) => {
        // The error message is the third item in the json list, and if the
        // message isn't an empty String, get the error-message tag from
        // memory-map.html and set it to the message.
        errorJson = searchAddressJson[2];
        if (errorJson != '') {
          document.getElementById('error-message').innerHTML = errorJson;
        }
      });
}

/* Creates the memory map visualization from the regions list, colors the
 * regions based on the permissions, and if the user entered an address for a
 * specific region, scrolls to that region and highlights it.
 */
function drawRegions() {
  fetch('/memorymap')
      .then((response) => {
        return response.json();
      })
      .then((memoryMapJson) => {
        // Get the div that'll hold all the regions.
        const memMapDiv = document.getElementById('memory-map-div');

        // Go through each region in the list of regions Json.
        for (let i = memoryMapJson.length - 1; i >= 0; i--) {
          // Get attributes for this region from the memory map json.
          let addressRange = memoryMapJson[i][0];
          let addressArray = addressRange.split(' ');
          let permissions = memoryMapJson[i][1];

          // Create a new div for this region to go in.
          let regDiv = document.createElement('div');

          // Create the region as a button object.
          let region = document.createElement('button');

          // Set an ID for the region that is the same as it's location in the
          // list.
          region.id = i;

          // Style the region with the class called region in style.css.
          region.className = 'region';

          // Adjust the style so that only the last region has a bottom border
          // (because they overlap otherwise and double the width), and also
          // color the region based on permissions.
          if (i == 0) {
            region.style['borderBottomWidth'] = '0.05em';
          } else {
            region.style['borderBottomWidth'] = '0em';
          }
          region.style['backgroundColor'] = getColor(permissions);

          // Add the text nodes to the region line by line going backwards
          // because we want to print the end address first (at the top of the
          // button), and print a line break after each word so they stack
          // within the button.
          for (let j = addressArray.length - 1; j >= 0; j--) {
            let text = document.createTextNode(addressArray[j]);
            region.appendChild(text);
            let br = document.createElement('br');
            region.appendChild(br);
          }

          // Add the new region to the region div, and add the new region div to
          // the memory map div.
          regDiv.appendChild(region);
          memMapDiv.appendChild(regDiv);
        }

        // Scroll the page to the specified region; if one has not been selected
        // or the reset button was clicked, the page will stay at the top.
        scrollToRegion();
      });
}

/* Scrolls the page to the region in which the address that the user
 * entered is in; if the address is invalid, the search box was
 * empty, or the reset button was clicked, the page will be set at the top.
 */
function scrollToRegion() {
  fetch('/searchaddress')
      .then((response) => {
        return response.json();
      })
      .then((searchAddressJson) => {
        // Get the address the user entered, and the index in the region list
        // that the region occupies, which is the same as that region's ID in
        // the memory map.
        const address = searchAddressJson[0];
        const index = searchAddressJson[1];

        // If the index is -1 it means there wasn't a matching region
        // found or no address was entered, so just reset the scroll to the top.
        // If the index isn't -1, it's valid, so scroll to that region.
        if (index != -1) {
          // Refill the textbox with the user-entered number.
          document.getElementById('address-input').value = address;

          // Get the region that has the index as it's ID.
          const region = document.getElementById(index);

          // Give the region a glow to highlight it by giving it a yellow box
          // shadow, adding a bottom border that it didn't have before, and
          // bringing it forward with it's z-index.
          region.style['boxShadow'] = '0 0 1.5em 1em rgba(252, 201, 52, 1)';
          region.style['borderBottomWidth'] = '0.05em';
          region.style['zIndex'] = '1';

          // Scroll the region to the center of the screen.
          region.scrollIntoView({behavior: 'smooth', block: 'center'});

          // Display the selected region to focus on the right.
          drawRegionFocus(searchAddressJson[3])
        }
      });
}

/* Creates the key to indicate which color corresponds to which permissions. */
function drawMemoryMapKey() {
  // Get the canvas for putting the key on.
  const c = document.getElementById('key-canvas');

  // Set the canvas width and height.
  c.width = 300;
  c.height = 220;

  // All permissions.
  const perms = ['---p', 'rw-p', 'r-xp', 'r--s', 'r--p', 'rw-s', 'r-xs'];
  const permsSpaced = [
    '- - - p', 'r w - p', 'r - x p', 'r - - s', 'r - - p', 'r w - s', 'r - x s'
  ];

  // Set the swatch size values.
  const x = 1;    // X-coordinate of the upper-left corner of the swatch.
  let y = 1;      // Y-coordinate of the upper-left corner of the swatch.
  const w = 135;  // Width of the swatch (pixels).
  const h = 30;   // Height of the swatch (pixels).

  // Draw all the color swatches indicating permission.
  const swatch = c.getContext('2d');
  for (let i = 0; i < perms.length; i++) {
    // Use black to draw the border of the swatch with line width of 2.
    swatch.beginPath();
    swatch.lineWidth = '1';
    swatch.strokeStyle = 'black';
    swatch.rect(x, y, w, h);
    swatch.stroke();

    // Use color at index i to make the swatch for permission at index i.
    let color = getColor(perms[i]);
    swatch.fillStyle = color;
    swatch.fill();

    // Use black to draw the text on the swatch indicating the permission.
    swatch.fillStyle = 'black';
    swatch.font = '14px Monospace';
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

/* Creates the box of information displayed at the right of the screen with the
 * specified region's fields.
 */
function drawRegionFocus(region) {
  // Get the button object that the region info will go in.
  const regionFocusContainer =
      document.getElementById('region-focus-container');

  // Create a button object to put all the region info in.
  const regionFocusBtn = document.createElement('button');

  // Style the button.
  regionFocusBtn.className = 'region-focus';

  // Create a json object with key value pairs for the field name and how it
  // will be displayed to the user.
  const fieldNames = createFieldNames();

  // Go through every field in the region.
  for (field in region) {
    // Print the proper label, the value, and if the field is one that is
    // represented in kB, add a kB to the end of the string, otherwise don't.
    let textNode;
    if (isKB(field)) {
      textNode = document.createTextNode(
          fieldNames[field] + ': ' + region[field] + ' kB');
    } else {
      textNode =
          document.createTextNode(fieldNames[field] + ': ' + region[field]);
    }

    // Add the text to the focused region with a line break so the next line
    // will be on a newline.
    regionFocusBtn.appendChild(textNode);
    const br = document.createElement('br');
    regionFocusBtn.appendChild(br);
  }

  // Add the focused region to the container on memory-map.html.
  regionFocusContainer.appendChild(regionFocusBtn);
}

/* Creates an object fieldNames that contains key/value pairs for the name of
 * the variable and how it will be displayed to the user when printed out with a
 * focused region.
 */
function createFieldNames() {
  const fieldNames = {
    'lineNumber': 'Line Number in Smaps File',
    'startLoc': 'Address Start (inclusive)',
    'endLoc': 'Address End (exclusive)',
    'permissions': 'Permissions',
    'offset': 'Offset',
    'device': 'Device',
    'inode': 'inode',
    'pathname': 'File Backing Pathname',
    'size': 'Size',
    'kernelPageSize': 'KernelPageSize',
    'mmuPageSize': 'MMUPageSize',
    'rss': 'Rss',
    'pss': 'Pss',
    'sharedClean': 'Shared_Clean',
    'sharedDirty': 'Shared_Dirty',
    'privateClean': 'Private_Clean',
    'privateDirty': 'Private_Dirty',
    'referenced': 'Referenced',
    'anonymous': 'Anonymous',
    'lazyFree': 'LazyFree',
    'anonHugePages': 'AnonHugePages',
    'shmemHugePages': 'ShmemHugePages',
    'shmemPmdMapped': 'ShmemPmdMapped',
    'sharedHugetlb': 'Shared_Hugetlb',
    'privateHugetlb': 'Private_Hugetlb',
    'hugePFNMap': 'HugePFNMap',
    'swap': 'Swap',
    'swapPss': 'SwapPss',
    'locked': 'Locked',
    'vmFlags': 'VmFlags'
  };
  return fieldNames;
}

/* Return whether a field should have a kB concatenated to the end after the
 * value, all of the fields listed here are in kB.
 */
function isKB(field) {
  const kBFields = [
    'size',
    'kernelPageSize',
    'mmuPageSize',
    'rss',
    'pss',
    'sharedClean',
    'sharedDirty',
    'privateClean',
    'privateDirty',
    'referenced',
    'anonymous',
    'lazyFree',
    'anonHugePages',
    'shmemHugePages',
    'shmemPmdMapped',
    'sharedHugetlb',
    'privateHugetlb',
    'hugePFNMap',
    'swap',
    'swapPss',
    'swapPss',
    'locked'
  ];

  if (kBFields.includes(field)) {
    return true;
  }
  return false;
}
