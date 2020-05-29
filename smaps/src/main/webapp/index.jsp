<!--
Copyright 2020 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.smaps.Homepage" %>
<html>
<head>
  <link href='//fonts.googleapis.com/css?family=Marmelad' rel='stylesheet' type='text/css'>
  <title>Smaps Visualizer</title>
</head>
<body>
    <h1>Welcome to the Smaps Visualizer</h1>

  <p><%= Homepage.getInfo() %></p>
  <table>
    <tr>
      <td colspan="2" style="font-weight:bold;">Available Servlets:</td>
    </tr>
    <tr>
      <td><a href='/home'>Home for our App Engine</a></td>
    </tr>
  </table>

</body>
</html>
