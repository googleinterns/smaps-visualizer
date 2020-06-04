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
    <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
    <link href='/css/style.css' rel='stylesheet' type='text/css'>
    <title>Smaps Visualizer</title>
</head>

<body>
    <div class="roboto center">
        <div id="smaps-title">
            <span class="gblue">s</span>
            <span class="gred">m</span>
            <span class="gyellow">a</span>
            <span class="gblue">p</span>
            <span class="ggreen">s</span>
        </div>
        <p id="visualizer-title" class="ggray">Visualizer</p>

        <p><%= Homepage.getProjInfo() %></p>

        <p>Available Servlets: <a href='/home'>App Engine System Information</a></p>
    </div>
</body>

</html>