## Installation Steps

## Requirements
- Java 21
## Dataset Generation
### Execution parameters
- r: [optional] (boolean) a flag used to indicate that a new dataset file should be made. If not present then the existing file will be extended
- p: [optional] (String) the path to the location for creation of the dataset and csp file. If not present, this will be created ```System.getProperty("user.home")``` under the title "CSPMGraphSynthesis"
- d: [optional] (boolean) a flag to indicate whether to include decorations in CSP channels. These are used to pass parameters along the channels
- b: [required] (Integer) the number of basic graphs generate that form a basis of the complex CSP models
- c: [required] (Integer) the number of complex graphs to create based on the basic graphs

  
## Step-by-step Replication
### Dataset creation
- Hosted dataset:
- Parameters used to create a new training dataset.
  ``` -b 50 -c 200 -p [local filepath] -r```  
- To build the dataset, the following parameters were used
  ``` -b 50 -c 200 -p [local filepath]```  
 
### Dataset refinement
Manual refinement was required to balance the generated dataset for use in initial training.  
The generated examples for CSP are weighted heavily towards deadlocked models, resulting in the dataset consisting of more failure cases when verifying against a deadlock-free assertion. 

### Further details
The creation of deadlock-free examples was at a rate of 126 to every 10000 failing cases.  
For every CSP file generated, multiple assertions may be specified. In the generation of this dataset, ~43,000 assertions were defined across ~3650 CSP files for an average of 12 assertions per file.  
Of the ~3680 CSP files generated, ~295 of these had passing assertions; indicating an 8% chance of files including deadlock-free processes. 

To guarantee enough examples to create a balanced dataset of 1000 entries (500 passing:500 failing), approximately 50000 assertions would need creating.  
This is achievable through parameters:   
``` -b 300 -c 4000 ```
     
