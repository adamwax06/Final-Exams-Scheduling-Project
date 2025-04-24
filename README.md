# Exam Scheduler

A graph coloring algorithm for scheduling final exams to minimize time slots while ensuring no student has multiple exams at the same time.

Authored by: Megan Cream, Molly Pecic, Gideon Sobek, and Adam Wax

## Overview

This project implements a custom graph coloring algorithm to solve the exam scheduling problem. By representing classes as vertices and conflicts as edges, we create an optimal schedule that minimizes the number of exam periods.

## Algorithm

Our approach follows these steps:

1. Build a conflict graph where classes are vertices and edges represent scheduling conflicts
2. Color the graph using our custom algorithm:
   - Pick a pivot vertex to start with and assign a color
   - Assign neighbors a different color
   - Check if neighbors have edges and change on of their colors
   - Pick a new pivot and repeat

## Usage

Follow the commands:

1. Enter your data into data/studentCourses.txt following the provided format
2. Enter "javac src/\*.java" in your terminal
3. Enter "java src.Runr"
