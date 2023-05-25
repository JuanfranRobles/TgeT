# TgeT
TgeT is a project that combines evolutionary single and multi-objective optimization algorithms, social network analysis and agent-based modeling to detect influential consumers and use them in viral marketing campaigns to maximize product adoption.

TgeT is a research project developed to carry out several research tasks during the course of my doctoral thesis. Mainly, the software makes use of:

- An agent-based model focused on consumption adoption proposed by Marco Jannsen and Wagner Jagger in [[1]](https://direct.mit.edu/artl/article-abstract/9/4/343/2440/Simulating-Market-Dynamics-Interactions-between) and [[2]](https://www.rug.nl/staff/w.jager/jager_janssen_eccs_2012.pdf). This model was improved through the addition of brand awareness and word-of-mouth information exchange processes to make the model more realistic in this [paper](https://ieeexplore.ieee.org/abstract/document/7748346). 
- A social network framework for representing connections and relationships between consumer agents. This module uses [GraphStream](https://graphstream-project.org/) as social network API.
- Evolutionary single and multi-objective algorithms in charge of identifying the best set of influential nodes to use during viral marketing campaings for maximizing consumption adoption for a custom brand. The aforementioned modules were used for research and the findings were published in this [paper](https://www.sciencedirect.com/science/article/abs/pii/S0957417420300099). 

This software is distributed under Creative Commons license.
