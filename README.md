# Flight Planning Application

This is a Java application that helps users plan flights between cities served by an airline. It takes two input files: one containing a list of available flight legs with their corresponding costs and durations, and another containing a list of requested flight plans. The application finds all possible flight routes between the requested origin and destination cities and outputs the three most efficient routes based on the user's preference for sorting by time or cost.

## Features

- Reads flight data from an input file containing origin, destination, cost, and duration for each flight leg
- Reads requested flight plans from an input file containing origin, destination, and sorting preference (time or cost)
- Calculates all possible flight routes between the requested origin and destination cities
- Outputs the three most efficient flight routes based on the sorting preference (time or cost)
- Handles cases where no feasible flight routes exist between the requested cities
- Utilizes an adjacency list data structure to represent the flight network
- Implements an iterative backtracking algorithm with a stack to find all possible flight routes

## Prerequisites

- Java Development Kit (JDK) 8 or higher

## Getting Started

1. Clone the repository or download the project files.
2. Ensure that you have JDK 8 or higher installed on your system.

### Running the Application

1. Navigate to the project directory.
2. Compile the Java files:
