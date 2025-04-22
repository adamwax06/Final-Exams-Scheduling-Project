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

Consider the following for more efficiency:
1. Order by “saturation degree” (number of differently‑colored neighbors) rather than just raw degree.
   - Going to be implemented in Neighbors folder
2. Shuffle your vertex list a few times and rerun the greedy coloring—track the minimum slots you see.
   - This is implemented in Random folder
3. When two vertices have the same degree, pick the one whose neighbors are more “constrained” (e.g. higher average neighbor degree) first.
   - Going to be implemented in Neighbors folder

## Usage

Follow the commands:

1. Enter "javac src/\*.java" in your terminal
2. Enter "java src.ExamScheduler"
