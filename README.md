# Algorithms and Data Structures project on different heaps and their use in the Dijkstra and A* algorithms.

I made this project in 2023 as part of AD2 assignment at UGent (major Informatica).<br>
**[Here you can see my the full report](extra/verslag.pdf) of the project with some mathematical conclusions about heaps.<br>**


# Benchmarks

Measured on: **Intel i5-1240P**<br>
Measured with **pre-filled heaps**

## Heaps

Running `decreaseKey`-operations on heaps:

### Pairing Heap

| Elements Count | Operations Count | Recursive | Mean     | Std-Dev  |
|----------------|------------------|-----------|----------|----------|
| 1 000          | 1 000            | true      | 0.115ms  | 0.048ms  |
| 1 000          | 1 000            | false     | 0.12ms   | 0.11ms   |
| 100 000        | 100 000          | true      | 12.23ms  | 3.99ms   |
| 100 000        | 100 000          | false     | 10.39ms  | 1.31ms   |
| 1 000 000      | 1 000 000        | true      | 276.61ms | 72.89ms  |
| 1 000 000      | 1 000 000        | false     | 253.06ms | 57.38ms  |

### Skew Heap

| Elements Count | Operations Count | Recursive | Mean      | Std-Dev |
|----------------|------------------|-----------|-----------|---------|
| 1 000          | 1 000            | true      | 0.314ms   | 0.041ms |
| 1 000          | 1 000            | false     | 0.305ms   | 0.074ms |
| 100 000        | 100 000          | true      | 43.06ms   | 17.47ms |
| 100 000        | 100 000          | false     | 37.45ms   | 1.55ms  |
| 1 000 000      | 1 000 000        | true      | 1069.40ms | 76.59ms |
| 1 000 000      | 1 000 000        | false     | 1061.73ms | 55.62ms |

**Mean** and **Std-Dev** are calculated from 100 individual runs for each row.<br>

## Shortest Path

Algorithm is benched using **coherent** graphs. 

### A* with Skew Heap

| Nodes       | Edges        | Mean         | Std-Dev       |
|-------------|--------------|--------------|---------------|
| 10 000      | 20 051       | 7.39ms       | 5.39ms        |
| 10 000      | 20 459       | 6.59ms       | 2.83ms        |
| 10 000      | 21 965       | 3.41ms       | 1.58ms        |
| 10 000      | 100 000      | 6.55ms       | 5.48ms        |
| 1 000 000   | 2 000 047    | 704.40ms     | 415.53ms      |
| 1 000 000   | 2 000 506    | 902.92ms     | 371.97ms      |
| 1 000 000   | 2 004 911    | 725.80ms     | 489.88ms      |
| 1 000 000   | 2 045 257    | 716.39ms     | 479.79ms      |
| 5 000 000   | 10 409 179   | 6084.92ms    | 3268.69ms     |

### A* with Pairing Heap

| Nodes       | Edges        | Mean         | Std-Dev       |
|-------------|--------------|--------------|---------------|
| 10 000      | 20 051       | 8.69ms       | 5.91ms        |
| 10 000      | 20 459       | 6.39ms       | 3.03ms        |
| 10 000      | 21 965       | 5.97ms       | 3.10ms        |
| 10 000      | 100 000      | 5.85ms       | 4.87ms        |
| 1 000 000   | 2 000 047    | 696.22ms     | 365.18ms      |
| 1 000 000   | 2 000 506    | 887.30ms     | 430.49ms      |
| 1 000 000   | 2 004 911    | 759.19ms     | 466.33ms      |
| 1 000 000   | 2 045 257    | 801.39ms     | 524.92ms      |
| 5 000 000   | 10 409 179   | 6058.80ms    | 3286.14ms     |


**Mean** and **Std-Dev** are calculated from 10 individual runs for each row
choosing each time 2 random nodes.<br>

