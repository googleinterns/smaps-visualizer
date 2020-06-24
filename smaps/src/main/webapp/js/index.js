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

// Prints error to index.html if there is one.
printError();

/**
 * Sets the error message on index.html if one is sent by FileUpload.java.
 */
function printError() {
  fetch('/fileupload')
      .then((response) => {
        return response.json();
      })
      .then((errorJson) => {
        // If the message isn't an empty String, get the error-message tag from
        // index.html and set it to the message.
        if (errorJson != '') {
          document.getElementById('error-message').innerHTML = errorJson;
        }
      });
}