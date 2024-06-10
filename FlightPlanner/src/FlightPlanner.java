import java.util.*;
import java.io.*;

// defines city with flight connections
class City {
    String name;
    List<Flight> flights = new ArrayList<>();

    public City(String name) {
        this.name = name;
    }

    // adds a flight connection to this city
    void addFlight(City destination, double cost, int time) {
        flights.add(new Flight(destination, cost, time));
    }
}

    //represents a flight from one city to another with cost and time
class Flight {
    City destination;
    double cost;
    int time;

    public Flight(City destination, double cost, int time) {

        this.destination = destination;
        this.cost = cost;
        this.time = time;
    }
}

// manages all cities and flights, also allows path finding
class FlightGraph {
    Map<String, City> cities = new HashMap<>();

    // adds city to the graph
    void addCity(String name) {
        cities.putIfAbsent(name, new City(name));
    }

    // adds a flight to the graph, bidirectional by default
    void addFlight(String origin, String destination, double cost, int time) {

        City oCity = cities.get(origin);
        City dCity = cities.get(destination);
        oCity.addFlight(dCity, cost, time);
        dCity.addFlight(oCity, cost, time);
    }

    //finds all paths between two cities and sorts them by cost or time
    List<List<String>> findAllPaths(String start, String end, char preference) {
        List<List<String>> allPaths = new ArrayList<>();
        if (!cities.containsKey(start) || !cities.containsKey(end)) {
            return allPaths;
        }

        // depth first search to find all paths
        Map<City, Boolean> visited = new HashMap<>();
        cities.values().forEach(city -> visited.put(city, false));
        List<String> cPath = new ArrayList<>();
        dfs(cities.get(start), cities.get(end), visited, cPath, allPaths);

        // sort paths based on preference
        allPaths.sort((path1, path2) -> {
            double cost1 = calcCost(path1);
            double cost2 = calcCost(path2);
            int time1 = calcTime(path1);
            int time2 = calcTime(path2);
            return preference == 'C' ? Double.compare(cost1, cost2) : Integer.compare(time1, time2);
        });


        return allPaths;
    }

    // helper for dfs
    void dfs(City current, City end, Map<City, Boolean> visited, List<String> cPath, List<List<String>> allPaths) {
        visited.put(current, true);
        cPath.add(current.name);

        if (current.equals(end)) {
            allPaths.add(new ArrayList<>(cPath));
        } else {
            for (Flight flight : current.flights) {
                if (!visited.get(flight.destination)) {
                    dfs(flight.destination, end, visited, cPath, allPaths);
                }
            }
        }

        cPath.remove(cPath.size() - 1);
        visited.put(current, false);
    }

    // calculates total cost of a path
    double calcCost(List<String> path) {
        double totalCost = 0;

        for (int i = 0; i < path.size() - 1; i++) {
            City cityRN = cities.get(path.get(i));
            for (Flight flight : cityRN.flights) {
                if (flight.destination.name.equals(path.get(i + 1))) {
                    totalCost += flight.cost;
                    break;
                }
            }
        }

        return totalCost;
    }

    // calculates total time of a path
    int calcTime(List<String> path) {
        int totalTime = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            City cityRN = cities.get(path.get(i));
            for (Flight flight : cityRN.flights) {
                if (flight.destination.name.equals(path.get(i + 1))) {
                    totalTime += flight.time;
                    break;
                }
            }
        }

        return totalTime;
    }
}

    //main class that loads data, process requests, and output results
class FlightPlanner {
    FlightGraph graph = new FlightGraph();

    // loads flight data from file
    public void loadData(String flightDataFilePath) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(flightDataFilePath));
        int num = Integer.parseInt(scanner.nextLine().trim());
        for (int i = 0; i < num; i++) {
            String[] data = scanner.nextLine().split("\\|");
            String origin = data[0];
            String destination = data[1];
            double cost = Double.parseDouble(data[2]);
            int time = Integer.parseInt(data[3]);
            graph.addCity(origin);
            graph.addCity(destination);
            graph.addFlight(origin, destination, cost, time);
        }

        scanner.close();
    }

    // processes requests and outputs results to file
    public void processrequests(String requestFilePath, String outputFilePath) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(requestFilePath));
        PrintWriter writer = new PrintWriter(new File(outputFilePath));


        int numberOfRequests = Integer.parseInt(scanner.nextLine().trim());
        for (int i = 0; i < numberOfRequests; i++) {
            String[] request = scanner.nextLine().split("\\|");
            String startCity = request[0];
            String endCity = request[1];
            char preference = request[2].charAt(0);

            List<List<String>> allPaths = graph.findAllPaths(startCity, endCity, preference);
            writer.println("Flight " + (i + 1) + ": " + startCity + ", " + endCity + " (" + (preference == 'C' ? "Cost" : "Time") + ")");

            if (allPaths.isEmpty()) {
                writer.println("No viable flight plan available.");
            } else {
                int pathCount = 1;
                for (List<String> path : allPaths) {
                    double totalCost = graph.calcCost(path);
                    int totalTime = graph.calcTime(path);
                    writer.println("Path " + pathCount + ": " + String.join(" -> ", path) + ". Time: " + totalTime + " Cost: " + String.format("%.2f", totalCost));
                    if (pathCount++ == 3) break; // limit to the three most efficient paths
                }
            }

            writer.flush();
        }

        scanner.close();
        writer.close();
    }


    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java FlightPlanner <flightDataFilePath> <requestFilePath> <outputFilePath>");
            return;
        }

        FlightPlanner planner = new FlightPlanner();

        try {
            planner.loadData(args[0]);
            planner.processrequests(args[1], args[2]);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }
}
