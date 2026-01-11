# Nutritional Data Analysis System

CLI system in Clojure for analyzing nutritional data from `calories.json`, returning all results in JSON format.

## Why this exists

This system was created to provide a command-line tool for statistical analysis of nutritional data, enabling developers and data analysts to extract insights from meal consumption records through simple CLI commands. The need arose from the requirement to analyze 6,000 meal records from 20 users over 3 months, identifying patterns in caloric consumption, macronutrient distribution, temporal trends, and correlations between variables. The JSON-first output design allows seamless integration with data processing pipelines and analysis tools like `jq`, making it suitable for automated analysis workflows.

## How does it work

The system loads nutritional data from a JSON file (or remote URL), normalizes and validates the records by converting data types and ensuring data consistency. It then applies statistical analyses including aggregations by user and food, temporal pattern analysis, comparative analysis across dimensions (procedence, meal types, users), and correlation calculations between variables. All analyses are performed in-memory using functional transformations, and results are returned as structured JSON to STDOUT, allowing for easy piping and integration with other command-line tools.

## Core concepts

- **JSON-first output**: All commands return structured JSON, enabling programmatic consumption and integration with data processing pipelines
- **CLI-oriented design**: Simple command syntax that follows Unix philosophy—each command does one thing well and outputs JSON
- **Statistical analysis**: The system performs descriptive statistics, aggregations, temporal trend analysis, and correlation calculations
- **Data normalization**: Automatic type conversion and validation ensure data consistency before analysis
- **Temporal patterns**: Analysis by time dimensions (day of week, hour of day, period of day) and temporal trends over time
- **Comparative analysis**: Multi-dimensional comparisons across users, foods, meal types, and procedence (homemade vs purchased)

## Available Commands

- `summary` - Returns general dataset statistics including overview, descriptive statistics, and food rankings
- `user <user-id>` - Returns detailed analysis of a specific user including total calories, daily averages, macronutrients, spending, favorite foods, and consumption patterns
- `food <food-name>` - Returns analysis of a specific food including average calories, price, frequency, cost-benefit ratio, and macronutrients
- `compare <dimension>` - Returns comparisons between groups. Dimensions: `procedence` (homemade vs purchased), `meal-type` (breakfast, lunch, dinner, snack), `users` (consumption and spending rankings)
- `temporal` - Returns temporal analyses including consumption by weekday, hour of day, period of day (morning, afternoon, evening), temporal trends, and moving averages
- `correlation` - Returns correlation analyses between variables: age vs calories, user weight vs calories, price vs calories, food weight vs calories, meal type vs time

**Global Options:**
- `-f, --file FILE` - Path to calories.json file (or URL). Default: Toptal GitLab URL
- `-p, --pretty` - Format JSON with indentation (pretty-print)
- `-h, --help` - Show help message

**Usage:** `lein run [command] [arguments] [options]`
