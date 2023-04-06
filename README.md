## cloudlet-scheduler
Simulation experiments using CloudSim5.0 toolkit to verify the optimization effect of WOA algorithm and HWGA algorithm using Random algorithm, FCFS algorithm and Min-Min algorithm as benchmarks

### Introduction
- ga: Genetic Algorithm
- [woa](https://github.com/LA4AM12/WOA): Whale Optimization Algorithm
- hwga: Hybrid Whale Genetic Algorithm
- random: Random allocation algorithm
- fcfs: First Come First Served algorithm
- minmin: Min-Min algorithm
- maxmin: Max-Min algorithm

### Benchmarks
#### Single-objective optimization
makespan using different algorithms for different task volumes:
![img.png](.github/images/img1.png)


Load balancing degree under different task volumes using different algorithms:
![img.png](.github/images/img2.png)


Execution time of virtual machines using different algorithms:
![img.png](.github/images/img3.png)

#### Multi-objective optimization
Comparison of fitness values of WOA and HWGA algorithms under different number of iterations:
![img.png](.github/images/img4.png)


Comparison of fitness values of WOA and HWGA algorithms at different population sizes:
![img.png](.github/images/img5.png)