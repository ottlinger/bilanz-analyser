# bilanz-analyser
Helper to analyze the local Bilanz

[![GitHub Action master branch status](https://github.com/ottlinger/bilanz-analyser/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/ottlinger/bilanz-analyser/actions)
[![AGPL v3.0](https://img.shields.io/github/license/ottlinger/bilanz-analyser.svg)](https://www.gnu.org/licenses/agpl-3.0.en.html)

## Abstract

I do have a highly-customized ODS/spreadsheet with my personal balance sheet. 

It contains a bunch of tables with certain logics. One of them is called Ausgaben/spending and its data is supposed to be visualised with this application.

# Roadmap

1. ☑️ Process an ODS file and parse its rows - see issue [#3](https://github.com/ottlinger/bilanz-analyser/issues/3)
2. Upload an ODS file via a webform for data analysis
3. Load and extract its data into an in-memory-database (h2)
4. Perform data visualisation on this database to allow drilldowns and lists in various dimensions

## Bootstrap integration

* in src/main/resources/static run
´
npm install bootstrap@5.3.8
´
and copy all stuff from 
node_modules/bootstrap/dist
into the application base folder.

## Bootstrap icon integration

* https://icons.getbootstrap.com/
* download ZIP
* put all files from the zip into [src/main/resources/static/css/icons/](src/main/resources/static/css/icons/)
* Find an icon via: https://icons.getbootstrap.com/?q=file
